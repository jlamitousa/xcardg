package tranforms;

import javafx.scene.transform.Translate;

public class UserMoveGlobalReversed extends Translate implements ReversedTranslation, IUserMoveGlobal {
	
	public UserMoveGlobalReversed() {
		this(0, 0);
	}
	
	public UserMoveGlobalReversed(double x, double y) {
		super(x, y);
	}
}
