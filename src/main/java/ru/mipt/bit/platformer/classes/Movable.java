package ru.mipt.bit.platformer.classes;
import ru.mipt.bit.platformer.classes.Positionable;

public interface Movable {
    public void moveSelfInCurrentDirection(Positionable[] obstacles);
    public void updateDirection();
}