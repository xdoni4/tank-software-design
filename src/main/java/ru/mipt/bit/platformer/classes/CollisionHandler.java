package ru.mipt.bit.platformer.classes;

import java.util.ArrayList;

import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.MovableEntity;
import ru.mipt.bit.platformer.classes.ShootableEntity;

public class CollisionHandler {
    private ArrayList<Positionable> obstacles;
    private ArrayList<Integer> idxsToSkip;

    public CollisionHandler(ArrayList<Positionable> obstacles, ArrayList<Integer> idxsToSkip) {
        this.obstacles = obstacles;
        this.idxsToSkip = idxsToSkip;
    }

    private boolean collidesWithObstacle(MovableEntity entity, Direction direction, Positionable obstacle) {
        boolean collision = false;
        boolean pointBlanc = false;

        if (obstacle instanceof Obstacle) {
            pointBlanc = obstacle.coordinates.equals(
                entity.coordinates
            );
            collision = obstacle.coordinates.equals(
                entity.coordinates.cpy().add(direction.getDirectionVector())
            ) || pointBlanc;
        }
        else if (obstacle instanceof MovableEntity) {
            MovableEntity movableObstacle = (MovableEntity) obstacle;
            pointBlanc = movableObstacle.coordinates.equals(
                entity.coordinates
            );
            collision = movableObstacle.destinationCoordinates.equals(
                entity.coordinates.cpy().add(direction.getDirectionVector())
            );
            if (movableObstacle.movementProgress < 0.75) {
                collision = collision || movableObstacle.coordinates.equals(
                    entity.coordinates.cpy().add(direction.getDirectionVector())
                ) || pointBlanc;
            }
            if (collision) {
                movableObstacle.onCollidingIntoSelf(entity);
            }
        }
        if (entity instanceof ShootableEntity) {
            ((ShootableEntity)entity).pointBlanc = pointBlanc;
        }
        
        return collision;
    }

    public boolean collidesWithObstacles(MovableEntity entity, Direction direction) {
        boolean collision = false;
        for (int i = 0; i < obstacles.size(); i++) {
            if (idxsToSkip.contains(i)) {
                continue;
            }
            Positionable obstacle = obstacles.get(i);
            if (collidesWithObstacle(entity, direction, obstacle)) {
                collision = true;
                break;
            }
        }
        return collision;
    }
}
