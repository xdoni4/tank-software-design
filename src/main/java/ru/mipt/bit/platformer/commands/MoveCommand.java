package ru.mipt.bit.platformer.commands;


import java.util.ArrayList;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.Direction;
import ru.mipt.bit.platformer.Positionable;
import ru.mipt.bit.platformer.MovableEntity;
import ru.mipt.bit.platformer.Bullet;
import ru.mipt.bit.platformer.Obstacle;
import ru.mipt.bit.platformer.CollisionHandler;


public class MoveCommand implements Command {
    public MovableEntity entity;
    public Direction direction;
    private CollisionHandler collisionHandler;

    public MoveCommand(
        MovableEntity entity,
        Direction dir,
        CollisionHandler collisionHandler
    ) {
        this.entity = entity;
        this.direction = dir;
        this.collisionHandler = collisionHandler;
    }

    @Override
    public void execute() {
        if (entity.isMoving) {
            entity.rotation = direction.getRotation();
            if (!collisionHandler.collidesWithObstacles(entity, direction)) {
                entity.destinationCoordinates.add(direction.getDirectionVector());
            }
            else {
                entity.onSelfCollidingInto();
            }
            entity.movementProgress = 0f;
            entity.updateDirection(direction);
        }
    }
}
