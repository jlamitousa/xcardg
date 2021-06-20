package tranforms;

import javafx.scene.transform.Translate;
import util.math.Point;

public class UserMoveGlobal extends Translate implements IUserMoveGlobal {
	
	public UserMoveGlobal() {
		this(0, 0);
	}
	
	public UserMoveGlobal(double x, double y) {
		super(x, y);
	}
}
