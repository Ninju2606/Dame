package de.richardvierhaus.dame;

import java.io.IOException;

import de.richardvierhaus.dame.exceptions.WrongUsedMethodException;
import de.richardvierhaus.dame.mechanics.Board;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class GameScene {

	private static GameScene INSTANCE;

	private ImageView[][] buttons, place;

	@FXML
	public ImageView a0, c0, e0, g0, b1, d1, f1, h1, a2, c2, e2, g2, b3, d3, f3, h3, a4, c4, e4, g4, b5, d5, f5, h5, a6,
			c6, e6, g6, b7, d7, f7, h7, placeStoneWhite, placeCheckerWhite, placeStoneBlack, placeCheckerBlack;
	@FXML
	public Label p1_name, p2_name, p1_stones, p2_stones, p1_checkers, p2_checkers, turnDisplay;
	@FXML
	public HBox devPane;
	@FXML
	public Button devModeSwitch, turnSwitch, back;

	protected static Scene getScene(GameData data) throws IOException {
		Scene scene = new Scene(
				FXMLLoader.load(MainWindow.class.getClass().getResource("/de/richardvierhaus/dame/GameFrame.fxml")));
		new Board(data, INSTANCE);
		return scene;
	}

	public void showAlertBox(String title, String message, ImageView[][] proof) {
		if (proof == place)
			MainWindow.showAlertBox(title, message);
		else
			throw new WrongUsedMethodException(
					"Method: showAlertBox(title:String, message:String, proof:ImageView[][]):void is only for programm");
	}

	public void endGame(GameData data, String winner, ImageView[][] proof) {
		if (proof == place)
			MainWindow.loadGameScene(GameEndWindow.display(data, winner));
		else
			throw new WrongUsedMethodException(
					"Method: endGame(data:GameData, winner:String, proof:ImageView[][]):void is only for programm");
	}

	public void backToMainMenu(ImageView[][] proof) {
		if (proof == place)
			MainWindow.loadStartScene();
		else
			throw new WrongUsedMethodException("Method: backToMainMenu(proof:ImageView[][]):void is only for programm");
	}

	@FXML
	public void initialize() {
		INSTANCE = this;

		buttons = new ImageView[8][8];
		buttons[0][0] = a0;
		buttons[2][0] = c0;
		buttons[4][0] = e0;
		buttons[6][0] = g0;
		buttons[1][1] = b1;
		buttons[3][1] = d1;
		buttons[5][1] = f1;
		buttons[7][1] = h1;
		buttons[0][2] = a2;
		buttons[2][2] = c2;
		buttons[4][2] = e2;
		buttons[6][2] = g2;
		buttons[1][3] = b3;
		buttons[3][3] = d3;
		buttons[5][3] = f3;
		buttons[7][3] = h3;
		buttons[0][4] = a4;
		buttons[2][4] = c4;
		buttons[4][4] = e4;
		buttons[6][4] = g4;
		buttons[1][5] = b5;
		buttons[3][5] = d5;
		buttons[5][5] = f5;
		buttons[7][5] = h5;
		buttons[0][6] = a6;
		buttons[2][6] = c6;
		buttons[4][6] = e6;
		buttons[6][6] = g6;
		buttons[1][7] = b7;
		buttons[3][7] = d7;
		buttons[5][7] = f7;
		buttons[7][7] = h7;

		place = new ImageView[2][2];
		place[0][1] = placeCheckerBlack;
		place[1][1] = placeCheckerWhite;
		place[0][0] = placeStoneBlack;
		place[1][0] = placeStoneWhite;
	}

	public void showDevPanel(boolean var) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: showDevPanel(var: boolean):void is only for programm");
		devPane.setVisible(var);
	}

	public ImageView[][] getButtons() {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: getButtons():ImageView[][] is only for programm");
		return buttons;
	}

	public void setPlayer1Name(String name) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: setPlayer1Name(name: String):void is only for programm");
		p1_name.setText(name);
	}

	public void setPlayer2Name(String name) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: setPlayer2Name(name: String):void is only for programm");
		p2_name.setText(name);
	}

	public void setPlayer1Stones(String amount) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: setPlayer1Stones(amount: String):void is only for programm");
		p1_stones.setText(amount);
	}

	public void setPlayer2Stones(String amount) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: setPlayer2Stones(amount: String):void is only for programm");
		p2_stones.setText(amount);
	}

	public void setPlayer1Checkers(String amount) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: setPlayer1Checkers(amount: String):void is only for programm");
		p1_checkers.setText(amount);
	}

	public void setPlayer2Checkers(String amount) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: setPlayer2Checkers(amount: String):void is only for programm");
		p2_checkers.setText(amount);
	}

	public void setTurnDisplay(String name) {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: setTurnDisplay(name: String):void is only for programm");
		turnDisplay.setText(name + " ist am Zug");
	}

	public Button getTurnSwitch() {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: getTurnSwitch():Button is only for programm");
		return turnSwitch;
	}

	public Button getDevmodeSwitch() {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: getDevmodeSwitch():Button is only for programm");
		return devModeSwitch;
	}

	public ImageView[][] getPlaceButtons() {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: getPlaceButtons():ImageView[][] is only for programm");
		return place;
	}

	public Button getBackButton() {
		if (Board.isSetup())
			throw new WrongUsedMethodException("Method: getBackButton():Button is only for programm");
		return back;
	}

}
