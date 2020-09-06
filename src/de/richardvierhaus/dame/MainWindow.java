package de.richardvierhaus.dame;

import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private static Stage window;
	private static Scene activeScene = null;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		if (activeScene == null) {
			MainWindow.window = primaryStage;
			window.setTitle("Dame");

			loadStartScene();

			window.show();
		}
	}

	protected static void loadStartScene() {
		try {
			MainWindow.activeScene = StartScene.getScene();
		} catch (IOException e) {
			e.printStackTrace();
		}
		window.setScene(activeScene);
	}

	protected static void loadGameScene(boolean devmode) {
		GameData data = GameSetupWindow.display(devmode);
		loadGameScene(data);
	}

	protected static void loadGameScene(GameData data) {
		if (data != null) {
			try {
				MainWindow.activeScene = GameScene.getScene(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			window.setScene(activeScene);
		} else {
			loadStartScene();
		}
	}

	protected static void showAlertBox(String title, String message) {
		Stage window = new Stage();

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(350);

		Label label = new Label();
		label.setText(message);
		Button okButton = new Button("OK");
		okButton.setOnAction(e -> window.close());

		VBox layout = new VBox(10);
		VBox.setMargin(label, new Insets(5, 5, 5, 5));
		VBox.setMargin(okButton, new Insets(5, 5, 5, 5));
		layout.getChildren().addAll(label, okButton);
		layout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();

	}

}
