package client.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.InvalidActionException;
import client.gi.CardState;
import client.gi.GraphicInterface;
import client.gi.HandZone;
import client.gi.PickZone;
import client.gi.PileDisplayer;
import client.gi.PlayerState;
import client.gi.PlayerZone;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import main.DeckLoader;
import main.Main;

public class Menu extends Pane {

	private static final Logger LOGGER = LoggerFactory.getLogger("Menu");

	private static final String BCK_URL = "/background-menu-default.jpg";

	private static final List<Map<String, String>> ALL_DECKS = new ArrayList<Map<String,String>>();

	private static final double ACTION_BUTTON_START_X = 10.0 /100.0;
	private static final double INFOS_TABLE_PREF_HEIGHT = 20.0 / 100.0;
	private static final double GENERAL_VIEW_HEIGHT_PERCENT = 57.0 / 100.0;
	private static final double CONTEXT_VIEW_HEIGHT_PERCENT = GENERAL_VIEW_HEIGHT_PERCENT;
	private static final double ACTION_VIEW_HEIGHT_PERCENT = GENERAL_VIEW_HEIGHT_PERCENT;
	private static final double ACTION_PADDING_X = 2.0 /100.0;
	private static final double SPACE_FOR_ACTION_Y = 5.0 / 100.0;
	private static final double SPACE_FOR_TEXT_Y = 4.0 / 100.0;
	private static final double SPACE_FOR_USER_TEXT_Y = 15.0/100.0;
	private static final double ESTIMATED_INFO_HEIGHT = 5.5/100.0;

	private static final int SERVER_UPCOM_WAIT = 5;
	private static final int USER_COME_DURATION_SEC = 5;

	private double width;
	private double height;

	private Accordion fullMenu;
	private List<HBox> generaleActions;
	private List<HBox> generaleInGameActions;
	private ScrollablePane generalMenu;
	private TitledPane generalMenuPane;
	private ScrollablePane actionsMenu;
	private TitledPane actionsMenuPane;
	private Accordion usersInfoTable;
	private TitledPane cardStatePane;

	private GraphicInterface gi;
	private CardState currentCardState;
	
	
	static {
		
		Map<String, String> edgarMarkov = new HashMap<>();
		Map<String, String> eightAndAHalfTails = new HashMap<>();
		Map<String, String> aminatou = new HashMap<>();
		
		edgarMarkov.put("deckName", "edgar-markov");
		edgarMarkov.put("deckFile", "maxime/edgar-markov-s-feast.txt");
		ALL_DECKS.add(edgarMarkov);
		
		eightAndAHalfTails.put("deckName", "eight-and-a-half-tails");
		eightAndAHalfTails.put("deckFile", "romain/eight-and-a-half-tails.txt");
		ALL_DECKS.add(eightAndAHalfTails);
		
		aminatou.put("deckName", "aminatou-the-fateshifter");
		aminatou.put("deckFile", "youssef/aminatou-the-fateshifter.txt");
		ALL_DECKS.add(aminatou);
		
	}

