package ru.mipt.bit.platformer.classes;

import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.MovableEntity;

public class Bullet extends MovableEntity implements ShootableEntity {
    public Direction direction;
    private int damage;
    public boolean exploded = false;

    public Bullet(int xCoordinate, int yCoordinate, Direction direction, int damage, float movementSpeed) {
        super(xCoordinate, yCoordinate, movementSpeed);
        this.direction = direction;
        this.damage = damage;
        this.destinationCoordinates = this.coordinates.cpy().add(direction.getDirectionVector());
        this.rotation = this.direction.rotation;
        this.isMoving = true;
    }

    public void on_collision_do() {
        exploded = true;
    }
}
