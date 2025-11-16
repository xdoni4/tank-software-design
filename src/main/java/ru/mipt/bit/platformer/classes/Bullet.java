package ru.mipt.bit.platformer.classes;

import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.MovableEntity;

public class Bullet extends MovableEntity implements ShootableEntity {
    public Bullet(int xCoordinate, int yCoordinate, float movementSpeed) {
        super(xCoordinate, yCoordinate, movementSpeed);
    }
}
