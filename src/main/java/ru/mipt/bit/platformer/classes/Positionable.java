package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.GameObject;

public abstract class Positionable extends GameObject {
    public GridPoint2 coordinates;

    public Positionable(int xCoordinate, int yCoordinate) {
        this.coordinates = new GridPoint2(xCoordinate, yCoordinate);
    }
}