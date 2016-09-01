package com.github.hallgat89.application;

import com.github.hallgat89.interfaces.HasRect;
import com.github.hallgat89.interfaces.HasTargetRect;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class AsteroidVisual implements HasRect, HasTargetRect {

	final int SPEED = 5;
	final Image image;
	int x = 0;
	int y = 0;

	public AsteroidVisual(Image i) {
		this.image = i;
	}

	@Override
	public int getX() {
		return (int) (x - image.getWidth() / 2);
	}



	@Override
	public int getY() {
		return (int) (y - image.getHeight() / 2);
	}

	public Image getImage() {
		updateSprite();
		return image;
	}

	private void updateSprite() {
		this.y += SPEED;
	}

	@Override
	public Rectangle2D getRect() {
		return getFullRect();
	}

	@Override
	public Rectangle2D getFullRect() {
		return new Rectangle2D(getX(), getY(), image.getWidth(), image.getHeight());
	}

	public int getWidth() {
		return (int) image.getWidth();
	}

	public int getHeight() {
		return (int) image.getHeight();
	}

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		
	}

}
