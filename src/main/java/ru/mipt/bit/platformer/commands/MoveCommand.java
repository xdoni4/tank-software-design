package ru.mipt.bit.platformer.commands;
import ru.mipt.bit.platformer.commands.Command;


import java.util.ArrayList;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.MovableEntity;
import ru.mipt.bit.platformer.classes.Bullet;
import ru.mipt.bit.platformer.classes.Obstacle;
import ru.mipt.bit.platformer.classes.CollisionHandler;


public class MoveCommand implements Command {
    public MovableEntity entity;
    public Direction direction;
    private CollisionHandler collisionHandler;
    // private ArrayList<Positionable> obstacles;
    // public ArrayList<Integer> idxsToSkip;

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
