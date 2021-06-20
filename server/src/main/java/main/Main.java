package main;

import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		GameMasterServer eHandler = new GameMasterServer();
		
		eHandler.startCon(4546);
	}
	
}
