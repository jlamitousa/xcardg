package tranforms;

import javafx.scene.transform.Translate;

public class DetailReversedTranslation extends Translate implements ReversedTranslation {

	public DetailReversedTranslation(double x, double y) {
		super(x, y);
	}
}