package de.richardvierhaus.dame.mechanics;

import java.util.ArrayList;
import java.util.HashMap;

import de.richardvierhaus.dame.GameData;
import de.richardvierhaus.dame.GameScene;
import de.richardvierhaus.dame.exceptions.WrongUsedMethodException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Board {

	private static Board INSTANCE;
	private GameScene gameScene;

	private Field[][] fields;

	private ArrayList<Field> blackFields;
	private ArrayList<Field> whiteFields;

	private String player1Name;
	private String player2Name;

	private final boolean devmode;
	private boolean devmodeActivated;
	private Button devmodeSwitch;
	private Button turnSwitch;
	private ImageView[][] devmodePlace;
	private ButtonType devType;
	private ButtonColour devColour;

	private Field activeField;
	private HashMap<Field, Move> activeMoves;

	private boolean turn;

	private static boolean setup;

	private GameData data;

	public Board(GameData data, GameScene scene) {
		INSTANCE = this;
		gameScene = scene;
		this.data = data;
		setup = false;
		this.player1Name = data.getPlayer1Name();
		this.player2Name = data.getPlayer2Name();

		this.devmode = data.isDevmode();

		if (devmode) {
			setupDevmode();
			toggleDevmode();
		} else {
			gameScene.showDevPanel(false);
			devmodeActivated = false;
		}
		turn = false;
		activeField = null;
		blackFields = new ArrayList<>();
		whiteFields = new ArrayList<>();
		activeMoves = new HashMap<>();
		fields = new Field[8][8];
		fillFields();

		gameScene.getBackButton().setOnAction(e -> {
			devmodeActivated = true;
			gameScene.backToMainMenu(getDevFields());
		});

		gameScene.setPlayer1Name(player1Name);
		gameScene.setPlayer2Name(player2Name);
		setupDisplay();
		whiteFields.forEach(field -> field.calculateMoves());
		blackFields.forEach(field -> field.calculateMoves());
		setup = true;

		if (data.getPluginManager().getPlugin1() != null)
			data.getPluginManager().getPlugin1().setColour(ButtonColour.BLACK);
		if (data.getPluginManager().getPlugin2() != null)
			data.getPluginManager().getPlugin2().setColour(ButtonColour.WHITE);

		if (!turn && data.getPluginManager().getPlugin1() != null && !devmode) {
			data.getPluginManager().getPlugin1().doMove(getMoves(blackFields), createSimulation()).paintAndExecute();
		}
	}

	protected Board(Field[][] fields, ArrayList<Field> blackFields, ArrayList<Field> whiteFields, boolean turn) {
		devmode = false;
		this.fields = fields;
		this.blackFields = blackFields;
		this.whiteFields = whiteFields;
		activeMoves = new HashMap<>();
	}

	private StoneClickListener getStoneClickListener() {
		return new StoneClickListener() {
			public void onStoneClicked(StoneClickEvent e) {
				Field field = e.getSource();
				if (devmodeActivated) {
					whiteFields.remove(field);
					blackFields.remove(field);
					if (devColour == ButtonColour.WHITE)
						whiteFields.add(field);
					else if (devColour == ButtonColour.BLACK)
						blackFields.add(field);

					field.setStone(devColour, devType);

					setupDisplay();
				} else {
					if ((turn && data.getPluginManager().getPlugin2() == null)
							|| (!turn && data.getPluginManager().getPlugin1() == null)) {
						if (activeField == null) {
							if ((turn && field.getColour() == ButtonColour.WHITE)
									|| (!turn && field.getColour() == ButtonColour.BLACK)) {
								if (field.getMoves().size() > 0) {
									activeField = field;
									field.paintMoves();
								} else {
									gameScene.showAlertBox("Achtung", "Auf diesem Feld ist kein Zug möglich.",
											getDevFields());
								}
							} else {
								gameScene.showAlertBox("Achtung", "Wähle eins deiner eigenen Felder aus.",
										getDevFields());
							}
						} else {
							if (activeField == field) {

								activeField = null;
								activeMoves.keySet().forEach(selected -> activeMoves.get(selected).unpaint());
								activeMoves.clear();
							} else if (activeMoves.containsKey(field)) {
								activeMoves.get(field).execute(INSTANCE);

							} else {
								gameScene.showAlertBox("Achtung", "Wähle eins der markierten Felder.", getDevFields());
							}
						}

					} else {
						gameScene.showAlertBox("Achtung", "Der Computer berechnet.", getDevFields());
					}
				}
			}
		};
	}

	protected void changeTurn() {
		activeField = null;
		activeMoves.clear();
		turn = !turn;
		whiteFields.forEach(field -> field.calculateMoves());
		blackFields.forEach(field -> field.calculateMoves());
		if (!(this instanceof Simulation)) {
			setupDisplay();

			if (!devmode) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (blackFields.size() == 0) {
							gameScene.endGame(data, player2Name, getDevFields());
							return;
						} else if (!canMove(blackFields)) {
							gameScene.endGame(data, player2Name, getDevFields());
							return;
						}

						if (whiteFields.size() == 0) {
							gameScene.endGame(data, player1Name, getDevFields());
							return;
						} else if (!canMove(whiteFields)) {
							gameScene.endGame(data, player1Name, getDevFields());
							return;
						}
					}
				});
			}

			if (turn && data.getPluginManager().getPlugin2() != null && !devmodeActivated
					&& getMoves(whiteFields).size() > 0) {
				data.getPluginManager().getPlugin2().doMove(getMoves(whiteFields), createSimulation())
						.paintAndExecute();
			} else if (!turn && data.getPluginManager().getPlugin1() != null && !devmodeActivated
					&& getMoves(blackFields).size() > 0) {
				data.getPluginManager().getPlugin1().doMove(getMoves(blackFields), createSimulation())
						.paintAndExecute();
			}
		}
	}

	public int[] getStoneAmount(ArrayList<Field> fields) {
		int[] stones = new int[2];
		stones[0] = 0;
		stones[1] = 0;
		for (Field f : fields) {
			if (f.getType() == ButtonType.STONE)
				stones[0]++;
			else
				stones[1]++;
		}
		return stones;
	}

	public ArrayList<Move> getMoves(ArrayList<Field> fields) {
		ArrayList<Move> moves = new ArrayList<>();
		for (Field field : fields)
			for (Move m : field.getMoves())
				moves.add(m);
		return moves;
	}

	public boolean canMove(ArrayList<Field> fields) {
		return !getMoves(fields).isEmpty();
	}

	public Simulation createSimulation() {
		Field[][] fieldsCopy = new Field[8][8];
		ArrayList<Field> blackFieldsCopy = new ArrayList<>(), whiteFieldsCopy = new ArrayList<>();
		Field current;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((j % 2 == 0) == (i % 2 == 0)) {
					current = fields[i][j];
					fieldsCopy[i][j] = new Field(current.getPositionX(), current.getPositionY(), current.getColour(),
							current.getType());
					if (current.getColour() == ButtonColour.WHITE)
						whiteFieldsCopy.add(fieldsCopy[i][j]);
					else if (current.getColour() == ButtonColour.BLACK)
						blackFieldsCopy.add(fieldsCopy[i][j]);
				}
			}
		}
		return new Simulation(fieldsCopy, blackFieldsCopy, whiteFieldsCopy, turn);
	}

	private void fillFields() {
		ImageView[][] buttons = gameScene.getButtons();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((j % 2 == 0) == (i % 2 == 0)) {
					Field f = new Field(buttons[i][j], i, j, this).setListener(getStoneClickListener());
					if (j < 3 && !devmode) {
						whiteFields.add(f);
						f.setStone(ButtonColour.WHITE, ButtonType.STONE);
					}
					if (j > 4 && !devmode) {
						blackFields.add(f);
						f.setStone(ButtonColour.BLACK, ButtonType.STONE);
					}
					fields[i][j] = f;
				}
			}
		}
	}

	private void setupDevmode() {
		devType = ButtonType.NONE;
		devColour = ButtonColour.EMPTY;
		devmodePlace = gameScene.getPlaceButtons();
		devmodeActivated = false;
		devmodeSwitch = gameScene.getDevmodeSwitch();
		devmodeSwitch.setOnMouseClicked(e -> toggleDevmode());
		turnSwitch = gameScene.getTurnSwitch();
		turnSwitch.setText(player1Name);
		turnSwitch.setOnMouseClicked(e -> changeTurn());
		devmodePlace[0][0].setOnMouseClicked(getPlaceHandler());
		devmodePlace[0][1].setOnMouseClicked(getPlaceHandler());
		devmodePlace[1][0].setOnMouseClicked(getPlaceHandler());
		devmodePlace[1][1].setOnMouseClicked(getPlaceHandler());
	}

	private EventHandler<MouseEvent> getPlaceHandler() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (devmodeActivated) {
					ImageView source = (ImageView) event.getSource();
					ButtonType sourceType = ButtonType.STONE;
					ButtonColour sourceColour = ButtonColour.BLACK;
					if (source == devmodePlace[1][0]) {
						sourceColour = ButtonColour.WHITE;
					} else if (source == devmodePlace[0][1]) {
						sourceType = ButtonType.CHECKER;
					} else if (source == devmodePlace[1][1]) {
						sourceColour = ButtonColour.WHITE;
						sourceType = ButtonType.CHECKER;
					}

					if (devColour == sourceColour && devType == sourceType) {
						devColour = ButtonColour.EMPTY;
						devType = ButtonType.NONE;
						String path = "/de/richardvierhaus/dame/grafics/field_dark";
						if (sourceType == ButtonType.STONE)
							path += "_stone";
						else
							path += "_checker";
						if (sourceColour == ButtonColour.BLACK)
							path += "_dark";
						else
							path += "_bright";
						source.setImage(new Image(path + ".png"));
					} else {
						devmodePlace[0][0]
								.setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_stone_dark.png"));
						devmodePlace[1][0]
								.setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_stone_bright.png"));
						devmodePlace[0][1]
								.setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_checker_dark.png"));
						devmodePlace[1][1]
								.setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_checker_bright.png"));
						devColour = sourceColour;
						devType = sourceType;
						String path = "/de/richardvierhaus/dame/grafics/field_dark";
						if (sourceType == ButtonType.STONE)
							path += "_stone";
						else
							path += "_checker";
						if (sourceColour == ButtonColour.BLACK)
							path += "_dark";
						else
							path += "_bright";
						source.setImage(new Image(path + "_active.png"));
					}
				}
			}
		};
	}

	private void toggleDevmode() {
		devmodeActivated = !devmodeActivated;
		if (activeField != null) {
			activeField = null;
			activeMoves.keySet().forEach(selected -> activeMoves.get(selected).unpaint());
			activeMoves.clear();
		}
		if (devmodeActivated) {
			devmodeSwitch.setText("AN");
			devType = ButtonType.NONE;
			devColour = ButtonColour.EMPTY;
			devmodePlace[0][0].setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_stone_dark.png"));
			devmodePlace[1][0].setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_stone_bright.png"));
			devmodePlace[0][1].setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_checker_dark.png"));
			devmodePlace[1][1].setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_checker_bright.png"));
		} else {
			devmodeSwitch.setText("AUS");
			devmodePlace[0][0].setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_stone_dark_kick.png"));
			devmodePlace[1][0].setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_stone_bright_kick.png"));
			devmodePlace[0][1].setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_checker_dark_kick.png"));
			devmodePlace[1][1]
					.setImage(new Image("/de/richardvierhaus/dame/grafics/field_dark_checker_bright_kick.png"));
			if (turn && data.getPluginManager().getPlugin2() != null && getMoves(whiteFields).size() > 0) {
				data.getPluginManager().getPlugin2().doMove(getMoves(whiteFields), createSimulation())
						.paintAndExecute();
			} else if (!turn && data.getPluginManager().getPlugin1() != null && getMoves(blackFields).size() > 0) {
				data.getPluginManager().getPlugin1().doMove(getMoves(blackFields), createSimulation())
						.paintAndExecute();
			}
		}
	}

	private void setupDisplay() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				setup = false;
				if (turn) {
					gameScene.setTurnDisplay(player2Name);
					if (devmode)
						turnSwitch.setText(player2Name);
				} else {
					gameScene.setTurnDisplay(player1Name);
					if (devmode)
						turnSwitch.setText(player1Name);
				}
				int[] stoneAmount = getStoneAmount(blackFields);
				gameScene.setPlayer1Stones(String.valueOf(stoneAmount[0]));
				gameScene.setPlayer1Checkers(String.valueOf(stoneAmount[1]));

				stoneAmount = getStoneAmount(whiteFields);
				gameScene.setPlayer2Stones(String.valueOf(stoneAmount[0]));
				gameScene.setPlayer2Checkers(String.valueOf(stoneAmount[1]));
				setup = true;
			}
		});
	}

	public Field[][] getFields() {
		return fields;
	}

	private ImageView[][] getDevFields() {
		ImageView[][] fields = null;
		setup = false;
		try {
			fields = gameScene.getPlaceButtons();
		} catch (WrongUsedMethodException e) {
			e.printStackTrace();
		}
		setup = true;
		return fields;
	}

	protected HashMap<Field, Move> getActiveMoves() {
		return activeMoves;
	}

	public ArrayList<Field> getBlackFields() {
		return blackFields;
	}

	public ArrayList<Field> getWhiteFields() {
		return whiteFields;
	}

	public static boolean isSetup() {
		return setup;
	}

	protected void setActiveField(Field f) {
		activeField = f;
	}

	protected static Board getInstance() {
		return INSTANCE;
	}

}
