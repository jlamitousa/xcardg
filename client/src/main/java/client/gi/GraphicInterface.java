package client.gi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.CaptureZoneAction;
import actions.GameZoneAction;
import actions.HandZoneAction;
import actions.InvalidActionException;
import actions.MouseAction;
import actions.PileZoneAction;
import actions.RemoteEvent;
import client.menu.Menu;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import main.DeckLoader;
import main.Main;
import util.helper.ListUtil;
import util.math.MathOps;
import util.math.Point;

public class GraphicInterface extends Pane implements Environnement {

	private static final Logger LOGGER = LoggerFactory.getLogger("GraphicInterface");

	private static final String BCK_URL = "/background-default.jpg";	
	private static final int USER_COME_DURATION_SEC = 5;
	private static final int MINIMAL_TIME_BETWEEN_PLAYER_COMES_SEC = 2;

	private RemoteEventHandler eHandler;
	private static final Object FIRST_CON_LOCK = new Object();
	private boolean firstConDone;

	private static final Object IMG_DISPLAY_LOCK = new Object();
	private Boolean isImgDisplayed;

	private Menu menu;

	private GameZone gmZone;
	private HandZone hdZone;
	private HandZone fakeHdZone;

	private PileDisplayer pileDisplayer;
	private ImageDisplayer imgDisplayer;
	private CaptureZone currentlyCardCapturing;
	private CaptureZone currentCapturingZoneZoomed;
	private PlayerZone currentlyZoomed;
	private Card currentlyZoomedCard;
	private boolean isEventBlocked;

	private PlayerState ps1;
	private PlayerState ps2;
	private PlayerState ps3;
	private PlayerState ps4;

