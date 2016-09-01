package com.github.hallgat89.application;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class CloudVisual {

	public CloudVisual(int x, int y, Image image) {
		super();
		this.x = x;
		this.y = y;
		this.image = image;
	}

	public CloudVisual(javafx.scene.image.Image image2) {
		super();

		this.image = image2;
	}

	final int SPEED = 10;
	int x = 0;
	int y = 0;

	final Image image;

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Image getImage() {
		updatePosition();
		return this.image;
	}

	private void updatePosition() {
		this.y += SPEED;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return (int) image.getWidth();
	}

	public int getHeight() {
		return (int) image.getHeight();
	}

	public Rectangle2D getFullRect()
	{
		return new Rectangle2D(x, y, getWidth(), getHeight());
	}
}
