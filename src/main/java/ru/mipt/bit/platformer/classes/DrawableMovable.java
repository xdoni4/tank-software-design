package ru.mipt.bit.platformer.classes;

import ru.mipt.bit.platformer.classes.MovableEntity;
import ru.mipt.bit.platformer.util.TileMovement;
import com.badlogic.gdx.graphics.g2d.Batch;

public interface DrawableMovable {
    public void draw(Batch batch, MovableEntity entity, TileMovement tileMovement);
}