	public GraphicInterface(double width, double height) throws InvalidActionException {

		GraphicInterface that = this;

		String bckUrl = Main.getResourceUrl(BCK_URL);
		BackgroundImage bckImg = new BackgroundImage(new Image(bckUrl, width, height, false, true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);

		this.setWidth(width);
		this.setMinWidth(width);
		this.setMaxWidth(width);

		this.setHeight(height);
		this.setMinHeight(height);
		this.setMaxHeight(height);

		setBackground(new Background(bckImg));

		this.eHandler = new RemoteEventHandler(this);
		this.menu = null;
		this.gmZone = new GameZone(this);
		this.hdZone = new HandZone(this, null, false, false);
		this.fakeHdZone = new HandZone(this, null, true, false);
		this.pileDisplayer = new PileDisplayer(width, height);
		this.imgDisplayer = null;

		setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent me) {

				try {

					boolean sendRemote = true;
					int sourceUserId = -1;
					boolean doubleClicked = false;
					MouseAction ma = null;

					if(that.gmZone.getPlayerZone1().isMe()) {
						sourceUserId = that.gmZone.getPlayerZone1().getUserId();
					}

					doubleClicked = me.getButton().equals(MouseButton.PRIMARY) && (me.getClickCount() == 2);
					ma = new MouseAction(that, sourceUserId,  me.getX(), me.getY(), me.getTarget(), doubleClicked);

					sendRemote = handleMouseClicked(ma, false) && sendRemote;

					if(sendRemote) {
						sendMouseAction(ma, RemoteEvent.MCLICK);
					}

				} catch(Exception e) {
					LOGGER.error("", e);
				}

			}

		});

		setOnMousePressed(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent me) {

				try {

					boolean sendRemote = true;
					int sourceUserId = -1;
					boolean doubleClicked = false;
					MouseAction ma = null;

					if(that.gmZone.getPlayerZone1().isMe()) {
						sourceUserId = that.gmZone.getPlayerZone1().getUserId();
					}

					doubleClicked = me.getButton().equals(MouseButton.PRIMARY) && (me.getClickCount() == 2);
					ma = new MouseAction(that, sourceUserId,  me.getX(), me.getY(), me.getTarget(), doubleClicked);

					sendRemote = handleMousePressed(ma, false) && sendRemote;

					if(sendRemote) {
						sendMouseAction(ma, RemoteEvent.MPRESSED);
					}

				} catch(Exception e) {
					LOGGER.error("", e);
				}

			}

		});

		setOnMouseDragged(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent me) {

				try {

					boolean sendRemote = true;
					int sourceUserId = -1;
					boolean doubleClicked = false;
					MouseAction ma = null;

					if(that.gmZone.getPlayerZone1().isMe()) {
						sourceUserId = that.gmZone.getPlayerZone1().getUserId();
					}

					doubleClicked = me.getButton().equals(MouseButton.PRIMARY) && (me.getClickCount() == 2);
					ma = new MouseAction(that, sourceUserId,  me.getX(), me.getY(), me.getTarget(), doubleClicked);

					sendRemote = handleMouseDragged(ma, false) && sendRemote;

					if(sendRemote) {
						sendMouseAction(ma, RemoteEvent.MDRAGGED);
					}

				}  catch(Exception e) {
					LOGGER.error("", e);
				}

			}

		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent me) {

				try {

					boolean sendRemote = true;
					int sourceUserId = -1;
					boolean doubleClicked = false;
					MouseAction ma = null;

					if(that.gmZone.getPlayerZone1().isMe()) {
						sourceUserId = that.gmZone.getPlayerZone1().getUserId();
					}

					doubleClicked = me.getButton().equals(MouseButton.PRIMARY) && (me.getClickCount() == 2);
					ma = new MouseAction(that, sourceUserId,  me.getX(), me.getY(), me.getTarget(), doubleClicked);

					sendRemote = handleMouseReleased(ma, false) && sendRemote;

					if(sendRemote) {
						sendMouseAction(ma, RemoteEvent.MRELEASE);
					}

				}  catch(Exception e) {
					LOGGER.error("", e);
				}
			}

		});

		this.currentlyCardCapturing = null;
		this.currentlyZoomed = null;
		this.currentlyZoomedCard = null;

		this.firstConDone = false;
		this.isEventBlocked = false;
		this.isImgDisplayed = false;

		displayGame();

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(getDisplay());
		}
	}

	@Override
	public String getName() {
		return "GraphicInterface";
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public boolean firstConDone() {
		return this.firstConDone;
	}

	public boolean isConnectedToServer() {
		return eHandler.isConnectedToServer();
	}

	public void triggerServerCon() {
		eHandler.triggerServerCon();
	}

	public void startClient(String ip, int port) throws InvalidActionException {

		GraphicInterface that = this;

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					eHandler.startClient(ip, port, that);
				} catch(Exception e) {
					LOGGER.error("", e);
				}
			}
		}).start();

	}

	@Override
	public void blockEvents() {
		eHandler.blockEvents();
		this.isEventBlocked = true;
	}

	@Override
	public void unblockEvents() {
		eHandler.unblockEvents();
		this.isEventBlocked = false;
	}

	public boolean isEventBlocked() {
		return isEventBlocked;
	}

	public synchronized void handleServerMsg(Map<String, String> data) throws InvalidActionException {

		Platform.runLater(new Runnable() {

			@Override 
			public void run() {

				LOGGER.info("handleServerMsg:run()");

				try {

					switch(RemoteEvent.valueOf(data.get("eventType"))) {
					case ID_ASSIGNATION: {

						handleIdAssign(data);
						break;

					}
					case CON_OK: {

						handleConnectionOK(data);
						break;

					}
					case NEW_USER_CON: {

						handleNewUserCon(data);
						break;

					}
					case OLD_USER_CON: {

						handleOldUserCon(data);
						break;

					}
					case DECK_LOAD: {

						handleDeckLoad(data);
						break;

					}
					case RQ_HANDZONE_F:
					case RQ_HANDZONE: {

						handleRequestHandZone(data);
						break;

					}
					case HANDZONE_F:
					case HANDZONE: {

						handleHandZone(data);
						break;

					}
					case RQ_PICKZONE: {

						handleRequestPickZone(data);
						break;

					}
					case PICKZONE: {

						handlePickZone(data);
						break;

					}
					case SORT: {
						handleSort(data);
						break;
					}
					case MULLIGAN: {

						handleMulligan(data);
						break;

					}
					case PICK: {

						handlePickOne(data);
						break;

					}
					case CAPTURE: {

						handleCapture(data);
						break;

					}
					case CTOKEN: {

						handleCreateToken(data);
						break;

					}
					case MCLICK:
					case MPRESSED: 
					case MDRAGGED:
					case MRELEASE: {

						handleMouseAction(data);
						break;

					}
					default: {

						LOGGER.error("Unknown event type: "+data.get("eventType"));
						break;
					}}

				} catch(InvalidActionException iae) {
					LOGGER.error("Invalid action", iae);
				}

			}
		});

	}

	@Override
	public boolean handleMouseClicked(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		if(isPileDisplayMode() && ma.getTarget() instanceof Card) {

			try {

				Card c = (Card) ma.getTarget();

				pileDisplayer.remove(c);
				this.hdZone.captureCard(c, true);
				sendCapture(c.getCardId());

			} catch(InvalidActionException iae) {
				LOGGER.error("Invalid action", iae);
			}

		} else {

			sendRemote = this.hdZone.handleMouseClicked(ma, remote) && sendRemote;
			sendRemote = this.gmZone.handleMouseClicked(ma, remote) && sendRemote;

		}


		return !remote && sendRemote;

	}

	@Override
	public boolean handleMousePressed(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		sendRemote = this.gmZone.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.hdZone.handleMousePressed(ma, remote) && sendRemote;

		return !remote && sendRemote;

	}

	@Override
	public boolean handleMouseDragged(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		sendRemote = this.gmZone.handleMouseDragged(ma, remote) && sendRemote;
		sendRemote = this.hdZone.handleMouseDragged(ma, remote) && sendRemote;

		return !remote && sendRemote;

	}

	@Override
	public boolean handleMouseReleased(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		sendRemote = this.gmZone.handleMouseReleased(ma, remote) && sendRemote;
		sendRemote = this.hdZone.handleMouseReleased(ma, remote) && sendRemote;

		putInFront(this.hdZone);

		return !remote && sendRemote;

	} 

	public void waitFirstConDone() throws InterruptedException {

		synchronized (FIRST_CON_LOCK) {
			FIRST_CON_LOCK.wait();
		}
	}

	private void handleConnectionOK(Map<String, String> data) {

		synchronized (FIRST_CON_LOCK) {
			this.firstConDone = true;
			FIRST_CON_LOCK.notify();
		}

	}

	private void  handleIdAssign(Map<String, String> data) {

		int id = Integer.parseInt(data.get("id"));

		if(id > 0) {

			PlayerZone pz1 = this.gmZone.getPlayerZone1();

			this.hdZone.setRelatedPZ(pz1);
			pz1.setUserId(id);
			pz1.setIsMe(true);
			this.menu.refreshUsersInfosTable();	
			sendAcknowledge(RemoteEvent.ID_ASSIGNATION_OK);

		}

	}

	private void handleNewUserCon(Map<String, String> data) {

		int newUserId = Integer.parseInt(data.get("id"));
		PlayerZone pz = newUserId > 0 ? this.gmZone.findPlayerZone(newUserId) : null;

		if(pz != null) {
			try {
				pz.setUserId(newUserId);
				pz.createOpponentHandZone();
				sendNewUserConOK(newUserId);
			} catch(Exception e) {
				LOGGER.error("Error on loading new user", e);
			}
		}

	}

	private void handleOldUserCon(Map<String, String> data) {

		int oldUserId = Integer.parseInt(data.get("id"));
		PlayerZone pz = this.gmZone.findPlayerZone(oldUserId);

		try {

			pz.setUserId(oldUserId);
			pz.createOpponentHandZone();
			DeckLoader.loadDeck(pz, this.fakeHdZone, data.get("deckPath"), data.get("deckName"));
			sendOldUserConOK(oldUserId);
			displayImg("/"+data.get("deckName")+".png", USER_COME_DURATION_SEC);
			askForHand(oldUserId, true);
			this.menu.refreshUsersInfosTable();

		} catch(Exception e) {
			LOGGER.error("Error on loading deck", e);
		}
	}

	private void handleDeckLoad(Map<String, String> data) {

		int userId = Integer.parseInt(data.get("id"));
		String deckName = data.get("deckName");
		String deckPath = data.get("deckPath");
		String deckOrder = data.get("order");

		try {

			PlayerZone playerZone = this.gmZone.findPlayerZone(userId);
			PickZone pickZone = playerZone.getPickZone();

			playerZone.reset();
			DeckLoader.loadDeck(playerZone, fakeHdZone, deckPath, deckName);
			pickZone.sort(deckOrder);
			getXCard(pickZone, playerZone, 7);
			displayImg("/"+deckName+".png", USER_COME_DURATION_SEC);

			this.menu.refreshUsersInfosTable();

		} catch(Exception e) {
			LOGGER.error("Error on loading deck", e);
		}

	}

	private void handleRequestHandZone(Map<String, String> data) {
		boolean firstTime = RemoteEvent.RQ_HANDZONE_F.name().equals(data.get("eventType"));
		sendHandZone(Integer.parseInt(data.get("source_id")), this.hdZone.serialize(), firstTime);
	}

	private void handleHandZone(Map<String, String> data) throws InvalidActionException {

		int userId = Integer.parseInt(data.get("source_id"));
		PlayerZone pz = this.gmZone.findPlayerZone(userId);
		String hand = data.get("hand");
		String[] cardIds = null;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("handzone for '"+userId+"': "+hand);
		}

		if(hand != null && hand.length() > 0) {
			cardIds = hand.split("=>");
		}

		pz.captureInOppenentHand(cardIds);
	}

	private void handleRequestPickZone(Map<String, String> data) {

		PickZone pickZone = this.gmZone.getPlayerZone1().getPickZone();

		sendPickZone(Integer.parseInt(data.get("source_id")), pickZone.serialize());
	}

	private void handlePickZone(Map<String, String> data) throws InvalidActionException {

		int sourceId = Integer.parseInt(data.get("source_id"));
		PlayerZone playerZone = this.gmZone.findPlayerZone(sourceId);

		playerZone.getPickZone().sort(data.get("order"));
	}

	private void handleSort(Map<String, String> data) {

		int sourceId = Integer.parseInt(data.get("id"));
		PlayerZone playerZone = this.gmZone.findPlayerZone(sourceId);

		try {

			playerZone.getPickZone().sort(data.get("order"));	

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		refreshMenuData();

	}

	private void handleMulligan(Map<String, String> data) {

		int sourceId = Integer.parseInt(data.get("id"));
		PlayerZone playerZone = this.gmZone.findPlayerZone(sourceId);

		try {

			playerZone.getOppenentHandZone().putHandInPickZone(playerZone);
			playerZone.getPickZone().sort(data.get("order"));	

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		refreshMenuData();

	}

	private void handlePickOne(Map<String, String> data) {

		int sourceId = Integer.parseInt(data.get("id"));
		PlayerZone playerZone = this.gmZone.findPlayerZone(sourceId);

		try {

			playerZone.getOppenentHandZone().captureCard(playerZone.getPickZone().getFirst(), false);

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		refreshMenuData();

	}

	private void handleCapture(Map<String, String> data) {

		int sourceId = Integer.parseInt(data.get("id"));
		PlayerZone playerZone = this.gmZone.findPlayerZone(sourceId);
		String cardId = data.get("card_id");

		try {

			Card c = playerZone.find(cardId);

			if(c != null) {
				playerZone.getOppenentHandZone().captureCard(c, false);
			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		refreshMenuData();

	}

	private void handleCreateToken(Map<String, String> data) {

		int sourceId = Integer.parseInt(data.get("id"));
		PlayerZone playerZone = this.gmZone.findPlayerZone(sourceId);

		try {

			playerZone.createToken(true);

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		refreshMenuData();

	}

	private void handleMouseAction(Map<String, String> data) {

		int sourceId = Integer.parseInt(data.get("id"));
		String targetId = data.get("target_id");
		double finalX = Double.parseDouble(data.get("x"));
		double finalY = Double.parseDouble(data.get("y"));
		boolean doubleClicked = "1".equals(data.get("doubleClicked"));
		MouseAction ma = null;
		Point gmMiddle =  this.gmZone.getAbsoluteMiddle();

		if(this.gmZone.getPlayerZone1().getUserId()==this.gmZone.findPlayerZone(2).getUserId()) {

			Point p = MathOps.rotate(REVERSE_ANGLE, gmMiddle, new Point(finalX, finalY));

			finalX = p.getX();
			finalY = p.getY();

		} else if(this.gmZone.getPlayerZone1().getUserId()==this.gmZone.findPlayerZone(3).getUserId()) {

			Point p = MathOps.rotate(REVERSE_ANGLE, new Point(finalX, gmMiddle.getY()), new Point(finalX, finalY));

			finalY = p.getY();

		} else if(this.gmZone.getPlayerZone1().getUserId()==this.gmZone.findPlayerZone(4).getUserId()) {

			Point p = MathOps.rotate(REVERSE_ANGLE, new Point(gmMiddle.getX(), finalY), new Point(finalX, finalY));

			finalY = p.getX();
		}

		ma = new MouseAction(this, sourceId, finalX, finalY, targetId, doubleClicked);

		switch(RemoteEvent.valueOf(data.get("eventType"))) {
		case MCLICK:
			handleMouseClicked(ma, true);
			break;
		case MPRESSED:
			handleMousePressed(ma, true);
			break;
		case MDRAGGED:
			handleMouseDragged(ma, true);
			break;
		case MRELEASE:
			handleMouseReleased(ma, true);
			break;
		default:
			LOGGER.error("invalid eventType: '"+data.get("eventType")+"'");
		}

	}

	public void sendServerFirstCon() {

		Map<String, String> data = new HashMap<String, String>();

		data.put("eventType", RemoteEvent.FIRST_CON.name());
		this.eHandler.sendEvent(data);

	}


	public void sendNewUserConOK(int targetId) {

		Map<String, String> data = new HashMap<String, String>();
		int userId = this.gmZone.getPlayerZone1().getUserId();

		data.put("eventType", RemoteEvent.NEW_USER_CON_OK.name());
		data.put("id", userId+"");
		data.put("target_id", targetId+"");

		this.eHandler.sendEvent(data);

	}

	public void sendOldUserConOK(int targetId) {

		Map<String, String> data = new HashMap<String, String>();
		int userId = this.gmZone.getPlayerZone1().getUserId();

		data.put("eventType", RemoteEvent.OLD_USER_CON_OK.name());
		data.put("id", userId+"");
		data.put("target_id", targetId+"");

		this.eHandler.sendEvent(data);

	}

	private void sendAcknowledge(RemoteEvent re) {

		Map<String, String> data = new HashMap<String, String>();
		int userId = this.gmZone.getPlayerZone1().getUserId();

		data.put("eventType", re.name());
		data.put("id", userId+"");

		this.eHandler.sendEvent(data);

	}

	public void sendServerDeckLoad(int userId, String deckPath, String deckName, String deckOrder) {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();

			data.put("eventType", RemoteEvent.DECK_LOAD.name());
			data.put("id", userId+"");
			data.put("deckName", deckName);
			data.put("deckPath", deckPath);
			data.put("order", deckOrder);

			this.eHandler.sendEvent(data);

		}
	}

	public void askForHand(int targetId, boolean firstTime) {

		if(!isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();
			String eventType = firstTime ? RemoteEvent.RQ_HANDZONE_F.name() : RemoteEvent.RQ_HANDZONE.name();

			data.put("eventType", eventType);
			data.put("id", userId+"");
			data.put("target_id", targetId+"");

			this.eHandler.sendEvent(data);
		}
	}

	public void sendHandZone(int targetId, String order, boolean firstTime) {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();
			String eventType = firstTime ? RemoteEvent.HANDZONE_F.name() : RemoteEvent.HANDZONE.name();

			data.put("eventType", eventType);
			data.put("id", userId+"");
			data.put("target_id", targetId+"");
			data.put("hand", order);

			this.eHandler.sendEvent(data);

		}

	}

	public void sendMulligan() {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();
			String eventType = RemoteEvent.MULLIGAN.name();
			PickZone pickZone = this.gmZone.getPlayerZone1().getPickZone();

			data.put("eventType", eventType);
			data.put("id", userId+"");
			data.put("order", pickZone.serialize());

			this.eHandler.sendEvent(data);

		}

	}

	public void sendSort() {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();
			String eventType = RemoteEvent.SORT.name();
			PickZone pickZone = this.gmZone.getPlayerZone1().getPickZone();

			data.put("eventType", eventType);
			data.put("id", userId+"");
			data.put("order", pickZone.serialize());

			this.eHandler.sendEvent(data);

		}

	}

	public void sendPick() {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();
			String eventType = RemoteEvent.PICK.name();

			data.put("id", userId+"");
			data.put("eventType", eventType);

			this.eHandler.sendEvent(data);

		}

	}

	public void sendCapture(String cardId) {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();
			String eventType = RemoteEvent.PICK.name();

			data.put("id", userId+"");
			data.put("eventType", eventType);
			data.put("card_id", cardId);

			this.eHandler.sendEvent(data);

		}

	}

	public void sendCreateToken() {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();
			String eventType = RemoteEvent.CTOKEN.name();

			data.put("eventType", eventType);
			data.put("id", userId+"");

			this.eHandler.sendEvent(data);

		}

	}

	public void sendMouseAction(MouseAction ma, RemoteEvent action) {

		if(firstConDone && !isEventBlocked) {

			int sourceUserId = -1;

			if(this.gmZone.getPlayerZone1().isMe()) {
				sourceUserId = this.gmZone.getPlayerZone1().getUserId();
			}

			if(sourceUserId > 0) {

				Map<String, String> data = new HashMap<String, String>();
				double finalX = ma.getX();
				double finalY = ma.getY();
				Point src = new Point(finalX, finalY);
				Point target = null;
				Point gmMiddle = this.gmZone.getAbsoluteMiddle();


				if(this.gmZone.getPlayerZone1().getUserId()==this.gmZone.findPlayerZone(2).getUserId()) {

					target = MathOps.rotate(REVERSE_ANGLE, gmMiddle, src);

					finalX = target.getX();
					finalY = target.getY();

				} else if(this.gmZone.getPlayerZone1().getUserId()==this.gmZone.findPlayerZone(3).getUserId()) {

					target = MathOps.rotate(REVERSE_ANGLE, new Point(finalX, gmMiddle.getY()), src);

					finalY = target.getY();

				} else if(this.gmZone.getPlayerZone1().getUserId()==this.gmZone.findPlayerZone(4).getUserId()) {

					target = MathOps.rotate(REVERSE_ANGLE, new Point(gmMiddle.getX(), finalY), src);

					finalY = target.getX();

				}

				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("from: "+src+" to "+target);
				}

				data.put("eventType", action.name());
				data.put("id", sourceUserId+"");
				data.put("target_id", ma.getTargetId());
				data.put("x", finalX+"");
				data.put("y", finalY+"");
				data.put("doubleClicked", ma.isDoubleClicked() ? "1" : "0");

				this.eHandler.sendEvent(data);

			}

		}	

	}

	public void sendPickZone(int targetId, String order) {

		if(firstConDone && !isEventBlocked) {

			Map<String, String> data = new HashMap<String, String>();
			int userId = this.gmZone.getPlayerZone1().getUserId();

			data.put("eventType", RemoteEvent.PICKZONE.name());
			data.put("id", userId+"");
			data.put("target_id", targetId+"");
			data.put("order", order);

			this.eHandler.sendEvent(data);

		}
	}

	@Override
	public void displayPile(Pile pile) throws InvalidActionException  {
		displayPile(pile, Integer.MAX_VALUE);
	}

	@Override
	public void displayPile(Pile pile, int nb)  throws InvalidActionException {

		List<HandZone> oppHZs = getOpponentsHZ();

		this.pileDisplayer.setPile(pile, nb);
		this.getChildren().clear();
		this.getChildren().addAll(oppHZs);
		this.getChildren().add(this.pileDisplayer);
		this.menu.handleFocus(this.pileDisplayer);
	}

	public boolean isPileDisplayMode() {
		return getChildren().contains(this.pileDisplayer);
	}

	@Override
	public void displayGame() throws InvalidActionException {

		List<HandZone> oppHZs = getOpponentsHZ();

		this.pileDisplayer.clear();

		this.getChildren().clear();
		this.getChildren().addAll(oppHZs);
		this.getChildren().add(this.gmZone);
		this.getChildren().add(this.hdZone);

	}

	@Override
	public void displayImg(String img, int secDuration) throws InvalidActionException {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					synchronized (IMG_DISPLAY_LOCK) {

						while(isImgDisplayed) {
							IMG_DISPLAY_LOCK.wait();
						}

						isImgDisplayed = true;

					}

					Platform.runLater(new Runnable() {

						@Override 
						public void run() {

							LOGGER.info("displayImg:run()");

							List<HandZone> oppHZs = getOpponentsHZ();

							imgDisplayer = new ImageDisplayer(getWidth(), getHeight(), img);
							getChildren().clear();
							getChildren().addAll(oppHZs);
							getChildren().add(imgDisplayer);

							new Thread(new Runnable() {

								@Override
								public void run() {

									try {

										Thread.sleep(secDuration*1000);

										Platform.runLater(new Runnable() {

											@Override 
											public void run() {

												try {

													LOGGER.info("displayGame:run()");
													displayGame();
													Thread.sleep(MINIMAL_TIME_BETWEEN_PLAYER_COMES_SEC*1000);
													isImgDisplayed = false;

													synchronized (IMG_DISPLAY_LOCK) {
														IMG_DISPLAY_LOCK.notify();
													}

												} catch(InvalidActionException iae) {
													LOGGER.error("Invalid action", iae);
												} catch (InterruptedException ie) {
													LOGGER.error("", ie);
												}

											}

										});

									} catch (InterruptedException ie) {
										LOGGER.error("", ie);
									}

								}
							}).start();
						}
					});

				} catch (InterruptedException ie) {
					LOGGER.error("", ie);
				}

			}

		}).start();

	}

	public GameZone getGmZone() {
		return this.gmZone;
	}

	public HandZone getHdZone() {
		return this.hdZone;
	}

	@Override
	public double getAbsoluteX(double x) {
		return x;
	}

	@Override
	public double getAbsoluteY(double y) {
		return y;
	}

	@Override
	public Point getInterfaceAbsMiddle() {
		return getAbsoluteMiddle();
	}

	@Override
	public Point getGameZoneAbsMiddle() {
		return this.gmZone.getAbsoluteMiddle();
	}

	@Override
	public boolean authorizeAction(GameZone gz, GameZoneAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		boolean authorize = 
				(currentlyZoomed == null) ||
				action == GameZoneAction.PZ_UNZOOM ||
				action == GameZoneAction.ZONE_ZOOM ||
				action == GameZoneAction.CARD_ZOOM ||
				action == GameZoneAction.CARD_UNZOOM;

		boolean canPutInFront = true;


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("GameZone:"+gz.getName()+" is trying to do action '"+action.name()+"'");
		}


		switch(action) {
		case PZ_CARD_CAPTURE: {
			canPutInFront = false;
			break;

		}
		case PZ_CARD_RELEASE: {
			canPutInFront = false;
			break;

		}
		case CARD_PRECAPTURE: {
			canPutInFront = false;
			break;

		}
		case CARD_CAPTURE_ABORD: {

			if(authorize) {
				setCurrentCapturingZone(null);
			}
			canPutInFront = false;
			break;

		}	
		case PZ_ZOOM: {

			authorize = authorize && this.currentlyZoomedCard==null;
			break;

		}
		case PZ_UNZOOM: {

			authorize = authorize && this.currentlyZoomedCard==null;
			break;

		}
		case PZ_USER_MOVE: {

			authorize = authorize && this.currentlyZoomedCard==null;
			break;

		}}


		if(authorize && canPutInFront && !forceDontPutInFront && !remote) {
			putInFront(gz);
		}


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success: "+authorize+", put in front: "+(canPutInFront && !forceDontPutInFront && !remote));
		}


		return authorize;
	}

	@Override
	public boolean authorizeAction(HandZone hz, HandZoneAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		boolean authorize = 
				(currentlyZoomed == null) ||
				action == HandZoneAction.CARD_ZOOM ||
				action == HandZoneAction.CARD_UNZOOM;

		boolean canPutInFront = !hz.isFakeZone();


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("HandZone:"+hz.getName()+" is trying to do action '"+action.name()+"'");
		}


		switch(action) {
		case CARD_PRECAPTURE: {

			if(authorize) {
				setCurrentCapturingZone(hz);
			}
			canPutInFront = false;
			break;

		}
		case CARD_CAPTURE_ABORD: {

			if(authorize) {
				setCurrentCapturingZone(null);
			}
			canPutInFront = false;
			break;

		}
		case USER_MOVE: {

			authorize = this.currentlyZoomedCard==null;
			break;

		}}


		if(authorize && canPutInFront && !forceDontPutInFront && !remote) {
			putInFront(hz);
		}


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success: "+authorize+", put in front: "+(canPutInFront && !forceDontPutInFront && !remote));
		}


		return authorize;
	}

	@Override
	public boolean authorizeAction(CaptureZone cz, CaptureZoneAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		if(cz instanceof HandZone) {
			return authorizeAction((HandZone)cz, HandZoneAction.translate(action), remote, forceDontPutInFront);
		}

		throw new InvalidActionException("Not implemented");
	}

	@Override
	public void notifyActionFinish(CaptureZone cz, CaptureZoneAction action) throws InvalidActionException {

		if(cz instanceof HandZone) {
			notifyActionFinish((HandZone)cz, HandZoneAction.translate(action));
			return;
		}

		throw new InvalidActionException("Not implemented");
	}

	@Override
	public void notifyActionFinish(PileZone pileZone, PileZoneAction action) throws InvalidActionException {

	}

	@Override
	public void notifyActionFinish(HandZone hz, HandZoneAction action) throws InvalidActionException {

		switch(action) {
		case CARD_CAPTURE: {

			this.currentlyCardCapturing = null;
			break;
		}}

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("HandZone:"+hz.getName()+" has finish action '"+action.name()+"'");
		}
	}

	private void getXCard(PickZone pickZone, PlayerZone playerZone, int count) throws InvalidActionException {

		for(int i = 0; i < Math.min(count, pickZone.getPileSize()); i++) {
			playerZone.captureCardInOpponentHandZone(pickZone.getFirst());
		}		

	}

	@Override
	public Environnement getParentEnv() {
		return null;
	}

	@Override
	public CaptureZone getCurrentCapturingZone() {
		return this.currentlyCardCapturing;
	}

	@Override
	public void setCurrentCapturingZone(CaptureZone cz) throws InvalidActionException {
		this.currentlyCardCapturing = cz;
	}

	@Override
	public PlayerZone getCurrentlyZoomedPZ() {
		return this.currentlyZoomed;
	}

	@Override
	public void setCurrentlyZoomedPZ(PlayerZone pz) throws InvalidActionException {
		this.currentlyZoomed = pz;
	}

	@Override
	public Card getCurrentlyZoomedCard() {
		return this.currentlyZoomedCard;
	}

	@Override
	public void setCurrentlyZoomedCard(Card c) throws InvalidActionException {
		this.currentlyZoomedCard = c;
	}

	@Override
	public PlayerZone getRelatedPZ() {
		return null;
	}

	@Override
	public GraphicInterface getGI() {
		return this;
	}

	@Override
	public GameZone getGZ() {
		return this.gmZone;
	}

	@Override
	public HandZone getHandZone() {
		return this.hdZone;
	}

	@Override
	public CaptureZone getCurrentCapturingZoneZoomed() {
		return this.currentCapturingZoneZoomed;
	}

	@Override
	public void setCurrentCapturingZoneZoomed(CaptureZone cz) {
		this.currentCapturingZoneZoomed = cz;
	}

	@Override
	public void reset() throws InvalidActionException {

		this.gmZone.reset();
		this.hdZone.reset();

		currentlyCardCapturing = null;
		currentCapturingZoneZoomed = null;
		currentlyZoomed = null;
	}

	public void initPlayerState(int playerId) {

		PlayerState ps = null;
		PlayerZone pz = this.gmZone.findPlayerZone(playerId);
		HandZone hz = pz.getOppenentHandZone() != null ? pz.getOppenentHandZone() : this.hdZone;

		if(pz != null) {

			ps = new PlayerState(pz.getPickZone(), hz);

			ps.setId(playerId);
			ps.setName(pz.getUserName());
			ps.setVie(40);
			ps.setDegatCommanderP2(0);
			ps.setDegatCommanderP3(0);
			ps.setDegatCommanderP4(0);
			ps.setDegatPoisonP2(0);
			ps.setDegatPoisonP3(0);
			ps.setDegatPoisonP4(0);

		}

		if(playerId==1) {
			ps1 = ps;
		} else if(playerId==2) {
			ps2 = ps;
		}  else if(playerId==3) {
			ps3 = ps;
		}  else if(playerId==4) {
			ps4 = ps;
		}
	}

	public PlayerState getPlayerState(int playerId) {

		PlayerState ps = null;

		if(playerId==this.gmZone.findPlayerZone(1).getUserId()) {
			ps = ps1;
		} else if(playerId==this.gmZone.findPlayerZone(2).getUserId()) {
			ps = ps2;
		}  else if(playerId==this.gmZone.findPlayerZone(3).getUserId()) {
			ps = ps3;
		}  else if(playerId==this.gmZone.findPlayerZone(4).getUserId()) {
			ps = ps4;
		}

		return ps;
	}

	/**
	 * 
	 * Nous faisons en sorte que le jouer connecté voit toujours
	 * son plateau de jeu dans la partie bas-droite. Cela explique
	 * le traitement suivant pour retrouver la position définitive 
	 * de la zone de jeu à traiter.
	 * @param playerId
	 * @return
	 */
	public PlayerState getRelativePlayerState(int playerId) {

		PlayerState ps = null;

		if(this.gmZone.getPlayerZone1().getUserId() == 1) {

			if(playerId == 1) {
				ps = ps1;
			} else if(playerId == 2) {
				ps = ps2;
			}  else if(playerId == 3) {
				ps = ps3;
			}  else if(playerId == 4) {
				ps = ps4;
			}

		} else if(this.gmZone.getPlayerZone1().getUserId() == 2) {

			if(playerId == 1) {
				ps = ps2;
			} else if(playerId == 2) {
				ps = ps1;
			}  else if(playerId == 3) {
				ps = ps4;
			}  else if(playerId == 4) {
				ps = ps3;
			}

		} else if(this.gmZone.getPlayerZone1().getUserId() == 3) {

			if(playerId == 1) {
				ps = ps3;
			} else if(playerId == 2) {
				ps = ps4;
			}  else if(playerId == 3) {
				ps = ps1;
			}  else if(playerId == 4) {
				ps = ps2;
			}

		} else if(this.gmZone.getPlayerZone1().getUserId() == 4) {

			if(playerId == 1) {
				ps = ps4;
			} else if(playerId == 2) {
				ps = ps3;
			}  else if(playerId == 3) {
				ps = ps2;
			}  else if(playerId == 4) {
				ps = ps1;
			}

		}

		return ps;
	}

	private List<HandZone> getOpponentsHZ() {

		return ListUtil.getAll(getChildren(), HandZone.class)
				.stream()
				.filter(hz -> (hz != this.hdZone))
				.collect(Collectors.toList());

	}

	public void setChildrenPosition() {

		List<HandZone> fakeHZs = new ArrayList<HandZone>();

		for(Node n : getChildren()) {

			if(n instanceof HandZone && ((HandZone)n).isFakeZone()) {
				fakeHZs.add((HandZone)n);
			}

		}

		for(HandZone hz : fakeHZs) {
			getChildren().remove(hz);
			getChildren().add(0, hz);
		}

	}

	@Override
	public String getEnvId() {
		return "GI";
	}
}
