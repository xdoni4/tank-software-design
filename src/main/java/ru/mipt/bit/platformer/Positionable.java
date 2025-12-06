package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;


public abstract class Positionable extends GameObject {
    public GridPoint2 coordinates;

    public Positionable(int xCoordinate, int yCoordinate) {
        this.coordinates = new GridPoint2(xCoordinate, yCoordinate);
    }
}