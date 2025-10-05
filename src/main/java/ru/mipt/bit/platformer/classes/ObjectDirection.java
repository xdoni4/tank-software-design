package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Input.Keys.*;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.KeyboardListener;

public class ObjectDirection {
    public Direction dir;
    private KeyboardListener kl;

    public ObjectDirection() {
        this.dir = Direction.UP;
        this.kl = new KeyboardListener();
    }

    public GridPoint2 getDirectionVector() {
        return new GridPoint2(dir.vector);
    }

    public float getRotation() {
        return dir.rotation;
    }

    public void update() {
        dir = kl.captureMovementKey();
    }
}