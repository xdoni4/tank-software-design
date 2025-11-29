package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.MovableEntity;
import ru.mipt.bit.platformer.classes.Shooter;
import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.Bullet;
import ru.mipt.bit.platformer.classes.HasHealth;

public class Tank extends MovableEntity implements Shooter, HasHealth {
    private float currentHealth = 100;
    private float maxHealth = 100;
    public int shootCoolDown = 0;
    public int shootCoolDownMax = 100;

    public Tank(int xCooordinate, int yCoordinate, float movementSpeed) {
        super(xCooordinate, yCoordinate, movementSpeed);
    }

    @Override
    public ShootableEntity shoot() {
        GridPoint2 destinationWithDirection = destinationCoordinates.cpy().add(direction.getDirectionVector());
        ShootableEntity projectile = new Bullet(
            coordinates.x,
            coordinates.y,
            direction,
            10,
            0.3f
        );
        return projectile;
    }

    @Override
    public void onCollidingIntoSelf(MovableEntity collider) {
        if (collider instanceof ShootableEntity) {
            updateHealth(-((ShootableEntity)collider).damage);
        }
    }

    @Override
    public float getHealth() {
        return currentHealth;
    }
    
    @Override
    public float getMaxHealth() {
        return maxHealth;
    }

    @Override 
    public void updateHealth(float diff) {
        currentHealth += diff;
        currentHealth = currentHealth < 0 ? 0 : currentHealth;
    }

    @Override
    public void tick() {
        if (shootCoolDown > 0) {
            shootCoolDown--;
        }
    }
}