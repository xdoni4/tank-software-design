package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.classes.MovableEntity;
import ru.mipt.bit.platformer.classes.Obstacle;
import ru.mipt.bit.platformer.classes.Tree;
import ru.mipt.bit.platformer.classes.Tank;
import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.Graphics;


import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {
    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TileMovement tileMovement;

    private MovableEntity player;
    private Obstacle[] obstacles;
    private Graphics playerGraphics;
    private Graphics[] obstaclesGraphics;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // load level tiles
        level = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);

        player = new Tank(1, 1, 0.4f);
        // tree = new Tree(1, 3);
        obstacles = new Obstacle[] {
            new Tree(1, 3)
        };
        playerGraphics = new Graphics("images/tank_blue.png");
        // treeGraphics = new Graphics("images/greenTree.png");
        obstaclesGraphics = new Graphics[] {
            new Graphics("images/greenTree.png")
        };
        for (int i = 0; i < obstacles.length; i++) {
            Obstacle obstacle = obstacles[i];
            Graphics obstacleG = obstaclesGraphics[i];
            moveRectangleAtTileCenter(groundLayer, obstacleG.rectangle, obstacle.coordinates);
        }
    }

    private void glClearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private void startRendering() {
        // clear the screen
        glClearScreen();

        // render each tile of the level
        levelRenderer.render();

        // start recording all drawing commands
        batch.begin();
    }

    private void finishRendering() {
        // submit all drawing requests
        batch.end();
    }

    private void renderPlayer() {
        // get time passed since the last render
        float deltaTime = Gdx.graphics.getDeltaTime();
        player.movementProgress = continueProgress(player.movementProgress, deltaTime, player.movementSpeed);
            if (isEqual(player.movementProgress, 1f)) {
                // record that the player has reached his/her destination
                player.coordinates.set(player.destinationCoordinates);
            }
        if (obstacles != null) {
            player.updateDirection();
            // Positionable[] obstacles = {tree};
            player.moveSelfInCurrentDirection(obstacles);

            // calculate interpolated player screen coordinates
            tileMovement.moveRectangleBetweenTileCenters(playerGraphics.rectangle, player.coordinates, player.destinationCoordinates, player.movementProgress);
        }
        drawTextureRegionUnscaled(batch, playerGraphics.graphics, playerGraphics.rectangle, player.rotation);
    }

    private void renderObstacles() {
         if (obstaclesGraphics != null) {
            for (int i = 0; i < obstaclesGraphics.length; i++) {
                Graphics obstacleG = obstaclesGraphics[i];
                drawTextureRegionUnscaled(batch, obstacleG.graphics, obstacleG.rectangle, 0f);
            }
        }
    }

    @Override
    public void render() {
        startRendering();

        // render player
        renderPlayer();

        // render obstacles
        renderObstacles();

        finishRendering();
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        // dispose of all the native resources (classes which implement com.badlogic.gdx.utils.Disposable)
        level.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
