package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.GridPoint2;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

import ru.mipt.bit.platformer.classes.Tree;
import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.ObjectDirection;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;

public class Tank {
    // Texture decodes an image file and loads it into GPU memory, it represents a native resource
    public Texture texture;
    // TextureRegion represents Texture portion, there may be many TextureRegion instances of the same Texture
    public TextureRegion playerGraphics;
    public Rectangle playerRectangle;
    public GridPoint2 playerCoordinates;
    public GridPoint2 playerDestinationCoordinates;
    public float playerMovementProgress = 1f;
    public float playerRotation;
    public ObjectDirection playerDirection;


    public Tank(String imagePath, int xCooordinate, int yCoordinate) {
        this.texture = new Texture(imagePath);
        this.playerGraphics = new TextureRegion(this.texture);
        this.playerRectangle = createBoundingRectangle(this.playerGraphics);

        // set player initial position
        this.playerDestinationCoordinates = new GridPoint2(xCooordinate, yCoordinate);
        this.playerCoordinates = new GridPoint2(this.playerDestinationCoordinates);
        this.playerRotation = 0f;
        this.playerDirection = new ObjectDirection();
    }

    public void tryGoUp(Tree tree) {
        if (isEqual(playerMovementProgress, 1f)) {
            // check potential player destination for collision with obstacles
            if (!tree.treeCoordinates.equals(incrementedY(playerCoordinates))) {
                playerDestinationCoordinates.y++;
                playerMovementProgress = 0f;
            }
            playerRotation = 90f;
        }
    }

    public void tryGoLeft(Tree tree) {
        if (isEqual(playerMovementProgress, 1f)) {
            if (!tree.treeCoordinates.equals(decrementedX(playerCoordinates))) {
                playerDestinationCoordinates.x--;
                playerMovementProgress = 0f;
            }
            playerRotation = -180f;
        }
    }

    public void tryGoDown(Tree tree) {
        if (isEqual(playerMovementProgress, 1f)) {
            if (!tree.treeCoordinates.equals(decrementedY(playerCoordinates))) {
                playerDestinationCoordinates.y--;
                playerMovementProgress = 0f;
            }
            playerRotation = -90f;
        }
    }

    public void tryGoRight(Tree tree) {
         if (isEqual(playerMovementProgress, 1f)) {
            if (!tree.treeCoordinates.equals(incrementedX(playerCoordinates))) {
                playerDestinationCoordinates.x++;
                playerMovementProgress = 0f;
            }
            playerRotation = 0f;
        }
    }

    public void moveOneTile(Tree tree) {
        this.playerDirection.update();
        if (this.playerDirection.dir == Direction.LEFT) {
            this.tryGoLeft(tree);
        }
        else if (this.playerDirection.dir == Direction.UP) {
            this.tryGoUp(tree);
        }
        else if (this.playerDirection.dir == Direction.RIGHT) {
            this.tryGoRight(tree);
        }
        else if (this.playerDirection.dir == Direction.DOWN) {
            this.tryGoDown(tree);
        }
    }

    public void dispose() {
        texture.dispose();
    }
}