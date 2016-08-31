package com.github.hallgat89.application;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class ShipVisual {

	final int FRAMESPEED = 5;
	final int FRAMES = 4; // n-1
	final int ACC = 10;
	final int SIDESPEED = 5;
	final int FORESPEED = 10;
	final int EXHAUSTW=25;
	final int width;
	final int height;
	final int DEFAULT_EXHAUST_OFFSET=50;
	int exhaustOffset=0;
	int frameCounter = 0;
	int currentFrame = 0;
	int currentAcceleration = 0;
	final Image spritesLeft;
	final Image spritesRight;
	final Image spriteNormal;
	final Image exhaust;
	State animationState;
	int x = 0;
	int y = 0;
	boolean rocketDirection=true; //left-right

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

	private void animRight() {
		if (animationState != State.NORMAL_RIGHT  && animationState!=State.RIGHTMOST) {
			currentFrame = 0;
			frameCounter = 0;
			this.animationState = State.NORMAL_RIGHT;
		}
	}

	private void animLeft() {
		if (animationState != State.NORMAL_LEFT && animationState!=State.LEFTMOST) {
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

	public ShipVisual(Image spritesLeft, Image spritesRight, Image spriteNormal,Image exhaust) {
		super();
		this.exhaust=exhaust;
		this.width = (int)spriteNormal.getWidth();
		this.height = (int)spriteNormal.getHeight();
		this.spritesLeft = spritesLeft;
		this.spritesRight = spritesRight;
		this.spriteNormal = spriteNormal;
		this.animationState = State.getDefault();
	}

	private void updateSprite() {
		frameCounter++;
		if (frameCounter >= FRAMESPEED) {
			frameCounter = 0;
			if (currentFrame < FRAMES) {
				currentFrame++;
			} else {
				currentFrame = 0;
				if (animationState.hasNextState()) {
					animationState = animationState.getNextState();
				}
			}
		}
	}
	
	public Image getExhaust()
	{
		return exhaust;
	}
	
	public int getExhaustX()
	{
		return this.x-EXHAUSTW/2;
	}
	
	public int getExhaustY()
	{
		return this.y+exhaustOffset+DEFAULT_EXHAUST_OFFSET;
	}

	public Image getImage() {
		updateSprite();
		Image imageToReturn;
		PixelReader reader;
		switch (this.animationState) {
		case NORMAL:
			imageToReturn = spriteNormal;
			break;
		case LEFTMOST:
			reader = spritesLeft.getPixelReader();
			imageToReturn = new WritableImage(reader, FRAMES * width, 0, width, height);
			break;
		case RIGHTMOST:
			reader = spritesRight.getPixelReader();
			imageToReturn = new WritableImage(reader, FRAMES * width, 0, width, height);
			break;
		case NORMAL_LEFT:
			reader = spritesLeft.getPixelReader();
			imageToReturn = new WritableImage(reader, width * currentFrame, 0, width, height);
			break;
		case NORMAL_RIGHT:
			reader = spritesRight.getPixelReader();
			imageToReturn = new WritableImage(reader, width * currentFrame, 0, width, height);
			break;
		case LEFT_NORMAL:
			reader = spritesLeft.getPixelReader();
			imageToReturn = new WritableImage(reader, (FRAMES * width) - (width * currentFrame), 0, width, height);
			break;
		case RIGHT_NORMAL:
			reader = spritesRight.getPixelReader();
			imageToReturn = new WritableImage(reader, (FRAMES * width) - (width * currentFrame), 0, width, height);
			break;
		default:
			throw new IllegalStateException();
		}
		return imageToReturn;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return this.x-(width/2);
	}

	public int getY() {
		return this.y-(height/2);
	}

	public void moveUp() {
		exhaustOffset=+20;
		y -= FORESPEED;
	}

	public void moveDown() {
		exhaustOffset=-20;
		y += SIDESPEED;
	}

	public void moveLeft() {
		exhaustOffset=0;
		x -= SIDESPEED;
		animLeft();
	}

	public void moveRight() {
		exhaustOffset=0;
		x += SIDESPEED;
		animRight();
	}

	public void stop() {
		exhaustOffset=0;
		animBackToNormal();
	}
	
	public RocketSetup shootRocket()
	{
		rocketDirection=!rocketDirection;
		 return new RocketSetup(this.x, this.y, rocketDirection);
	}
}
