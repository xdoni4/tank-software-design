package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.GridPoint2;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

import ru.mipt.bit.platformer.classes.Tree;
import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.ObjectDirection;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;

public class Tank extends Positionable {
    // Texture decodes an image file and loads it into GPU memory, it represents a native resource
    public Texture texture;
    // TextureRegion represents Texture portion, there may be many TextureRegion instances of the same Texture
    public TextureRegion graphics;
    public Rectangle rectangle;
    public GridPoint2 destinationCoordinates;
    public float movementProgress = 1f;
    public float rotation;
    public ObjectDirection direction;


    public Tank(String imagePath, int xCooordinate, int yCoordinate) {
        super(xCooordinate, yCoordinate);
        this.texture = new Texture(imagePath);
        this.graphics = new TextureRegion(this.texture);
        this.rectangle = createBoundingRectangle(this.graphics);

        // set initial position
        this.destinationCoordinates = new GridPoint2(this.coordinates);
        this.rotation = 0f;
        this.direction = new ObjectDirection();
    }

    private boolean isMovementActionCompleted() {
        return isEqual(movementProgress, 1f);
    }

    private boolean collidesWithObstacle(Positionable obstacle) {
        return obstacle.coordinates.equals(coordinates.cpy().add(direction.getDirectionVector()));
    }

    public void tryGo(Positionable[] obstacles) {
        if (isMovementActionCompleted()) {
            boolean noCollision = true;
            for (Positionable obstacle : obstacles) {
                if (collidesWithObstacle(obstacle)) {
                    noCollision = false;
                    break;
                }
            }
            if (noCollision) {
                destinationCoordinates.add(direction.getDirectionVector());
                movementProgress = 0f;
            }
            if (direction.dir != Direction.IDLE) {
                rotation = direction.getRotation();
            }
        }
    }

    public void updateDirection() {
        this.direction.update();
    }

    public void moveSelfInCurrentDirection(Positionable[] obstacles) {
        tryGo(obstacles);
    }

    public void dispose() {
        texture.dispose();
    }
}