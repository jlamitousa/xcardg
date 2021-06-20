package client.gi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.InvalidActionException;
import actions.RemoteEvent;

public class RemoteEventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger("EventHandler");
	private static final Object START_CON_LOCK = new Object();
	private static final Lock EVENT_LOCK = new ReentrantLock();

	private boolean started;

	private boolean isRunning;

	private GraphicInterface gi;

	private Socket client;
	private PrintWriter out;
	private BufferedReader in;

	private Thread evenProcessor;
	private List<String> eventQueue;

	public RemoteEventHandler(GraphicInterface gi) {
		this.started = false;
		this.isRunning = false;
		this.gi = gi;
		this.eventQueue = new ArrayList<String>();
	}

	public boolean isConnectedToServer() {
		synchronized (START_CON_LOCK) {
			return client != null;	
		}
	}
	
	public void triggerServerCon() {
		synchronized (START_CON_LOCK) {
			START_CON_LOCK.notify();
		}
	}
	
	public void startClient(final String ip, final int port, GraphicInterface gi) throws IOException, InterruptedException {

		this.isRunning = true;

		Thread t = new Thread(new Runnable(){

			public void run(){

				String line = null;
				boolean stopTry = false;

				try {

					synchronized (START_CON_LOCK) {

						while(client==null && !stopTry) {

							try {
								client = new Socket(ip, port);
							} catch(ConnectException ce) {
								LOGGER.error("Server not ready.", ce);
							}

							if(client==null) {
								try {
									START_CON_LOCK.wait();
								} catch(InterruptedException ie) {
									LOGGER.error("", ie);
									isRunning = false;
									stopTry = true;
								}
							}

						}
					}


					out = new PrintWriter(client.getOutputStream(),true);
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));

					evenProcessor = new Thread(new Runnable() {

						@Override
						public void run() {

							while(isRunning) {

								String nextEvent = null;

								synchronized (eventQueue) {

									if(!eventQueue.isEmpty()) {
										nextEvent = eventQueue.get(0);
										eventQueue.remove(0);
									}

									if(nextEvent==null) {
										try {
											eventQueue.wait();
										} catch(InterruptedException ie) {
											LOGGER.error("", ie);
											isRunning = false;
										} 
									}
								}

								if(nextEvent != null) {

									Map<String, String> data = unserializeEvent(nextEvent);
									boolean isHandShake = 
											RemoteEvent.ID_ASSIGNATION.name().equals(data.get("eventType")) || 
											RemoteEvent.OLD_USER_CON.name().equals(data.get("eventType")) ||
											RemoteEvent.HANDZONE_F.name().equals(data.get("eventType")) || 
											RemoteEvent.CON_OK.name().equals(data.get("eventType"));

									try {

										EVENT_LOCK.lock();

										if(isHandShake || gi.firstConDone()) {

											if(LOGGER.isDebugEnabled()) {
												LOGGER.debug("Processing event: "+nextEvent);
											}

											gi.handleServerMsg(data);

										} else {

											if(LOGGER.isDebugEnabled()) {
												LOGGER.debug("Dropped event: "+nextEvent);
											}

										}

										EVENT_LOCK.unlock();

									} catch(InvalidActionException iae) {
										LOGGER.error("Invalid action", iae);
										isRunning = false;
										try {
											client.close();
										} catch(IOException ioe) {
											LOGGER.error("", ioe);
										}
									} 

								}

							}

						}
					});

					evenProcessor.start();

					LOGGER.info("Event handling started ..");

					started = true;

					while((line = in.readLine()) != null && isRunning) {

						if(LOGGER.isDebugEnabled()) {
							LOGGER.debug("event: "+line);
						}

						synchronized (eventQueue) {
							eventQueue.add(line);
							eventQueue.notifyAll();
						}

					}

				}  catch (IOException ioe) {
					LOGGER.error("", ioe);
					isRunning = false;
				}

				LOGGER.debug("No more data to read .. connexion closed .. ");

				try {
					client.close();
				} catch (IOException ioe) {
					LOGGER.error("", ioe);
				}
			}
		});

		t.setDaemon(true);
		t.start();

		while(started==false) {
			LOGGER.debug("Waiting start ..");
			Thread.sleep(2000);
		}

		LOGGER.info("Start finish ..");
	}

	private Map<String, String> unserializeEvent(String line) {

		Map<String, String> data = new HashMap<String, String>();

		for(String parts : line.split(";")) {
			String[] nameAndVal = parts.split(":");
			data.put(nameAndVal[0], nameAndVal[1]);
		}

		return data;
	}

	private String serializeEvent(Map<String, String> data) {

		StringBuilder eventB = new StringBuilder();
		Iterator<Map.Entry<String, String>> dataIt = data.entrySet().iterator();
		Map.Entry<String, String> oneData = null;

		while(dataIt.hasNext()){
			oneData = dataIt.next();
			eventB.append(oneData.getKey()+":"+oneData.getValue()+(dataIt.hasNext() ? ";" : ""));
		}

		return eventB.toString();
	}

	public void sendEvent(Map<String, String> data) {

		String serialized = null;

		EVENT_LOCK.lock();

		serialized = serializeEvent(data);

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("sending: "+serialized);
		}

		out.append(serialized+"\n");
		out.flush();
		EVENT_LOCK.unlock();
	}

	public void blockEvents() {
		EVENT_LOCK.lock();
		LOGGER.debug("Events blocked .. ");
	}

	public void unblockEvents() {
		EVENT_LOCK.unlock();
		LOGGER.debug("Events unblocked .. ");
	}

	private String createHelloWorldMsg() {
		Map<String, String> data = new HashMap<String, String>();

		data.put("hello", "world");

		return serializeEvent(data);
	}

	public void sendHelloWorldMsg() {

		Map<String, String> data = new HashMap<String, String>();

		data.put("hello", "world");

		sendEvent(data);
	}
}