	public Menu(double startX, double startY, double width, double height) {

		String bckFullUrl = Main.getResourceDirURL() + BCK_URL;
		BackgroundImage bckImg = new BackgroundImage(new Image(bckFullUrl, width, height, false, true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);


		this.width = width;
		this.height = height;
		this.generaleActions = new ArrayList<HBox>();
		this.generaleInGameActions = new ArrayList<HBox>();

		this.setLayoutX(startX);
		this.setLayoutY(startY);

		this.setWidth(width);
		this.setMinWidth(width);
		this.setMaxWidth(width);

		this.setHeight(height);
		this.setMinHeight(height);
		this.setMaxHeight(height);

		setBackground(new Background(bckImg));

		this.gi = null;
		this.currentCardState = null;
	}

	public GraphicInterface getGi() {
		return gi;
	}

	public void setGi(GraphicInterface gi) {
		this.gi = gi;
	}

	public void buildView() {

		fullMenu = new Accordion();

		SimplePane accordeon1 = new SimplePane(width, height);
		generalMenu = new ScrollablePane(width, height*GENERAL_VIEW_HEIGHT_PERCENT);

		SimplePane accordeon2 = new SimplePane(width, height);
		ScrollablePane contextMenu = new ScrollablePane(width, height*CONTEXT_VIEW_HEIGHT_PERCENT);

		SimplePane accordeon3 = new SimplePane(width, height);
		TitledPane titledPane3 = null;
		actionsMenu = new ScrollablePane(width, height*ACTION_VIEW_HEIGHT_PERCENT);




		generalMenu.setLayoutX(0);
		generalMenu.setLayoutY(0);
		createGeneralView();
		usersInfoTable = new Accordion();
		generalMenu.addChildren(usersInfoTable);
		refreshUsersInfosTable();

		contextMenu.setLayoutX(0);
		contextMenu.setLayoutY(0);

		actionsMenu.setLayoutX(0);
		actionsMenu.setLayoutY(0);
		createGeneralInGameActions();


		accordeon1.getChildren().add(generalMenu);
		generalMenuPane = new TitledPane("Général", accordeon1);
		fullMenu.getPanes().add(generalMenuPane);

		accordeon2.getChildren().add(actionsMenu);
		actionsMenuPane = new TitledPane("Actions", accordeon2);
		fullMenu.getPanes().add(actionsMenuPane);

		accordeon3.getChildren().add(contextMenu);
		titledPane3 = new TitledPane("Context", accordeon3);
		fullMenu.getPanes().add(titledPane3);


		fullMenu.setExpandedPane(generalMenuPane);

		getChildren().add(fullMenu);
		
	}


	private void createGeneralView() {

		int index = 0;


		for(Map<String, String> deck : ALL_DECKS) {
			
			String deckName = deck.get("deckName");
			String deckFile = deck.get("deckFile");
			
			generaleActions.add(createAction(generalMenu, "Charger '"+deckName+"'", "OK", new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					if(!gi.isEventBlocked()) {

						try {

							HandZone handZone = gi.getHandZone();
							PlayerZone playerZone = gi.getGmZone().getPlayerZone1();
							int id = playerZone.getUserId();

							while(!gi.isConnectedToServer()) {
								gi.triggerServerCon();
								Thread.sleep(SERVER_UPCOM_WAIT*1000);
							}

							if(id <= 0 && !gi.firstConDone()) {

								gi.sendServerFirstCon();

								runLater(new UICallback() {

									@Override
									public void waitData() throws Exception {
										gi.waitFirstConDone();
									}

									@Override
									public void callback() throws Exception {
										postConDeckLoad(playerZone, handZone, deckFile, deckName);
									}
								});

							} else {

								postConDeckLoad(playerZone, handZone, deckFile, deckName);

							}

						} catch(Exception e) {
							LOGGER.error("Error on loading deck", e);
						}

					}

				}
			}));
			generalMenu.addChildren(generaleActions.get(index++));
			
		}
		
