package client.gi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import actions.InvalidActionException;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

class PileCardState {
	
	boolean isReturnedMode;
	
	PileCardState(boolean isReturnedMode) {
		this.isReturnedMode = isReturnedMode;
	}
}

public class PileDisplayer extends ScrollPane {

	private static final double CARD_WIDHT_PERCENT = 0.15;
	private static final double CARD_HEIGHT_PERCENT = 0.3;
	private static final double SPACE_BETWEEN_CARD_X = 0.05;
	private static final double SPACE_BETWEEN_CARD_Y = 0.05;

	private double width;
	private double height;

	private Pane content;
	private Pile pile;
	private List<Card> pileCnt;
	private List<PileCardState> cardStates;
	private int nbCardsByLine;
	private double cardWidth;
	private double cardHeight;
	private double spaceX;
	private double spaceY;

	public PileDisplayer(double width, double height) {

		this.content = new Pane();

		this.width = width;
		this.height = height;

		this.setMinWidth(width);
		this.setMaxWidth(width);
		this.setWidth(width);
		this.setMinHeight(height);
		this.setHeight(height);
		this.setMaxHeight(height);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setPannable(true);


		this.cardWidth = width*CARD_WIDHT_PERCENT;
		this.cardHeight = height*CARD_HEIGHT_PERCENT;
		this.spaceX = width*SPACE_BETWEEN_CARD_X;
		this.spaceY = height*SPACE_BETWEEN_CARD_Y;

		this.nbCardsByLine = (int) (width / (this.cardWidth+this.spaceX) -1);

		setContent(this.content);
	}

	public void clear() throws InvalidActionException {

		this.content.getChildren().clear();

		if(pileCnt != null) {
			for(int i = pileCnt.size()-1; i >= 0; i--) {
				
				Card c = pileCnt.get(i);
				PileCardState cs = cardStates.get(i);
				
				c.setReturnedMode(cs.isReturnedMode);
				
				pile.captureCard(c, false);
			}
			
			pile.refreshMenuData();
		}

		this.pile = null;		
	}

	public void remove(Card c) {
		
		int pos = -1;
		
		for(int i = 0; i <  pileCnt.size(); i++) {
			if(c==this.pileCnt.get(i)) {
				pos = i;
				break;
			}
		}
		
		if(pos != -1) {
			this.pileCnt.remove(pos);
			this.cardStates.remove(pos);
		}
		
		displayCnt();
		
	}
	
	public void setPile(Pile pile, int nb) {

		this.pile = pile;
		
		this.pileCnt = 
				pile
				.get(nb)
				.stream()
				.filter(c->!c.isToken())
				.collect(Collectors.toList());
		
		this.cardStates = new ArrayList<PileCardState>(pileCnt.size());
	
		displayCnt();
		
	}
	
	private void displayCnt() {
		
		this.content.getChildren().clear();
		
		for(int i = 0; i < pileCnt.size(); i++) {

			Card c = pileCnt.get(i);

			int line = (i / this.nbCardsByLine);
			int column = (i % this.nbCardsByLine);

			this.cardStates.add(new PileCardState(c.isReturnedMode()));
			
			c.setReturnedMode(false);
			c.setWidth(this.cardWidth);
			c.setHeight(this.cardHeight);
			c.setX(this.spaceX + (this.cardWidth+this.spaceX)*column);
			c.setY(this.spaceY + (this.cardHeight+this.spaceY)*line);

			this.content.getChildren().add(c);
			
		}

	}
}
