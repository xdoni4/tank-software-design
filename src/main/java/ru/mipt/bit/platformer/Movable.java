package ru.mipt.bit.platformer;

import java.util.ArrayList;

public interface Movable {
    public void updateDirection(Direction dir);
    public void onSelfCollidingInto();
    public void onCollidingIntoSelf(MovableEntity collider);
}