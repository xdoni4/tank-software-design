package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Input.Keys.*;
import com.badlogic.gdx.math.GridPoint2;

enum Direction {
    IDLE(new GridPoint2(0, 0), 0f),
    LEFT(new GridPoint2(-1, 0), -180f),
    UP(new GridPoint2(0, 1), 90f),
    RIGHT(new GridPoint2(1, 0), 0f),
    DOWN(new GridPoint2(0, -1), -90f);

    public final GridPoint2 vector;
    public final float rotation;

    Direction(GridPoint2 vector, float rotation) {
        this.vector = vector;
        this.rotation = rotation;
    }

    // public GridPoint2 getDirectionVector() {
    //     return new GridPoint2(vector);
    // }

    // public float getRotation() {
    //     return rotation;
    // }
}

public class ObjectDirection {
    public Direction dir;

    public ObjectDirection() {
        this.dir = Direction.UP;
    }

    public GridPoint2 getDirectionVector() {
        return new GridPoint2(dir.vector);
    }

    public float getRotation() {
        return dir.rotation;
    }

    public void update() {
        if (Gdx.input.isKeyPressed(UP) || Gdx.input.isKeyPressed(W)) {
            this.dir = Direction.UP;
        }
        else if (Gdx.input.isKeyPressed(LEFT) || Gdx.input.isKeyPressed(A)) {
            this.dir = Direction.LEFT;
        }
        else if (Gdx.input.isKeyPressed(DOWN) || Gdx.input.isKeyPressed(S)) {
            this.dir = Direction.DOWN;
        }
        else if (Gdx.input.isKeyPressed(RIGHT) || Gdx.input.isKeyPressed(D)) {
            this.dir = Direction.RIGHT;
        }
        else {
            this.dir = Direction.IDLE;
        }
    }
}