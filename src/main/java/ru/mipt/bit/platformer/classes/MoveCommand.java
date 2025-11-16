package ru.mipt.bit.platformer.classes;

import java.util.ArrayList;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.Command;
import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.MovableEntity;


public class MoveCommand implements Command {
    private MovableEntity movableEntity;
    public Direction direction;
    private ArrayList<GridPoint2> obstacleCoordinates;
    public ArrayList<Integer> idxsToSkip;

    public MoveCommand(
        MovableEntity movableEntity,
        Direction dir,
        ArrayList<GridPoint2> obstacleCoordinates,
        ArrayList<Integer> idxsToSkip
    ) {
        this.movableEntity = movableEntity;
        this.direction = dir;
        this.obstacleCoordinates = obstacleCoordinates;
        this.idxsToSkip = idxsToSkip;
    }

    private boolean collidesWithObstacle(GridPoint2 obstacleCoords) {
        return obstacleCoords.equals(
            movableEntity.coordinates.cpy().add(direction.getDirectionVector())
        );
    }

    private boolean collidesWithObstacles() {
        boolean collision = false;
        for (int i = 0; i < obstacleCoordinates.size(); i++) {
            if (idxsToSkip.contains(i)) {
                continue;
            }
            GridPoint2 obstacleCoords = obstacleCoordinates.get(i);
            if (collidesWithObstacle(obstacleCoords)) {
                collision = true;
                break;
            }
        }
        return collision;
    }

    @Override
    public void execute() {
        movableEntity.updateDirection(direction);
        if (direction != Direction.IDLE) {
            movableEntity.rotation = direction.getRotation();
        }
        if (!collidesWithObstacles()) {
            movableEntity.destinationCoordinates.add(direction.getDirectionVector());
        }
        movableEntity.movementProgress = 0f;
    }
}
