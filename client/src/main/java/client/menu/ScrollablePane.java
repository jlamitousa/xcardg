package client.menu;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class ScrollablePane extends ScrollPane {

	private Pane content;
	
	public ScrollablePane(double width, double height) {
		
		this.content = new Pane();
		
		this.setMinWidth(width);
		this.setWidth(width);
		this.setMaxWidth(width);
		this.setMinHeight(height);
		this.setHeight(height);
		this.setMaxHeight(height);
		
		setContent(this.content);
	}
	
	public int getNbActions() {
		return this.content.getChildren().size();
	}
	
	public void clearActions() {
		this.content.getChildren().clear();
	}
	
	public void addChildren(Node n) {
		content.getChildren().add(n);
	}
}
