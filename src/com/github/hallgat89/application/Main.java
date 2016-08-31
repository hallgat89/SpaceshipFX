package com.github.hallgat89.application;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	Group root;
	Scene theScene;
	Canvas theCanvas;
	GraphicsContext gc;

	int window_x = 800;
	int window_y = 800;

	ShipVisual ship;

	ArrayList<KeyCode> input = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {

		initEnvironment(primaryStage);
		loadVisuals();
		mainLoop();
		setupKeyHandlers();
		primaryStage.show();
	}

	private void loadVisuals() {
		ship = new ShipVisual(95, 151, new Image("fighter_left.png"), new Image("fighter_right.png"),
				new Image("fighter_normal.png"));
		ship.setPos(400, 400);
	}

	private void setupKeyHandlers() {

		theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				KeyCode code = event.getCode();
				// only add once... prevent duplicates
				if (!input.contains(code))
					input.add(code);

			}
		});

		theScene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				KeyCode code = event.getCode();
				if (input.contains(code))
					input.remove(code);

			}

		});

	}

	private void mainLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				// TODO Auto-generated method stub
				handleInput();
				gc.fillRect(0, 0, window_x, window_y);
				gc.drawImage(ship.getImage(), ship.getX(), ship.getY());
			}
		}.start();
	}

	private void handleInput() {
		if (input.contains(KeyCode.LEFT)) {
			if (ship.getX() > 0) {
				ship.moveLeft();
			}
		} else if (input.contains(KeyCode.RIGHT)) {
			if (ship.getX() < window_x-ship.getWidth()) {
				ship.moveRight();
			}
		} else {
			ship.stop();
		}

		if (input.contains(KeyCode.UP)) {
			if (ship.getY() > 0) {
				ship.moveUp();
			}
		} else if (input.contains(KeyCode.DOWN)) {
			if (ship.getY() < window_y-ship.getHeight()) {
				ship.moveDown();
			}
		}

	}

	private void initEnvironment(Stage primaryStage) {
		primaryStage.setTitle("Woooosh!");
		root = new Group();
		theScene = new Scene(root);
		primaryStage.setScene(theScene);
		theCanvas = new Canvas(window_x, window_y);
		root.getChildren().add(theCanvas);
		gc = theCanvas.getGraphicsContext2D();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
