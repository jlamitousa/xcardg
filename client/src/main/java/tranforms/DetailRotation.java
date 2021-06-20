package tranforms;

import javafx.scene.transform.Rotate;

public class DetailRotation extends Rotate { 
	
	public DetailRotation(double degreesAngle, double x, double y) {
		setAngle(degreesAngle);
		setPivotX(x);
		setPivotY(y);
	}
	
}
