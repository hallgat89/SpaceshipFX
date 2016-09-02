package com.github.hallgat89.application;

import com.github.hallgat89.interfaces.HasRect;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class ExplosionVisual implements HasRect {

	final int SPEED = 5;
	final int FRAMEN = 6;
	final int FTIME = 5;
	int timer = 0;
	int currentFrame = 0;
	int spriteWidth;
	int spriteHeight;
	boolean visible = true;

	public ExplosionVisual(Image frames, int x, int y) {
		super();
		spriteWidth = (int) (frames.getWidth() / FRAMEN);
		spriteHeight = (int) frames.getHeight();
		this.frames = new Image[FRAMEN];
		PixelReader r = frames.getPixelReader();
		for (int i = 0; i < FRAMEN; i++) {
			this.frames[i] = new WritableImage(r, i * spriteWidth, 0, spriteWidth, spriteHeight);
		}

		this.x = x;
		this.y = y;
	}

	final Image[] frames;
	int x = 0;
	int y = 0;

	@Override
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;

	}

	@Override
	public void update() {
		y += SPEED;
		if (this.visible) {
			timer++;
			if(timer>=FTIME)
			{
				timer=0;
				currentFrame++;
			}
			if(currentFrame>FRAMEN-1)
			{
				this.visible=false;
			}
		}

	}

	@Override
	public Rectangle2D getFullRect() {
		return new Rectangle2D(getX(), getY(), spriteWidth, spriteHeight);
	}

	@Override
	public Image getImage() {
		return frames[currentFrame];
	}

	@Override
	public int getX() {

		return x - spriteWidth / 2;
	}

	@Override
	public int getY() {
		return y - spriteHeight / 2;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		currentFrame=0;
		timer=0;
		this.visible = visible;

	}

}
