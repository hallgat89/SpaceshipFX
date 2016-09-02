package com.github.hallgat89.application;

import com.github.hallgat89.interfaces.HasRect;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class CloudVisual implements HasRect {

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
	boolean visible=true;
	final Image image;

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public Image getImage() {
		updatePosition();
		return this.image;
	}

	private void updatePosition() {
		this.y += SPEED;
	}
	@Override
	public int getX() {
		return x;
	}
	@Override
	public int getY() {
		return y;
	}

	public int getWidth() {
		return (int) image.getWidth();
	}

	public int getHeight() {
		return (int) image.getHeight();
	}

	@Override
	public Rectangle2D getFullRect()
	{
		return new Rectangle2D(x, y, getWidth(), getHeight());
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible=visible;
	}
}
