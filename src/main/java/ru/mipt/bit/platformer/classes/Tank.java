package ru.mipt.bit.platformer.classes;
import ru.mipt.bit.platformer.classes.MovableEntity;

public class Tank extends MovableEntity {
    public int currentHealth = 100;
    public int maxHealth = 100;

    public Tank(int xCooordinate, int yCoordinate, float movementSpeed) {
        super(xCooordinate, yCoordinate, movementSpeed);
    }
}