package ru.mipt.bit.platformer.classes;

import ru.mipt.bit.platformer.classes.MovableEntity;

public abstract class ShootableEntity extends MovableEntity implements Shootable {
    public float damage = 10;

    ShootableEntity(int xCooordinate, int yCoordinate, float movementSpeed) {
        super(xCooordinate, yCoordinate, movementSpeed);
    }
}
