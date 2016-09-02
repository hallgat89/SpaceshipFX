package application;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.github.hallgat89.application.AsteroidVisual;
import com.github.hallgat89.application.CloudVisual;
import com.github.hallgat89.application.ExplosionVisual;
import com.github.hallgat89.application.RocketSetup;
import com.github.hallgat89.application.RocketVisual;
import com.github.hallgat89.application.ShipVisual;
import com.github.hallgat89.interfaces.HasRect;

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

	final int CLOUDSBACK = 1;
	final int CLOUDSFORE = 2;
	final int ASTEROIDS = 10;

	// Group root;
	StackPane root;
	Scene theScene;
	Canvas theCanvas;
	GraphicsContext gc;

	Random rnd = new Random();

	int window_x = 800;
	int window_y = 800;

	ShipVisual ship;

	List<KeyCode> input = new ArrayList<>();

	Queue<RocketVisual> rocketsInUse = new LinkedList<>();
	Queue<RocketVisual> rocketsUnused = new LinkedList<>();

	Queue<ExplosionVisual> explosionsInUse = new LinkedList<>();
	Queue<ExplosionVisual> explosionsUnused = new LinkedList<>();

	Image spaceBg;
	BackgroundImage bg;

	List<HasRect> backgroundElements = new ArrayList<>();
	List<HasRect> foregroundElements = new ArrayList<>();

	Image explosionFrames = new Image("explosion.png");

	@Override
	public void start(Stage primaryStage) {

		initEnvironment(primaryStage);
		loadVisuals();
		mainLoop();
		setupKeyHandlers();
		primaryStage.show();
	}

	private void loadVisuals() {

		final Image cim = new Image("cloud.png");
		final Image ast1 = new Image("asteroid1.png");

		for (int i = 0; i < CLOUDSBACK; i++) {
			CloudVisual cloud = new CloudVisual(cim);
			cloud.setPosition(rnd.nextInt(window_x), rnd.nextInt(window_y));
			backgroundElements.add(cloud);
		}
		for (int i = 0; i < CLOUDSFORE; i++) {
			CloudVisual cloud = new CloudVisual(cim);
			cloud.setPosition(rnd.nextInt(window_x), rnd.nextInt(window_y));
			foregroundElements.add(cloud);
		}

		for (int i = 0; i < ASTEROIDS; i++) {
			AsteroidVisual asteroid = new AsteroidVisual(ast1);
			asteroid.setPosition(-asteroid.getWidth(), rnd.nextInt(window_y));
			backgroundElements.add(asteroid);
		}

		spaceBg = new Image("spacebg.jpg");
		updateBg();

		ship = new ShipVisual(new Image("fighter_left.png"), new Image("fighter_right.png"),
				new Image("fighter_normal.png"), new Image("exhaust.png"));
		ship.setPosition(window_x / 2, window_y / 2);
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
				new BackgroundSize(window_x, window_y, false, false, true, false));

		root.setBackground(new Background(bg));

	}

	private void mainLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				handleInput();

				clearRockets();
				clearShip();
				clearExtras();
				clearExplosions();

				drawBackgroundExtras();
				drawRockets();
				drawShip();
				drawExplosions();
				drawForegroundExtras();

				collisionTest();

				cleanUp();
			}

		}.start();
	}

	protected void drawExplosions() {

		for (ExplosionVisual v : explosionsInUse) {
			v.update();
			if (v.isVisible()) {
				gc.drawImage(v.getImage(), v.getX(), v.getY());
			}
		}
	}

	protected void clearExplosions() {
		for (ExplosionVisual v : explosionsInUse) {
			clearRect(v.getFullRect());
		}

	}

	private void collisionTest() {
		for (HasRect obstacle : backgroundElements) {
			if (obstacle.isVisible() && obstacle instanceof AsteroidVisual) {
				AsteroidVisual ca = (AsteroidVisual) obstacle;
				for (RocketVisual rocket : rocketsInUse) {
					if (rocket.isVisible() && rocket.getRect().intersects(((AsteroidVisual) obstacle).getRect())) {
						// BOOOM!!!
						if (explosionsUnused.isEmpty()) {
							ExplosionVisual nev = new ExplosionVisual(explosionFrames, ca.getCenterX(),
									ca.getCenterY());
							explosionsInUse.add(nev);
						} else {
							ExplosionVisual ev = explosionsUnused.poll();
							ev.setVisible(true);
							ev.setPosition(ca.getCenterX(), ca.getCenterY());
							explosionsInUse.add(ev);
						}
						rocket.setVisible(false);
						obstacle.setVisible(false);
					}
				}
			}
		}
	}

	private void drawBackgroundExtras() {
		for (HasRect b : backgroundElements) {
			b.update();
			if (b.isVisible()) {
				gc.drawImage(b.getImage(), b.getX(), b.getY());
			}
		}
	}

	private void drawForegroundExtras() {
		for (HasRect f : foregroundElements) {
			f.update();
			if (f.isVisible()) {
				gc.drawImage(f.getImage(), f.getX(), f.getY());
			}
		}
	}

	private void clearExtras() {
		for (HasRect v : backgroundElements) {
			clearRect(v.getFullRect());
		}
		for (HasRect v : foregroundElements) {
			clearRect(v.getFullRect());
		}
	}

	private void cleanUp() {
		Iterator<RocketVisual> ri = rocketsInUse.iterator();
		while (ri.hasNext()) {
			RocketVisual rv = ri.next();
			if (rv.getY() < -rv.getFullRect().getHeight()) {
				rocketsUnused.add(rv);
				rv.setVisible(true);
				ri.remove();
			}
		}

		Iterator<ExplosionVisual> ei = explosionsInUse.iterator();
		while (ei.hasNext()) {
			ExplosionVisual rv = ei.next();
			if (rv.getY() < -rv.getFullRect().getHeight()) {
				explosionsUnused.add(rv);
				rv.setVisible(true);
				ei.remove();
			}
		}

		for (HasRect v : backgroundElements) {
			if (v.getY() > window_y) {
				v.setPosition(rnd.nextInt(window_x * 2) - window_x, (int) -v.getFullRect().getHeight());
				v.setVisible(true);
			}
		}

		for (HasRect v : foregroundElements) {
			if (v.getY() > window_y) {
				v.setPosition(rnd.nextInt(window_x * 2) - window_x, (int) -v.getFullRect().getHeight());
				v.setVisible(true);
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
			v.update();
			if (v.isVisible()) {
				if (v.hasExhaust()) {
					gc.drawImage(v.getExhaust(), v.getExhaustX(), v.getExhaustY());
				}
				gc.drawImage(v.getImage(), v.getX(), v.getY());
			}
		}
	}

	private void drawShip() {
		ship.update();
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
					reusedRocket.setPosition(rs.x, rs.y);
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
