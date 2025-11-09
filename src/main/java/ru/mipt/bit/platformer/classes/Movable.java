package ru.mipt.bit.platformer.classes;

import java.util.ArrayList;
import ru.mipt.bit.platformer.classes.Positionable;

public interface Movable {
    public void updateDirection(Direction dir);
}