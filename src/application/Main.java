package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.github.hallgat89.application.RocketSetup;
import com.github.hallgat89.application.RocketVisual;
import com.github.hallgat89.application.ShipVisual;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Main extends Application {

	Group root;
	Scene theScene;
	Canvas theCanvas;
	GraphicsContext gc;

	int window_x = 800;
	int window_y = 800;

	ShipVisual ship;

	ArrayList<KeyCode> input = new ArrayList<>();

	Queue<RocketVisual> rocketsInUse = new LinkedList<>();
	Queue<RocketVisual> rocketsUnused = new LinkedList<>();

	// ArrayList<RocketVisual> rockets = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {

		initEnvironment(primaryStage);
		loadVisuals();
		mainLoop();
		setupKeyHandlers();
		primaryStage.show();
	}

	private void loadVisuals() {
		ship = new ShipVisual(new Image("fighter_left.png"), new Image("fighter_right.png"),
				new Image("fighter_normal.png"), new Image("exhaust.png"));
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

		theScene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				window_x = newValue.intValue();
				theCanvas.setWidth(window_x);
			}

		});

		theScene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				window_y = newValue.intValue();
				theCanvas.setHeight(window_y);
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

				drawRockets();
				drawShip();
				cleanUp();

			}

		}.start();
	}

	private void cleanUp() {
		Iterator<RocketVisual> ri = rocketsInUse.iterator();
		while (ri.hasNext()) {
			RocketVisual rv = ri.next();
			if (rv.getX() < -100) {
				rocketsUnused.add(rv);
				ri.remove();
			}
		}
	}

	private void drawRockets() {

		for (RocketVisual v : rocketsInUse) {
			if (v.hasExhaust()) {
				gc.drawImage(v.getExhaust(), v.getExhaustX(), v.getExhaustY());
			}
			gc.drawImage(v.getImage(), v.getX(), v.getY());
		}
	}

	private void drawShip() {
		gc.drawImage(ship.getExhaust(), ship.getExhaustX(), ship.getExhaustY());
		gc.drawImage(ship.getImage(), ship.getX(), ship.getY());
	}

	private void handleInput() {
		if (input.contains(KeyCode.LEFT)) {
			if (ship.getX() > 0) {
				ship.moveLeft();
			}
		} else if (input.contains(KeyCode.RIGHT)) {
			if (ship.getX() < window_x - ship.getWidth()) {
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
			if (ship.getY() < window_y - ship.getHeight()) {
				ship.moveDown();
			}
		}

		if (input.contains(KeyCode.SPACE)) {
			if (ship.hasRocket()) {
				RocketSetup rs = ship.shootRocket();

				if (rocketsUnused.isEmpty()) {
					RocketVisual newRocket = new RocketVisual(rs.x, rs.y, rs.left, new Image("rocket1.png"),
							new Image("exhaust2.png"));
					rocketsInUse.add(newRocket);
				} else {
					RocketVisual reusedRocket = rocketsUnused.poll();
					reusedRocket.setPos(rs.x, rs.y);
					reusedRocket.setDirection(rs.left);
					rocketsInUse.add(reusedRocket);
				}
			}
		}

	}

	private void initEnvironment(Stage primaryStage) {
		primaryStage.setTitle("Woooosh!");
		primaryStage.setMaximized(true);
		primaryStage.setMinHeight(window_y);
		primaryStage.setMinWidth(window_x);
		
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
