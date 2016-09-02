package com.github.hallgat89.application;

import java.nio.channels.IllegalSelectorException;

import com.github.hallgat89.interfaces.HasRect;
import com.github.hallgat89.interfaces.HasTargetRect;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class ShipVisual implements HasTargetRect {

	final int FRAMESPEED = 5;
	final int FRAMES = 5;
	final int MAXACC = 10;
	final int SIDESPEED = 5;
	final int FORESPEED = 10;
	final int RELOADTIME = 30;

	final int width;
	final int height;
	final int DEFAULT_EXHAUST_OFFSET = 50;

	int exhaustOffset = 0;
	int frameCounter = 0;
	int currentFrame = 0;
	int currentAcceleration = 0;
	int reloadCounter = 0;
	boolean rocket = true;
	boolean visible = true;

	final Image[] spritesLeft;
	final Image[] spritesRight;
	final Image spriteNormal;
	final Image exhaust;
	Image currentImage;
	State animationState;
	int x = 0;
	int y = 0;
	boolean rocketDirection = true; // left-right

	enum State {
		NORMAL, LEFTMOST, RIGHTMOST, NORMAL_LEFT, NORMAL_RIGHT, LEFT_NORMAL, RIGHT_NORMAL;

		static {
			NORMAL.nextState = null;
			LEFTMOST.nextState = null;
			RIGHTMOST.nextState = null;
			NORMAL_LEFT.nextState = LEFTMOST;
			NORMAL_RIGHT.nextState = RIGHTMOST;
			LEFT_NORMAL.nextState = NORMAL;
			RIGHT_NORMAL.nextState = NORMAL;
			defaultState = NORMAL;
		}

		static final State defaultState;
		State nextState;

		public State getNextState() {
			if (this.nextState == null) {
				throw new IllegalStateException();
			} else {
				return nextState;
			}
		}

		public static State getDefault() {
			return defaultState;
		}

		public boolean hasNextState() {
			return nextState != null;
		}

	};

	public ShipVisual(Image spritesLeft, Image spritesRight, Image spriteNormal, Image exhaust) {
		super();
		this.exhaust = exhaust;
		this.width = (int) spriteNormal.getWidth();
		this.height = (int) spriteNormal.getHeight();
		this.spriteNormal = spriteNormal;
		this.currentImage = spriteNormal;
		this.spritesLeft = new Image[FRAMES];
		PixelReader readerLeft = spritesLeft.getPixelReader();
		for (int i = 0; i < FRAMES; i++) {
			this.spritesLeft[i] = new WritableImage(readerLeft, i * width, 0, width, height);
		}

		this.spritesRight = new Image[FRAMES];
		PixelReader readerRight = spritesRight.getPixelReader();
		for (int i = 0; i < FRAMES; i++) {
			this.spritesRight[i] = new WritableImage(readerRight, i * width, 0, width, height);
		}

		this.animationState = State.getDefault();
	}

	private void animRight() {
		if (animationState != State.NORMAL_RIGHT && animationState != State.RIGHTMOST) {
			currentFrame = 0;
			frameCounter = 0;
			this.animationState = State.NORMAL_RIGHT;
		}
	}

	private void animLeft() {
		if (animationState != State.NORMAL_LEFT && animationState != State.LEFTMOST) {
			currentFrame = 0;
			frameCounter = 0;
			this.animationState = State.NORMAL_LEFT;
		}
	}

	private void animBackToNormal() {
		if (animationState == State.LEFTMOST) {
			currentFrame = 0;
			frameCounter = 0;
			this.animationState = State.LEFT_NORMAL;
		}
		if (animationState == State.RIGHTMOST) {
			currentFrame = 0;
			frameCounter = 0;
			this.animationState = State.RIGHT_NORMAL;
		}
	}

	private boolean updateSprite() {
		frameCounter++;
		boolean wasupdate = false;
		if (!rocket)
			reloadCounter++;
		if (reloadCounter > RELOADTIME) {
			rocket = true;
			reloadCounter = 0;
		}

		if (frameCounter >= FRAMESPEED) {
			wasupdate = true;
			frameCounter = 0;
			if (currentFrame < FRAMES - 1) {
				currentFrame++;
			} else {
				currentFrame = 0;
				if (animationState.hasNextState()) {
					animationState = animationState.getNextState();
				}
			}
		}
		return wasupdate;
	}

	public Image getExhaust() {
		return exhaust;
	}

	public int getExhaustX() {
		return (int) (this.x + -exhaust.getWidth() / 2);
	}

	public int getExhaustY() {
		return this.y + exhaustOffset + DEFAULT_EXHAUST_OFFSET;
	}

	@Override
	public Rectangle2D getRect() {
		return new Rectangle2D(getX(), getY(), width, height);
	}

	@Override
	public Rectangle2D getFullRect() {
		return new Rectangle2D(getX(), getY(), width, height + exhaust.getHeight() + 5);
	}

	@Override
	public Image getImage() {
		if (updateSprite()) {

			switch (this.animationState) {
			case NORMAL:
				currentImage = spriteNormal;
				break;
			case LEFTMOST:
				currentImage = spritesLeft[spritesLeft.length - 1];
				break;
			case RIGHTMOST:
				currentImage = spritesRight[spritesRight.length - 1];
				break;
			case NORMAL_LEFT:
				currentImage = spritesLeft[currentFrame];
				break;
			case NORMAL_RIGHT:
				currentImage = spritesRight[currentFrame];
				break;
			case LEFT_NORMAL:
				currentImage = spritesLeft[FRAMES - currentFrame];
				break;
			case RIGHT_NORMAL:
				currentImage = spritesRight[FRAMES - currentFrame];
				break;
			default:
				throw new IllegalStateException();
			}
		}
		if (currentImage == null) {
			throw new IllegalStateException("Current image NULL!");
		}
		return currentImage;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getX() {
		return this.x - (width / 2);
	}

	@Override
	public int getY() {
		return this.y - (height / 2);
	}

	public void moveUp() {
		exhaustOffset = +20;
		y -= FORESPEED;
	}

	public void moveDown() {
		exhaustOffset = -20;
		y += SIDESPEED;
	}

	public void moveLeft() {
		exhaustOffset = 0;
		x -= SIDESPEED;
		animLeft();
	}

	public void moveRight() {
		exhaustOffset = 0;
		x += SIDESPEED;
		animRight();
	}

	public void stop() {
		exhaustOffset = 0;
		animBackToNormal();
	}

	public boolean hasRocket() {
		return rocket;
	}

	public RocketSetup shootRocket() {
		rocket = false;
		if (animationState.equals(State.NORMAL)) {
			rocketDirection = !rocketDirection;
		}
		if (animationState.equals(State.LEFTMOST) || animationState.equals(State.NORMAL_LEFT)) {
			rocketDirection = true;
		}
		if (animationState.equals(State.RIGHTMOST) || animationState.equals(State.NORMAL_RIGHT)) {
			rocketDirection = false;
		}
		return new RocketSetup(this.x, this.y, rocketDirection);
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;

	}

	@Override
	public int getCenterX() {
		return x;
	}

	@Override
	public int getCenterY() {
		return y;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
