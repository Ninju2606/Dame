package de.richardvierhaus.dame;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class StartScene {
	
	protected static Scene getScene() throws IOException {
		return new Scene(FXMLLoader.load(MainWindow.class.getClass().getResource("/de/richardvierhaus/dame/MainFrame.fxml")));
	}
	
	public void play() {
		MainWindow.loadGameScene(false);
	}
	
	public void startDeveloperMode() {
		MainWindow.loadGameScene(true);
	}

}
