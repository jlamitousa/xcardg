package client.gi;

import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import main.Main;

public class ImageDisplayer extends Pane {

	private double width;
	private double height;

	public ImageDisplayer(double width, double height, String imgPath) {

		String bckUrl = Main.getResourceUrl(imgPath);
		BackgroundImage bckImg = new BackgroundImage(new Image(bckUrl, width, height, false, true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		
		this.width = width;
		this.height = height;

		this.setMinWidth(width);
		this.setMaxWidth(width);
		this.setWidth(width);
		this.setMinHeight(height);
		this.setHeight(height);
		this.setMaxHeight(height);
		
		setBackground(new Background(bckImg));
	
	}
	 
}
