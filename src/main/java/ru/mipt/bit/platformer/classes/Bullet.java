package ru.mipt.bit.platformer.classes;

import ru.mipt.bit.platformer.classes.Shootable;
import ru.mipt.bit.platformer.classes.ShootableEntity;

public class Bullet extends ShootableEntity {
    public boolean exploded = false;
    public int explosionLifetimeMax = 25;
    public int explosionLifetime = 0;

    public Bullet(int xCoordinate, int yCoordinate, Direction direction, int damage, float movementSpeed) {
        super(xCoordinate, yCoordinate, movementSpeed);
        this.direction = direction;
        this.damage = damage;
        this.destinationCoordinates = this.coordinates.cpy().add(direction.getDirectionVector());
        this.rotation = this.direction.rotation;
        this.isMoving = true;
    }

    public void onSelfCollidingInto() {
        exploded = true;
        isMoving = false;
        if (!pointBlanc) {
            coordinates.add(this.direction.getDirectionVector());
            destinationCoordinates.add(this.direction.getDirectionVector());
        }
    }

    public void onCollidingIntoSelf(MovableEntity collider) {
        // onSelfCollidingInto();
        return;
    }

    @Override
    public void tick() {
        if (exploded) {
            if (explosionLifetime >= explosionLifetimeMax) {
                explosionLifetime = 0;
            }
            explosionLifetime++;
        }
        else {
            explosionLifetime = 0;
        }
    }
}