		/*
		generaleActions.add(createAction(generalMenu, "Reinitialiser le jeu", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					try {
						gi.reset();
						refreshUsersInfosTable();
					} catch(Exception e) {
						LOGGER.error("Error on loading deck", e);
					}

				}
			}
		}));
		generalMenu.addChildren(generaleActions.get(index++));
		*/

	}


	private void createGeneralInGameActions() {

		int index = 0;

		generaleInGameActions.add(createAction(actionsMenu, "Mélange", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					PlayerZone playerZone = gi.getGmZone().getPlayerZone1();

					try {
						playerZone.getPickZone().sort();
						gi.sendSort();
						refreshUsersInfosTable();
					} catch(InvalidActionException iae) {
						LOGGER.error("Invalid action", iae);
					}

				}

			}
		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));
		
		generaleInGameActions.add(createAction(actionsMenu, "Mulligan", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					HandZone handZone = gi.getHandZone();
					PlayerZone playerZone = gi.getGmZone().getPlayerZone1();

					try {
						handZone.putHandInPickZone(playerZone);
						playerZone.getPickZone().sort();
						gi.sendMulligan();
						refreshUsersInfosTable();
					} catch(InvalidActionException iae) {
						LOGGER.error("Invalid action", iae);
					}

				}

			}
		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));

		generaleInGameActions.add(createAction(actionsMenu, "Prendre 7 cartes", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					HandZone handZone = gi.getHandZone();
					PlayerZone playerZone = gi.getGmZone().getPlayerZone1();
					PickZone pickZone = playerZone.getPickZone();

					try {
						getXCard(pickZone, handZone, 7, true);
						refreshUsersInfosTable();
					} catch(InvalidActionException iae) {
						LOGGER.error("Invalid action", iae);
					}

				}

			}
		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));

		generaleInGameActions.add(createAction(actionsMenu, "Regard 1", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					try {
						gi.displayPile(gi.getGmZone().getPlayerZone1().getPickZone(), 1);
						refreshUsersInfosTable();
					} catch(InvalidActionException iae) {
						LOGGER.error("Invalid action", iae);
					}

				}

			}
		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));

		generaleInGameActions.add(createAction(actionsMenu, "Regard 2", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					try {
						gi.displayPile(gi.getGmZone().getPlayerZone1().getPickZone(), 2);
						refreshUsersInfosTable();
					} catch(InvalidActionException iae) {
						LOGGER.error("Invalid action", iae);
					}

				}

			}
		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));

		generaleInGameActions.add(createAction(actionsMenu, "Regard 3", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					try {
						gi.displayPile(gi.getGmZone().getPlayerZone1().getPickZone(), 3);
						refreshUsersInfosTable();
					} catch(InvalidActionException iae) {
						LOGGER.error("Invalid action", iae);
					}

				}

			}
		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));

		generaleInGameActions.add(createAction(actionsMenu, "Dégager tout", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					HandZone handZone = gi.getHandZone();
					PlayerZone playerZone = gi.getGmZone().getPlayerZone1();

					playerZone.degageAll();
					refreshUsersInfosTable();

				}

			}

		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));

		generaleInGameActions.add(createAction(actionsMenu, "Créer Token", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(!gi.isEventBlocked()) {

					try {
						
						gi.getGmZone().getPlayerZone1().createToken(false);
						gi.sendCreateToken();
						
					} catch(InvalidActionException iae) {
						LOGGER.error("Invalid action", iae);
					}

					refreshUsersInfosTable();

				}

			}

		}));
		actionsMenu.addChildren(generaleInGameActions.get(index++));
		
	}

	private void runLater(UICallback uiCallback) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					uiCallback.waitData();

					Platform.runLater(new Runnable() {

						@Override
						public void run() {

							try {
								uiCallback.callback();
							} catch(Exception e) {
								LOGGER.error("", e);
							}

						}

					});

				} catch(Exception e) {
					LOGGER.error("", e);
				}

			}

		}).start();

	}

	private void postConDeckLoad(PlayerZone playerZone, HandZone handZone, String deckPath, String deckName) throws Exception {

		int id = playerZone.getUserId();
		PickZone pickZone = playerZone.getPickZone();

		if(id > 0) {

			handZone.reset();
			playerZone.reset();
			DeckLoader.loadDeck(playerZone, handZone, deckPath, deckName);
			pickZone.sort();
			gi.sendServerDeckLoad(id, deckPath, deckName, pickZone.serialize());
			getXCard(pickZone, handZone, 7, false);
			gi.displayImg("/"+deckName+".png", USER_COME_DURATION_SEC);

			refreshUsersInfosTable();

		}

	}

	private void createActions(PileDisplayer pileDisplayer) {

		List<HBox> otherActions = new ArrayList<>();		
		int index = 0;

		actionsMenu.clearActions();
		createGeneralInGameActions();

		otherActions.add(createAction(actionsMenu, "Fermer la vue", "OK", new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					gi.displayGame();
					actionsMenu.clearActions();
					createGeneralInGameActions();
					refreshUsersInfosTable();
					fullMenu.setExpandedPane(generalMenuPane);
					
				} catch(InvalidActionException iae) {
					LOGGER.error("Invalid action", iae);
				}
			}
		}));
		actionsMenu.addChildren(otherActions.get(index++));

	}

	private HBox createAction(ScrollablePane actionsMenu, String actionLabel, String buttonLabel, EventHandler<ActionEvent> eventHandler) {

		int nbActions = actionsMenu.getNbActions();
		HBox hbox = new HBox();
		Text label = new Text(actionLabel+": ");
		Button action = new Button(buttonLabel);

		action.setOnAction(eventHandler);

		action.setLayoutX(label.getX()+ACTION_BUTTON_START_X*width);

		hbox.getChildren().add(label);
		hbox.getChildren().add(action);
		hbox.setLayoutX(width*ACTION_PADDING_X);
		hbox.setLayoutY(nbActions*SPACE_FOR_ACTION_Y*height);

		return hbox;
	}

	public void handleFocus(PileDisplayer pileDisplayer) {
		createActions(pileDisplayer);
		fullMenu.setExpandedPane(actionsMenuPane);
	}

	private void getXCard(PickZone pickZone, HandZone handZone, int count, boolean sendRemote) throws InvalidActionException {

		for(int i = 0; i < Math.min(count, pickZone.getPileSize()); i++) {
			
			handZone.captureCard(pickZone.getFirst(), false);
			
			if(sendRemote) {
				gi.sendPick();
			}
		}

		handZone.refreshMenuData();
	}

	public void refreshUsersInfosTable() {

		TitledPane oldTitlePane = usersInfoTable.getExpandedPane();
		PlayerState ps1 = gi.getRelativePlayerState(1);
		PlayerState ps2 = gi.getRelativePlayerState(2);
		PlayerState ps3 = gi.getRelativePlayerState(3);
		PlayerState ps4 = gi.getRelativePlayerState(4);

		TitledPane titledPane = null;

		double minimalY = generalMenu.getNbActions()*SPACE_FOR_ACTION_Y*height;

		usersInfoTable.getPanes().clear();

		if(ps1 != null && !ps1.getName().equals("none")) {
			titledPane = new TitledPane(ps1.getName(), createUserInfoTable(ps1));
			usersInfoTable.getPanes().add(titledPane);
		}
		if(ps2 != null && !ps2.getName().equals("none")) {
			titledPane = new TitledPane(ps2.getName(), createUserInfoTable(ps2));
			usersInfoTable.getPanes().add(titledPane);
		}
		if(ps3 != null && !ps3.getName().equals("none")) {
			titledPane = new TitledPane(ps3.getName(), createUserInfoTable(ps3));
			usersInfoTable.getPanes().add(titledPane);
		}
		if(ps4 != null && !ps4.getName().equals("none")) {
			titledPane = new TitledPane(ps4.getName(), createUserInfoTable(ps4));
			usersInfoTable.getPanes().add(titledPane);
		}

		if(this.currentCardState != null) {
			this.cardStatePane = createCardState();
			usersInfoTable.getPanes().add(this.cardStatePane);
		}
		
		usersInfoTable.setLayoutY(minimalY);

		if(oldTitlePane != null && findPane(usersInfoTable, oldTitlePane.getText()) != null) {

			usersInfoTable.setExpandedPane( findPane(usersInfoTable, oldTitlePane.getText()));
		}
	}

	public void setCardState(CardState cardState) {
		this.currentCardState = cardState;
		this.cardStatePane = null;
		refreshUsersInfosTable();
		
		if(this.cardStatePane != null) {
			usersInfoTable.setExpandedPane(this.cardStatePane);
		}

	}
	
	public TitledPane createCardState() {

		ObservableList<CardInfoData> data = null;
		TitledPane titledPane = null;
		
		TableView cardInfoTable = new TableView();
		List<TableColumn> columns = new ArrayList<>();
		TableColumn infoColumn = new TableColumn("INFOS");
		TableColumn valueColumn = new TableColumn("VALUE");
		



		infoColumn.setCellValueFactory(new PropertyValueFactory<CardInfoData, String>("infoName"));
		columns.add(infoColumn);
		valueColumn.setCellValueFactory(new PropertyValueFactory<CardInfoData, String>("infoValue"));
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setOnEditCommit(
				new EventHandler<CellEditEvent<CardInfoData, String>>() {

					@Override
					public void handle(CellEditEvent<CardInfoData, String> t) {
						((CardInfoData) t.getTableView().getItems().get(
								t.getTablePosition().getRow())
								).setInfoValue(t.getNewValue());
						refreshUsersInfosTable();
					}
				}
				);
		columns.add(valueColumn);

		cardInfoTable.getColumns().addAll(columns);

		
		data = FXCollections.observableArrayList(
				new CardInfoData(CardInfo.ATK, this.currentCardState),
				new CardInfoData(CardInfo.DEF, this.currentCardState));

		cardInfoTable.setItems(data);
		cardInfoTable.setEditable(true);
		cardInfoTable.setPrefHeight(height*ESTIMATED_INFO_HEIGHT*data.size());
		cardInfoTable.setPrefWidth(width);

		return new TitledPane("(Card) - "+this.currentCardState.getCardName(), cardInfoTable);
		
	}
	
	private TitledPane findPane(Accordion accordion, String name) {

		TitledPane tp = null;

		for(int i = 0; i < accordion.getPanes().size(); i++) {

			TitledPane tmpTP = accordion.getPanes().get(i);

			if(tmpTP.getText().equals(name)) {
				tp = tmpTP;
				break;
			}
		}

		return tp;
	}

	private TableView createUserInfoTable(PlayerState ps) {

		PlayerState ps2 = gi.getPlayerState(ps.getId()%4+1);
		PlayerState ps3 = gi.getPlayerState((ps.getId()+1)%4+1);
		PlayerState ps4 = gi.getPlayerState((ps.getId()+2)%4+1);

		ObservableList<JoueursInfo> data = null;

		TableView userInfoTable = new TableView();
		List<TableColumn> columns = new ArrayList<>();
		TableColumn infoColumn = new TableColumn("INFOS");
		TableColumn valueColumn = new TableColumn("VALUE");




		infoColumn.setCellValueFactory(new PropertyValueFactory<JoueursInfo, String>("infoName"));
		columns.add(infoColumn);
		valueColumn.setCellValueFactory(new PropertyValueFactory<JoueursInfo, String>("infoJoueur"+ps.getId()));
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setOnEditCommit(
				new EventHandler<CellEditEvent<JoueursInfo, String>>() {

					@Override
					public void handle(CellEditEvent<JoueursInfo, String> t) {
						((JoueursInfo) t.getTableView().getItems().get(
								t.getTablePosition().getRow())
								).setInfoJoueur(ps.getId(), t.getNewValue());
						refreshUsersInfosTable();
					}
				}
				);
		columns.add(valueColumn);



		userInfoTable.getColumns().addAll(columns);

		data = FXCollections.observableArrayList(
				new JoueursInfo(UserInfo.VIE, gi, ps2, ps3, ps4),
				new JoueursInfo(UserInfo.NB_CARD_HAND, gi, ps2, ps3, ps4),
				new JoueursInfo(UserInfo.NB_CARD_PICK, gi, ps2, ps3, ps4));

		if(ps2 != null && !ps2.getName().equals("none")) {
			data.add(new JoueursInfo(UserInfo.DEGAT_COMMANDER2, gi, ps2, ps3, ps4));
		}
		if(ps3 != null && !ps3.getName().equals("none")) {
			data.add(new JoueursInfo(UserInfo.DEGAT_COMMANDER3, gi, ps2, ps3, ps4));
		}
		if(ps4 != null && !ps4.getName().equals("none")) {
			data.add(new JoueursInfo(UserInfo.DEGAT_COMMANDER4, gi, ps2, ps3, ps4));
		}
		if(ps2 != null && !ps2.getName().equals("none")) {
			data.add(new JoueursInfo(UserInfo.DEGAT_POISON2, gi, ps2, ps3, ps4));
		}
		if(ps3 != null && !ps3.getName().equals("none")) {
			data.add(new JoueursInfo(UserInfo.DEGAT_POISON3, gi, ps2, ps3, ps4));
		}
		if(ps4 != null && !ps4.getName().equals("none")) {
			data.add(new JoueursInfo(UserInfo.DEGAT_POISON4, gi, ps2, ps3, ps4));
		}

		userInfoTable.setItems(data);
		userInfoTable.setEditable(true);
		userInfoTable.setPrefHeight(height*ESTIMATED_INFO_HEIGHT*data.size());
		userInfoTable.setPrefWidth(width);

		return userInfoTable;
	}
}