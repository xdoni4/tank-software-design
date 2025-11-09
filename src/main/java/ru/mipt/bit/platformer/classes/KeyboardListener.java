package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Input.Keys.*;

import ru.mipt.bit.platformer.classes.Direction;

public class KeyboardListener {
    public Direction captureMovementKey() {
        if (Gdx.input.isKeyPressed(UP) || Gdx.input.isKeyPressed(W)) {
            return Direction.UP;
        }
        else if (Gdx.input.isKeyPressed(LEFT) || Gdx.input.isKeyPressed(A)) {
            return Direction.LEFT;
        }
        else if (Gdx.input.isKeyPressed(DOWN) || Gdx.input.isKeyPressed(S)) {
            return Direction.DOWN;
        }
        else if (Gdx.input.isKeyPressed(RIGHT) || Gdx.input.isKeyPressed(D)) {
            return Direction.RIGHT;
        }
        else {
            return Direction.IDLE;
        }
    }
    public int captureLKey() {
        if (Gdx.input.isKeyPressed(L)) {
            return 1;
        }
        return 0;
    }
}