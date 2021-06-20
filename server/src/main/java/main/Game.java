package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("Game");
	
	List<Integer> availableId;
	int spectatorFreeId = -1;
	
	public Game() {
		
		availableId = new ArrayList<Integer>();
		
		reset();
	
	}
	
	public int getFreeUserId() {
		
		int userId = -1;
		
		if(availableId.size() > 0) {
			
			userId = availableId.get(0);
			availableId.remove(0);
		
		} else {
			userId = --spectatorFreeId;
		}
		
		return userId;
	}
	
	public void reset() {

		availableId.clear();
		availableId.add(1);
		availableId.add(2);
		availableId.add(3);
		availableId.add(4);

		spectatorFreeId = -1;
		
		LOGGER.info("Game reseted");
	}
}
