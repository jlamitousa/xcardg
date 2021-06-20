package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientRunnable implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger("ClientRunnable");
	
	protected PrintWriter out;
	protected BufferedReader in;
	
	private int id;
	private String deckName;
	private String deckPath;

	private List<ClientRunnable> newUserConOK;
	private List<ClientRunnable> oldUserConOK;
	
	ClientRunnable(Socket client) throws IOException {
		
		out = new PrintWriter(client.getOutputStream(),true);
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
		id = -1;
		deckName = null;
		deckPath = null;
		
		newUserConOK = new ArrayList<ClientRunnable>();
		oldUserConOK = new ArrayList<ClientRunnable>();
		
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getDeckName() {
		return deckName;
	}

	public void setDeckName(String deckName) {
		this.deckName = deckName;
	}
	
	public String getDeckPath() {
		return deckPath;
	}

	public void setDeckPath(String deckPath) {
		this.deckPath = deckPath;
	}

	public List<ClientRunnable> getNewUserConOK() {
		List<ClientRunnable> newUserConOKCpy = new ArrayList<>(newUserConOK);
		return newUserConOKCpy;
	}

	public void addNewUserConOK(ClientRunnable cr) {
		this.newUserConOK.add(cr);
	}
	
	public void removeNewUserConOK(ClientRunnable cr) {
		this.newUserConOK.remove(cr);
	}
	
	public List<ClientRunnable> getOldUserConOK() {
		List<ClientRunnable> oldUserConOKCpy = new ArrayList<>(oldUserConOK);
		return oldUserConOKCpy;
	}

	public void addOldUserConOK(ClientRunnable cr) {
		this.oldUserConOK.add(cr);
	}
	
	public void removeOldUserConOK(ClientRunnable cr) {
		this.oldUserConOK.remove(cr);
	}
	
	public PrintWriter getOut() {
		return out;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void sendEvent(String data) {
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("sending: "+data);
		}
		
		synchronized (out) {
			out.append(data+"\n");
			out.flush();	
		}
	}
}
