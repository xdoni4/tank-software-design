package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;
import ru.mipt.bit.platformer.classes.Tank;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.classes.DrawableMovable;
import com.badlogic.gdx.math.Vector2;

import ru.mipt.bit.platformer.classes.Graphics;


public class MovableEntityGraphics extends Graphics implements DrawableMovable {
    public MovableEntityGraphics(String imagePath) {
        super(imagePath);
    }

    public void draw(Batch batch, MovableEntity entity, TileMovement tileMovement) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, entity.coordinates, entity.destinationCoordinates, entity.movementProgress);
        int regionWidth = graphics.getRegionWidth();
        int regionHeight = graphics.getRegionHeight();
        float regionOriginX = regionWidth / 2f;
        float regionOriginY = regionHeight / 2f;
        batch.draw(graphics, rectangle.x, rectangle.y, regionOriginX, regionOriginY, regionWidth, regionHeight, 1f, 1f, entity.rotation);
    }
}