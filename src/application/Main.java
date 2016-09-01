package application;

import java.awt.Color;
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
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

public class Main extends Application {

	// Group root;
	StackPane root;
	Scene theScene;
	Canvas theCanvas;
	GraphicsContext gc;

	int window_x = 800;
	int window_y = 800;

	ShipVisual ship;

	ArrayList<KeyCode> input = new ArrayList<>();

	Queue<RocketVisual> rocketsInUse = new LinkedList<>();
	Queue<RocketVisual> rocketsUnused = new LinkedList<>();

	Image spaceBg;
	BackgroundImage bg;

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

		spaceBg = new Image("spacebg.jpg");
		updateBg();

		ship = new ShipVisual(new Image("fighter_left.png"), new Image("fighter_right.png"),
				new Image("fighter_normal.png"), new Image("exhaust.png"));
		ship.setPos(window_x / 2, window_y / 2);
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
				updateBg();
			}

		});

		theScene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				window_y = newValue.intValue();
				theCanvas.setHeight(window_y);
				updateBg();
			}

		});
	}

	private void updateBg() {
		bg = new BackgroundImage(spaceBg, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
				new BackgroundPosition(Side.LEFT, 0, false, Side.TOP, 0, false),
				new BackgroundSize(window_x, window_y, false, false, false, false));

		root.setBackground(new Background(bg));

	}

	private void mainLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				handleInput();
				// gc.clearRect(0, 0, window_x, window_y);
				clearRockets();
				clearShip(); 
				
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
			if (rv.getY() < -rv.getFullRect().getHeight()) {
				rocketsUnused.add(rv);
				ri.remove();
			}
		}
	}

	private void clearRockets() {
		for (RocketVisual v : rocketsInUse) {
				clearRect(v.getFullRect());
		}
	}

	private void clearShip() {
		clearRect(ship.getFullRect());
	}

	private void clearRect(Rectangle2D rect) {
		gc.clearRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
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

		root = new StackPane();
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
