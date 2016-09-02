package com.github.hallgat89.application;

import com.github.hallgat89.interfaces.HasRect;
import com.github.hallgat89.interfaces.HasTargetRect;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class RocketVisual implements HasTargetRect {

	// final int DEFAULT_EXHAUST_OFFSET = 70;
	final int VSPEED = 15;
	final int HSPEED = 1;
	final int INACTIVETIME = 50;
	boolean visible = true;

	int counter = 0;

	int exhaustOffset = 0;
	int width;
	int height;
	int x = 0;
	int y = 0;
	State rocketState;
	final Image image;
	final Image exhaust;

	public RocketVisual(int x, int y, boolean rocketState, Image image, Image exhaust) {
		super();
		this.width = (int) image.getWidth();
		this.height = (int) image.getHeight();
		this.x = x;
		this.y = y;
		if (rocketState)
			this.rocketState = State.LEFT;
		else
			this.rocketState = State.RIGHT;
		this.image = image;
		this.exhaust = exhaust;
	}

	enum State {
		LEFT, RIGHT, THRUST
	};

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

	@Override
	public Image getImage() {
		updateSprite();
		return this.image;
	}

	private void updateSprite() {
		counter++;
		if (counter >= INACTIVETIME && rocketState != State.THRUST) {
			rocketState = State.THRUST;
		} else {
			if (rocketState == State.LEFT) {
				this.x -= HSPEED;
			}
			if (rocketState == State.RIGHT) {
				this.x += HSPEED;
			}
		}
		if (rocketState == State.THRUST) {
			this.y -= VSPEED;
		}

	}

	public Image getExhaust() {
		return exhaust;
	}

	public int getExhaustX() {
		return (int) (this.x - exhaust.getWidth() / 2);
	}

	public int getExhaustY() {
		// return this.getY() + exhaustOffset + DEFAULT_EXHAUST_OFFSET;
		return this.getY() + this.height;
	}

	@Override
	public Rectangle2D getRect() {
		return new Rectangle2D(getX(), getY(), width, height);
	}

	@Override
	public Rectangle2D getFullRect() {
		int w = (int) Math.max(this.width, exhaust.getWidth());
		int h = (int) (this.height + exhaust.getHeight() + 20);
		return new Rectangle2D(this.x - w / 2, this.y - height / 2, w, h);

	}

	public boolean hasExhaust() {
		return rocketState == State.THRUST;
	}

	public void setDirection(boolean isLeft) {
		if (isLeft)
			rocketState = State.LEFT;
		else
			rocketState = State.RIGHT;
		counter = 0;
		exhaustOffset = 0;

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
