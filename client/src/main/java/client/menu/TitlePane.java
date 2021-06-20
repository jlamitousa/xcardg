package client.menu;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class TitlePane extends Pane {

	public TitlePane(double width, double height, String title) {

		HBox hbox = new HBox();

		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(new Text(title));
		hbox.setMinWidth(width);
		hbox.setMinHeight(height);
		hbox.setStyle("-fx-border-color: black;");

		this.setMinWidth(width);
		this.setWidth(width);
		this.setMinHeight(height);
		this.setHeight(height);

		getChildren().add(hbox);
	}
}
