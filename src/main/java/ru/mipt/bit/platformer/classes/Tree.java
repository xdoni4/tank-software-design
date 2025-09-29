package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.GridPoint2;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;

public class Tree {
    public Texture texture;
    public TextureRegion treeGraphics;
    public GridPoint2 treeCoordinates = new GridPoint2();
    public Rectangle treeRectangle = new Rectangle();

    public Tree(String imagePath, int xCoordinate, int yCoordinate) {
        this.texture = new Texture(imagePath);
        this.treeGraphics = new TextureRegion(this.texture);
        this.treeRectangle = createBoundingRectangle(this.treeGraphics);
        this.treeCoordinates = new GridPoint2(xCoordinate, yCoordinate);
    }

    public void dispose() {
        texture.dispose();
    }
}