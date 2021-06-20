package tranforms;

import javafx.scene.transform.Rotate;

public class InitReversedRotation extends Rotate { 
	
	public InitReversedRotation(double degreesAngle, double x, double y) {
		setAngle(degreesAngle);
		setPivotX(x);
		setPivotY(y);
	}
	
}
