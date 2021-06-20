package client.menu;

import javafx.scene.layout.Pane;

public class SimplePane extends Pane {

	public SimplePane() { }
	
	public SimplePane(double width, double height) {
		setSize(width, height);
	}
	
	public void setSize(double width, double height) {
		this.setWidth(width);
		this.setHeight(height);
	}
}
