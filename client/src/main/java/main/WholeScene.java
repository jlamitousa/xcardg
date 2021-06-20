package main;

import client.menu.Menu;
import client.gi.GraphicInterface;
import javafx.scene.layout.Pane;

public class WholeScene extends Pane {

	public WholeScene(double width, double height, GraphicInterface gi, Menu menu) {
		
		StringBuilder styles = new StringBuilder();
		
		this.setWidth(width);
		this.setHeight(height);
		
		this.getChildren().add(gi);
		this.getChildren().add(menu);
		
		styles.append("-fx-border-radius: 20;");
		styles.append("-fx-border-width: 5;");
		
	}
}
