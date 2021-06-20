package tranforms;

import javafx.scene.transform.Rotate;

/**
 * Engage/Degage rotation
 * @author jean-luc.amitousa
 *
 */
public class EDRotation extends Rotate {

	public EDRotation(double degreeAngle, double x, double y) {
		setAngle(degreeAngle);
		setPivotX(x);
		setPivotY(y);
	}
}
