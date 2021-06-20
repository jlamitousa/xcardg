package tranforms;

import javafx.scene.transform.Rotate;

public class InitRotation extends Rotate { 
	
	public InitRotation(double degreeAngle, double x, double y) {
		setAngle(degreeAngle);
		setPivotX(x);
		setPivotY(y);
	}
	
}
