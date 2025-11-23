package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.MovableEntity;
import ru.mipt.bit.platformer.classes.ShooterEntity;
import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.Bullet;

public class Tank extends MovableEntity implements ShooterEntity {
    public int currentHealth = 100;
    public int maxHealth = 100;

    public Tank(int xCooordinate, int yCoordinate, float movementSpeed) {
        super(xCooordinate, yCoordinate, movementSpeed);
    }

    @Override
    public ShootableEntity shoot() {
        GridPoint2 destinationWithDirection = destinationCoordinates.cpy().add(direction.getDirectionVector());

        ShootableEntity projectile = new Bullet(
            destinationCoordinates.x,
            destinationCoordinates.y,
            direction,
            10,
            0.3f
        );
        return projectile;
    }
}