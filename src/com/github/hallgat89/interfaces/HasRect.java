package com.github.hallgat89.interfaces;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public interface HasRect extends Movable {
	
	Rectangle2D getFullRect();

	Image getImage();

	int getX();

	int getY();

}
