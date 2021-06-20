package main;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMasterServer {

	private static final Logger LOGGER = LoggerFactory.getLogger("EventHandler");

	private ServerSocket serverSock;
	private List<Socket> clients;

	private List<Thread> clientThreads;
	private List<ClientRunnable> clientRunnables;

	private Game game;
	private boolean isRunning;

	public GameMasterServer() {
		isRunning = false;
		serverSock = null;
		clients = new ArrayList<Socket>();
		clientThreads = new ArrayList<Thread>();
		clientRunnables = new ArrayList<ClientRunnable>();
	}

	public void startCon(final int port) throws IOException {

		Thread t = null;

		serverSock = new ServerSocket(port);
		clients = new ArrayList<Socket>();
		clientThreads = new ArrayList<Thread>();
		clientRunnables = new ArrayList<ClientRunnable>();

		t = new Thread(new Runnable() {

			public void run(){

				game = new Game();

				isRunning = true;

				LOGGER.info("Event handling started at port '"+port+"' ..");

				while(isRunning){

					try {

						Socket client = null;
						InetSocketAddress sockaddr = null;
						InetAddress inaddr = null;
						Inet4Address in4addr = null;

						LOGGER.info("Waiting for client ..");

						client = serverSock.accept();
						sockaddr = (InetSocketAddress)client.getRemoteSocketAddress();
						inaddr = sockaddr.getAddress();
						in4addr = (Inet4Address)inaddr;

						LOGGER.info("Connection received from '"+in4addr.toString()+"'");

						clients.add(client);

						clientRunnables.add(new ClientRunnable(client) {

							public void run(){

								String line = null;
								Map<String, String> eventData = null;

								try {

									while((line = in.readLine()) != null) {

										if(LOGGER.isDebugEnabled()) {
											LOGGER.debug("event: "+line);
										}

										handleServerMsg(this, unserializeEvent(line));

									}

								}  catch (Exception e) {
									LOGGER.error("", e);
								}

								removePlayer(this);
							}
						});

						clientThreads.add(new Thread(clientRunnables.get(clientRunnables.size()-1)));
						clientThreads.get(clientThreads.size()-1).start();

					} catch (IOException ioe) {
						LOGGER.error("", ioe);
						isRunning = false;
					}
				}

				try {

					serverSock.close();
					LOGGER.info("Event handling finish");

				} catch (IOException ioe) {
					LOGGER.error("Error on close", ioe);
					isRunning = false;
				}
			}
		});

		t.start();
	}

	private void removePlayer(ClientRunnable cr) {

		this.clientRunnables.remove(cr);

		if(this.clientRunnables.size()==0) {

			this.game.reset();

		}
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

	private synchronized void handleServerMsg(ClientRunnable cr, Map<String, String> data) {

		switch(RemoteEvent.valueOf(data.get("eventType"))) {
		case FIRST_CON: {

			handleFirstCon(cr, data);
			break;

		}
		case ID_ASSIGNATION_OK: {

			handleIdAssignOK(cr, data);
			break;

		}
		case NEW_USER_CON_OK: {

			handleNewUserConOK(cr, data);
			break;

		}
		case OLD_USER_CON_OK: {

			handleOldUserConOK(cr, data);
			break;

		}
		case DECK_LOAD: {

			handleDeckLoad(cr, data);
			break;

		}
		case RQ_HANDZONE: {

			handleRequestHandZone(cr, data, false);
			break;

		}
		case RQ_HANDZONE_F: {

			handleRequestHandZone(cr, data, true);
			break;

		}
		case HANDZONE_F: {

			handleHandZone(cr, data, true);
			break;

		}
		case RQ_PICKZONE: {

			handleRequestPickZone(cr, data);
			break;

		}
		case PICKZONE: {

			handlePickZone(cr, data);
			break;

		}
		case SORT: {

			forward(cr, data);
			break;

		}
		case MULLIGAN: {

			forward(cr, data);
			break;

		}
		case PICK: {

			forward(cr, data);
			break;

		}
		case CAPTURE: {

			forward(cr, data);
			break;

		}
		case CTOKEN: {

			forward(cr, data);
			break;

		}	
		case MCLICK:
		case MPRESSED:
		case MDRAGGED:
		case MRELEASE: {

			handleMouseAction(cr, data);
			break;

		}
		default: {

			LOGGER.error("Unknown event type: "+data.get("eventType"));
			break;
		}}

	}

	private void sendGeneralMsg(ClientRunnable source, Map<String, String> data) {

		for(ClientRunnable tmpCr : this.clientRunnables) {

			if(tmpCr.getId() != source.getId()) {
				tmpCr.sendEvent(serializeEvent(data));
			}
		}
	}

	private void handleFirstCon(ClientRunnable cr, Map<String, String> data) {

		Map<String, String> respData = new HashMap<String, String>();
		int id = game.getFreeUserId();

		respData.put("eventType", RemoteEvent.ID_ASSIGNATION.name());
		respData.put("id", id+"");

		cr.setId(id);
		cr.sendEvent(serializeEvent(respData));

	}

	private void handleIdAssignOK(ClientRunnable cr, Map<String, String> data) {

		int id = Integer.parseInt(data.get("id"));
		Map<String, String> respData = new HashMap<String, String>();

		if(id > 0) {

			if(this.clientRunnables.size() > 1) {
				respData.clear();
				respData.put("eventType", RemoteEvent.NEW_USER_CON.name());
				respData.put("id", id+"");
				sendGeneralMsg(cr, respData);
			} else {
				afterAllNewUserCon(cr);
			}
		}

	}

	private void handleNewUserConOK(ClientRunnable cr, Map<String, String> data) {

		int targetId = Integer.parseInt(data.get("target_id"));
		ClientRunnable target = find(targetId);

		if(target != null) {

			target.addNewUserConOK(cr);

			if(haveAllClientRunnable(target, target.getNewUserConOK())) {
				afterAllNewUserCon(target);
			}
		}

	}

	private void afterAllNewUserCon(ClientRunnable cr) {

		Map<String, String> respData = new HashMap<String, String>();

		if(this.clientRunnables.size() > 1) {

			for(ClientRunnable tmpCr : this.clientRunnables) {

				if(tmpCr != cr && tmpCr.getId() > 0) {

					respData.clear();
					respData.put("eventType", RemoteEvent.OLD_USER_CON.name());
					respData.put("id", tmpCr.getId()+"");
					respData.put("deckName", tmpCr.getDeckName());
					respData.put("deckPath", tmpCr.getDeckPath());

					cr.sendEvent(serializeEvent(respData));

				}
			}

		} else {

			afterAllOldUserCon(cr);

		}

	}

	private void handleOldUserConOK(ClientRunnable cr, Map<String, String> data) {

		int targetId = Integer.parseInt(data.get("target_id"));
		ClientRunnable target = find(targetId);

		if(target != null) {

			cr.addOldUserConOK(target);

			if(haveAllClientRunnable(cr, cr.getOldUserConOK())) {
				afterAllOldUserCon(cr);
			}

		}

	}

	private void afterAllOldUserCon(ClientRunnable cr) {

		Map<String, String> respData = new HashMap<String, String>();

		respData.put("eventType", RemoteEvent.CON_OK.name());
		cr.sendEvent(serializeEvent(respData));

	}

	private void handleDeckLoad(ClientRunnable cr, Map<String, String> data) {

		Map<String, String> respData = new HashMap<String, String>();
		String deckName = data.get("deckName");
		String deckPath = data.get("deckPath");
		String order = data.get("order");

		cr.setDeckName(deckName);
		cr.setDeckPath(deckPath);

		respData.put("eventType", RemoteEvent.DECK_LOAD.name());
		respData.put("id", cr.getId()+"");
		respData.put("deckName", deckName);
		respData.put("deckPath", deckPath);
		respData.put("order", order);
		sendGeneralMsg(cr, respData);

	}

	private void handleRequestHandZone(ClientRunnable cr, Map<String, String> data, boolean firstTime) {

		int sourceId = cr.getId();
		int targetId = Integer.parseInt(data.get("target_id"));
		ClientRunnable targetCr = find(targetId);
		Map<String, String> respData = new HashMap<String, String>();
		String eventType = firstTime ? RemoteEvent.RQ_HANDZONE_F.name() : RemoteEvent.RQ_HANDZONE.name();

		respData.put("eventType", eventType);
		respData.put("source_id", sourceId+"");

		targetCr.sendEvent(serializeEvent(respData));

	}

	private void handleHandZone(ClientRunnable cr, Map<String, String> data, boolean firstTime) {

		int sourceId = cr.getId();
		int targetId = Integer.parseInt(data.get("target_id"));
		ClientRunnable targetCr = find(targetId);
		Map<String, String> respData = new HashMap<String, String>();
		String eventType = firstTime ? RemoteEvent.HANDZONE_F.name() : RemoteEvent.HANDZONE.name();

		respData.put("eventType", eventType);
		respData.put("source_id", sourceId+"");
		respData.put("hand", data.get("hand"));

		if(targetCr == null) {
			sendGeneralMsg(cr, respData);
		} else {
			targetCr.sendEvent(serializeEvent(respData));
		}

	}

	private void handleRequestPickZone(ClientRunnable cr, Map<String, String> data) {

		int sourceId = cr.getId();
		int targetId = Integer.parseInt(data.get("target_id"));
		ClientRunnable targetCr = find(targetId);
		Map<String, String> respData = new HashMap<String, String>();

		respData.put("eventType", RemoteEvent.RQ_PICKZONE.name());
		respData.put("source_id", sourceId+"");

		targetCr.sendEvent(serializeEvent(respData));

	}

	private void handlePickZone(ClientRunnable cr, Map<String, String> data) {

		int sourceId = cr.getId();
		int targetId = Integer.parseInt(data.get("target_id"));
		ClientRunnable targetCr = find(targetId);
		Map<String, String> respData = new HashMap<String, String>();

		respData.put("eventType", RemoteEvent.PICKZONE.name());
		respData.put("source_id", sourceId+"");
		respData.put("order", data.get("order"));

		if(targetCr == null) {
			sendGeneralMsg(cr, respData);
		} else {
			targetCr.sendEvent(serializeEvent(respData));
		}

	}

	private void forward(ClientRunnable cr, Map<String, String> data) {
		sendGeneralMsg(cr, data);
	}

	private void handleMouseAction(ClientRunnable cr, Map<String, String> data) {
		sendGeneralMsg(cr, data);
	}

	private ClientRunnable find(int id) {

		for(ClientRunnable cr : this.clientRunnables) {
			if(cr.getId()==id) {
				return cr;
			}
		}

		return null;

	}

	private boolean haveAllClientRunnable(ClientRunnable source, List<ClientRunnable> crList) {

		boolean haveAll = true;

		for(ClientRunnable cr : this.clientRunnables) {

			if(cr != source) {

				boolean found = false;

				for(ClientRunnable tmpCr : crList) {

					if(cr.getId() == tmpCr.getId()) {
						found = true;
						break;
					}
				}

				if(found == false) {
					haveAll = false;
					break;
				}

			}
			
		}

		return haveAll;

	}
}
