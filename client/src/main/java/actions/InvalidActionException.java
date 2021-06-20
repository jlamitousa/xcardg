package actions;

public class InvalidActionException extends Exception {

	public InvalidActionException(String msg) {
		super(msg);
	}
	
	public InvalidActionException() {
		super("Invalid action");
	}
}
