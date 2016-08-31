package com.github.hallgat89.application;

import javafx.scene.image.Image;

public class RocketVisual {

	final int DEFAULT_EXHAUST_OFFSET = 70;
	final int EXHAUSTW;
	final int VSPEED = 15;
	final int HSPEED = 1;
	final int INACTIVETIME = 50;

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
		this.EXHAUSTW=(int) exhaust.getWidth();
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

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return this.x - (width / 2);
	}

	public int getY() {
		return this.y - (height / 2);
	}

	public Image getImage() {
		updateSprite();
		return this.image;
	}

	private void updateSprite() {
		counter++;
		if (counter >= INACTIVETIME && rocketState != State.THRUST ){
			rocketState = State.THRUST;
		} else {
			if (rocketState == State.LEFT) {
				this.x-=HSPEED;
			}
			if (rocketState == State.RIGHT) {
				this.x+=HSPEED;
			}
		}
		if (rocketState==State.THRUST)
		{
			this.y-=VSPEED;
		}

	}

	public Image getExhaust() {
		return exhaust;
	}

	public int getExhaustX() {
		return this.x-EXHAUSTW/2;
	}

	public int getExhaustY() {
		return this.getY() + exhaustOffset + DEFAULT_EXHAUST_OFFSET;
	}
	
	public boolean hasExhaust()
	{
		return rocketState==State.THRUST;
	}

}
