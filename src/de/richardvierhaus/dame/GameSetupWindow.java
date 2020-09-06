package de.richardvierhaus.dame;

import java.io.File;
import java.io.IOException;

import de.richardvierhaus.dame.pluginLoading.PluginManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameSetupWindow {

	@FXML
	public TextField p1Name, p2Name, p1Path, p2Path;
	public ComboBox<String> p1Combo, p2Combo;
	public Button p1Browse, p2Browse, play, abort;

	private static TextField P1_NAME, P2_NAME, P1_PATH, P2_PATH;
	private static ComboBox<String> P1_COMBO, P2_COMBO;
	private static Button PLAY, ABORT, P1_BROWSE, P2_BROWSE;

	private static GameData gameData;

	protected static GameData display(boolean devmode) {
		gameData = null;
		Stage window = new Stage();

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Spieleinstellungen");

		try {
			Scene scene = new Scene(FXMLLoader
					.load(MainWindow.class.getClass().getResource("/de/richardvierhaus/dame/GameSetupFrame.fxml")));
			window.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}

		P1_BROWSE.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Wähle eine Spielstrategie-Datei aus");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("JAR-Datei", "*.jar"));
			File file = fileChooser.showOpenDialog(window);
			if (file != null)
				P1_PATH.setText(file.getAbsolutePath());
		});

		P2_BROWSE.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Wähle eine Spielstrategie-Datei aus");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("JAR-Datei", "*.jar"));
			File file = fileChooser.showOpenDialog(window);
			if (file != null)
				P2_PATH.setText(file.getAbsolutePath());
		});

		PLAY.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (P1_NAME.getText().length() > 0 && P1_NAME.getText().length() < 17 && P2_NAME.getText().length() > 0
						&& P2_NAME.getText().length() < 17) {
					PluginManager management = new PluginManager();
					boolean ready = true;
					if (P1_COMBO.getSelectionModel().getSelectedItem().equals("Computer")) {
						try {
							management.setPlugin1(management.getPlugin(P1_PATH.getText()));
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException
								| NullPointerException e) {
							ready = false;
							MainWindow.showAlertBox("Achtung",
									"Die für Spieler 1 eingefügte Spielstrategie wurde nicht gefunden oder ist beschädigt");
						}
					}
					if (P2_COMBO.getSelectionModel().getSelectedItem().equals("Computer")) {
						try {
							management.setPlugin2(management.getPlugin(P2_PATH.getText()));
						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException
								| NullPointerException e) {
							ready = false;
							MainWindow.showAlertBox("Achtung",
									"Die für Spieler 2 eingefügte Spielstrategie wurde nicht gefunden oder ist beschädigt");
						}
					}
					if (ready) {
						gameData = new GameData(P1_NAME.getText(), P2_NAME.getText(), management, devmode);
						window.close();
					}
				} else {
					MainWindow.showAlertBox("Achtung", "Der Name der Spieler darf 1-16 Zeichen lang sein");
				}
			}
		});

		ABORT.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				window.close();
			}
		});

		window.showAndWait();

		return gameData;
	}

	@FXML
	public void initialize() {
		P1_NAME = p1Name;
		P2_NAME = p2Name;
		P1_PATH = p1Path;
		P2_PATH = p2Path;
		P1_COMBO = p1Combo;
		P2_COMBO = p2Combo;
		P1_BROWSE = p1Browse;
		P2_BROWSE = p2Browse;

		PLAY = play;
		ABORT = abort;

		p1Combo.getItems().addAll("Spieler", "Computer");
		p1Combo.getSelectionModel().select("Spieler");
		p1Combo.setOnAction(e -> {
			if (p1Combo.getSelectionModel().getSelectedItem().equals("Spieler")) {
				p1Path.setText("");
				p1Path.setDisable(true);
				p1Browse.setDisable(true);
			} else {
				p1Path.setDisable(false);
				p1Browse.setDisable(false);
			}
		});

		p2Combo.getItems().addAll("Spieler", "Computer");
		p2Combo.getSelectionModel().select("Spieler");
		p2Combo.setOnAction(e -> {
			if (p2Combo.getSelectionModel().getSelectedItem().equals("Spieler")) {
				p2Path.setText("");
				p2Path.setDisable(true);
				p2Browse.setDisable(true);
			} else {
				p2Path.setDisable(false);
				p2Browse.setDisable(false);
			}
		});

	}

}
