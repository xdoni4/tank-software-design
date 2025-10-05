package ru.mipt.bit.platformer.classes;
import ru.mipt.bit.platformer.classes.Positionable;


public class Tree extends Positionable {
    public Graphics graphics;

    //String imagePath, 
    public Tree(int xCoordinate, int yCoordinate) {
        super(xCoordinate, yCoordinate);
        // this.graphics = new Graphics(imagePath);
    }

    public void dispose() {
        graphics.dispose();
    }
}