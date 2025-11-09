package ru.mipt.bit.platformer;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

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
import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.Obstacle;
import ru.mipt.bit.platformer.classes.Tree;
import ru.mipt.bit.platformer.classes.Tank;
import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.Graphics;
import ru.mipt.bit.platformer.classes.MapLayout;
import ru.mipt.bit.platformer.classes.KeyboardListener;
import ru.mipt.bit.platformer.classes.MoveCommand;


import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {
    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TileMovement tileMovement;
    private MapLayout mapLayout;

    private MovableEntity humanPlayer;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<MovableEntity> aiPlayers;
    private Graphics humanPlayerGraphics;
    private ArrayList<Graphics> aiPlayersGraphics;
    private ArrayList<Graphics> obstaclesGraphics;

    private KeyboardListener kl;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // load level tiles
        level = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
        mapLayout = new MapLayout(groundLayer);
        // mapLayout = new MapLayout("mapLayout.txt");

        kl = new KeyboardListener();

        GridPoint2 humanPlayerCoordinates = mapLayout.layout.get("Player").get(0);
        humanPlayer = new Tank(humanPlayerCoordinates.x, humanPlayerCoordinates.y, 0.4f);

        aiPlayers = new ArrayList<>();
        for (GridPoint2 aiPlayersCoordinates : mapLayout.layout.get("AI")) {
            aiPlayers.add(new Tank(aiPlayersCoordinates.x, aiPlayersCoordinates.y, 0.4f));
        }

        obstacles = new ArrayList<>();
        for (GridPoint2 obstacleCoordinates : mapLayout.layout.get("Obstacles")) {
            obstacles.add(new Tree(obstacleCoordinates.x, obstacleCoordinates.y));
        }
        humanPlayerGraphics = new Graphics("images/tank_blue.png");

        aiPlayersGraphics = new ArrayList<>();
        for (int i = 0; i < aiPlayers.size(); i++) {
            aiPlayersGraphics.add(new Graphics("images/tank_safari_mesh.png"));
        }
        obstaclesGraphics = new ArrayList<>(); 
        for (int i = 0; i < obstacles.size(); i++) {
            obstaclesGraphics.add(new Graphics("images/greenTree.png"));
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            Graphics obstacleG = obstaclesGraphics.get(i);
            moveRectangleAtTileCenter(groundLayer, obstacleG.rectangle, obstacle.coordinates);
        }
        for (GridPoint2 bordersCoordinates : mapLayout.layout.get("Borders")) {
            obstacles.add(new Obstacle(bordersCoordinates.x, bordersCoordinates.y));
        }
        System.out.println(mapLayout.layout);
    }

    private void glClearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private ArrayList<MoveCommand> initiateMoving() {
        ArrayList<MoveCommand> moveCommands = new ArrayList<>();
        ArrayList<GridPoint2> obstacleCoordinates = new ArrayList<>();

        int idx = 0;

        moveCommands.add(
            new MoveCommand(
                humanPlayer,
                humanPlayer.direction,
                obstacleCoordinates,
                new ArrayList<>(Arrays.asList(idx, idx+1))
            )
        );
        obstacleCoordinates.add(humanPlayer.coordinates);
        obstacleCoordinates.add(humanPlayer.coordinates.cpy().add(humanPlayer.direction.getDirectionVector()));

        idx += 2;

        for (MovableEntity aiPlayer : aiPlayers) {
            Direction dir = Direction.values()[(new Random()).nextInt(Direction.values().length)];
            moveCommands.add(
                new MoveCommand(
                    aiPlayer,
                    dir,
                    obstacleCoordinates,
                    new ArrayList<>(Arrays.asList(idx, idx+1))
                )
            );
            obstacleCoordinates.add(aiPlayer.coordinates);
            obstacleCoordinates.add(aiPlayer.coordinates.cpy().add(dir.getDirectionVector()));
            idx += 2;
        }
        for (Obstacle obstacle: obstacles) {
            obstacleCoordinates.add(obstacle.coordinates);
        }

        return moveCommands;
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

    private void renderPlayer(MovableEntity player, Graphics playerGraphics, MoveCommand moveCommand) {
        // get time passed since the last render
        float deltaTime = Gdx.graphics.getDeltaTime();
        player.movementProgress = continueProgress(player.movementProgress, deltaTime, player.movementSpeed);
        if (isEqual(player.movementProgress, 1f)) {
            // record that the player has reached his/her destination
            player.coordinates.set(player.destinationCoordinates);
            moveCommand.execute();
            player.updateDirection(kl.captureMovementKey());
        }
        // calculate interpolated player screen coordinates
        tileMovement.moveRectangleBetweenTileCenters(playerGraphics.rectangle, player.coordinates, player.destinationCoordinates, player.movementProgress);
        drawTextureRegionUnscaled(batch, playerGraphics.graphics, playerGraphics.rectangle, player.rotation);
    }

    private void renderAIPlayers(ArrayList<MoveCommand> moveCommands) {
        for (int i = 0; i < aiPlayers.size(); i++) {
            MovableEntity player = aiPlayers.get(i);
            Graphics playerGraphics = aiPlayersGraphics.get(i);
            renderPlayer(player, playerGraphics, moveCommands.get(i+1));
        }
    }

    private void renderObstacles() {
         if (obstaclesGraphics != null) {
            for (int i = 0; i < obstaclesGraphics.size(); i++) {
                Graphics obstacleG = obstaclesGraphics.get(i);
                drawTextureRegionUnscaled(batch, obstacleG.graphics, obstacleG.rectangle, 0f);
            }
        }
    }

    @Override
    public void render() {
        ArrayList<MoveCommand> moveCommands = initiateMoving();
        startRendering();

        // render player
        renderPlayer(humanPlayer, humanPlayerGraphics, moveCommands.get(0));

        // render AI players
        renderAIPlayers(moveCommands);

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
