package de.richardvierhaus.dame;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameEndWindow {

	@FXML
	public Button mainMenu, play;
	public Label winText;

	private static Button MAIN_MENU, PLAY;
	private static Label WIN_TEXT;

	private static GameData data;

	protected static GameData display(GameData data, String winner) {
		Stage window = new Stage();
		GameEndWindow.data = data;

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Spielende");

		try {
			Scene scene = new Scene(FXMLLoader
					.load(MainWindow.class.getClass().getResource("/de/richardvierhaus/dame/GameEndFrame.fxml")));
			window.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}

		WIN_TEXT.setText("Das Spiel ist zu Ende. " + winner + " hat gewonnen!");

		MAIN_MENU.setOnAction(e -> {
			GameEndWindow.data = null;
			window.close();
		});
		PLAY.setOnAction(e -> window.close());

		window.showAndWait();
		return GameEndWindow.data;
	}

	@FXML
	public void initialize() {
		MAIN_MENU = mainMenu;
		PLAY = play;
		WIN_TEXT = winText;
	}

}
