package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Input.Keys.*;

enum Direction {
    IDLE,
    LEFT,
    UP,
    RIGHT,
    DOWN
}

public class ObjectDirection {
    public Direction dir;

    public ObjectDirection() {
        this.dir = Direction.UP;
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