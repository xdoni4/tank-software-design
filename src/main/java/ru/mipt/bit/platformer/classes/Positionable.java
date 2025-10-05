package ru.mipt.bit.platformer.classes;
import com.badlogic.gdx.math.GridPoint2;

public class Positionable {
    public GridPoint2 coordinates;

    public Positionable(int xCoordinate, int yCoordinate) {
        this.coordinates = new GridPoint2(xCoordinate, yCoordinate);
    }
}