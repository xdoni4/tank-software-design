package ru.mipt.bit.platformer.classes;

import java.util.ArrayList;
import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.MovableEntity;

public interface Movable {
    public void updateDirection(Direction dir);
    public void onSelfCollidingInto();
    public void onCollidingIntoSelf(MovableEntity collider);
}