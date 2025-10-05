package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.Positionable;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;

public class Tree extends Positionable {
    public Texture texture;
    public TextureRegion graphics;
    public Rectangle rectangle = new Rectangle();

    public Tree(String imagePath, int xCoordinate, int yCoordinate) {
        super(xCoordinate, yCoordinate);
        this.texture = new Texture(imagePath);
        this.graphics = new TextureRegion(this.texture);
        this.rectangle = createBoundingRectangle(this.graphics);
    }

    public void dispose() {
        texture.dispose();
    }
}