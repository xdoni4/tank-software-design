package ru.mipt.bit.platformer;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;

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
import ru.mipt.bit.platformer.classes.MovableEntityGraphics;
import ru.mipt.bit.platformer.classes.MapLayout;
import ru.mipt.bit.platformer.classes.KeyboardListener;
import ru.mipt.bit.platformer.classes.MoveCommand;
import ru.mipt.bit.platformer.classes.Drawable;
import ru.mipt.bit.platformer.classes.DrawableMovable;
import ru.mipt.bit.platformer.classes.HealthBarDecorator;
import ru.mipt.bit.platformer.classes.ExecutionSuppressor;


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

    private Tank humanPlayer;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Tank> aiPlayers;
    private DrawableMovable humanPlayerGraphics;
    private ArrayList<DrawableMovable> aiPlayersGraphics;
    private ArrayList<Graphics> obstaclesGraphics;
    private ExecutionSuppressor healthBarSuppressor;

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
        healthBarSuppressor = new ExecutionSuppressor();

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
        humanPlayerGraphics = new HealthBarDecorator(
            new MovableEntityGraphics("images/tank_blue.png"),
            humanPlayer,
            healthBarSuppressor
        );

        aiPlayersGraphics = new ArrayList<>();
        for (int i = 0; i < aiPlayers.size(); i++) {
            aiPlayersGraphics.add(
                new HealthBarDecorator(
                    new MovableEntityGraphics("images/tank_safari_mesh.png"),
                    aiPlayers.get(i),
                    healthBarSuppressor
                )
            );
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
    }

    private void glClearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private ArrayList<MoveCommand> initiateMoving() {
        ArrayList<MoveCommand> moveCommands = new ArrayList<>();
        ArrayList<GridPoint2> obstacleCoordinates = new ArrayList<>();
        HashMap<GridPoint2, ArrayList<ArrayList<Integer>>> obstacleDestinationTies = new HashMap<>();
        int idx = 0;

        Direction humanPlayerOrderedDirection = kl.captureMovementKey();
        moveCommands.add(
            new MoveCommand(
                humanPlayer,
                humanPlayerOrderedDirection,
                obstacleCoordinates,
                new ArrayList<>(Arrays.asList(idx))
            )
        );
        obstacleCoordinates.add(humanPlayer.destinationCoordinates);
        GridPoint2 destinationWithDirection = humanPlayer.destinationCoordinates.cpy().add(humanPlayerOrderedDirection.getDirectionVector());
        obstacleCoordinates.add(destinationWithDirection);
        if (obstacleDestinationTies.containsKey(destinationWithDirection)) {
            obstacleDestinationTies.get(destinationWithDirection).add(new ArrayList<>(Arrays.asList(0, idx+1)));
        }
        else {
            obstacleDestinationTies.put(destinationWithDirection, new ArrayList<>());
            obstacleDestinationTies.get(destinationWithDirection).add(new ArrayList<>(Arrays.asList(0, idx+1)));
        }
        idx += 2;
        if (humanPlayer.movementProgress < 0.75) {
            obstacleCoordinates.add(humanPlayer.coordinates);
            moveCommands.get(0).idxsToSkip.add(idx);
            idx += 1;
        }

        for (int i = 0; i < aiPlayers.size(); i++) {
            MovableEntity aiPlayer = aiPlayers.get(i);
            Direction aiPlayerOrderedDirection = Direction.values()[(new Random()).nextInt(Direction.values().length)];
            moveCommands.add(
                new MoveCommand(
                    aiPlayer,
                    aiPlayerOrderedDirection,
                    obstacleCoordinates,
                    new ArrayList<>(Arrays.asList(idx))
                )
            );
            obstacleCoordinates.add(aiPlayer.destinationCoordinates);
            GridPoint2 aiDestinationWithDirection = aiPlayer.destinationCoordinates.cpy().add(aiPlayerOrderedDirection.getDirectionVector());
            obstacleCoordinates.add(aiDestinationWithDirection);
            if (obstacleDestinationTies.containsKey(aiDestinationWithDirection)) {
                obstacleDestinationTies.get(aiDestinationWithDirection).add(new ArrayList<>(Arrays.asList(i+1, idx+1)));
            }
            else {
                obstacleDestinationTies.put(aiDestinationWithDirection, new ArrayList<>());
                obstacleDestinationTies.get(aiDestinationWithDirection).add(new ArrayList<>(Arrays.asList(i+1, idx+1)));
            }
            idx += 2;

            if (aiPlayer.movementProgress < 0.75) {
                obstacleCoordinates.add(aiPlayer.coordinates);
                moveCommands.get(i+1).idxsToSkip.add(idx);
                idx += 1;
            }
            
        }
        for (Obstacle obstacle: obstacles) {
            obstacleCoordinates.add(obstacle.coordinates);
        }

        for (ArrayList<ArrayList<Integer>> value : obstacleDestinationTies.values()) {
            int winner = (new Random().nextInt(value.size()));
            for (int i = 0; i < value.size(); i++) {
                if (i == winner) {
                    int command = value.get(i).get(0);
                    int winner_idx = value.get(i).get(1);
                    moveCommands.get(command).idxsToSkip.add(winner_idx);
                }
            }
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

    private void renderPlayer(MovableEntity player, DrawableMovable playerGraphics) {
        // calculate interpolated player screen coordinates
        // tileMovement.moveRectangleBetweenTileCenters(playerGraphics.rectangle, player.coordinates, player.destinationCoordinates, player.movementProgress);
        playerGraphics.draw(batch, player, tileMovement);
    }

    private void renderAIPlayers() {
        for (int i = 0; i < aiPlayers.size(); i++) {
            MovableEntity player = aiPlayers.get(i);
            DrawableMovable playerGraphics = aiPlayersGraphics.get(i);
            renderPlayer(player, playerGraphics);
        }
    }

    private void renderObstacles() {
         if (obstaclesGraphics != null) {
            for (int i = 0; i < obstaclesGraphics.size(); i++) {
                Graphics obstacleG = obstaclesGraphics.get(i);
                obstacleG.draw(batch, 0f);
            }
        }
    }

    private void updatePlayer(MovableEntity player, MoveCommand moveCommand) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        player.movementProgress = continueProgress(player.movementProgress, deltaTime, player.movementSpeed);
        if (isEqual(player.movementProgress, 1f)) {
            // record that the player has reached his/her destination

            player.coordinates.set(player.destinationCoordinates);
            moveCommand.execute();
            player.updateDirection(moveCommand.direction); // kl.captureMovementKey()
        }
    }

    private void updateAIPlayers(ArrayList<MoveCommand> moveCommands) {
        for (int i = 0; i < aiPlayers.size(); i++) {
            MovableEntity player = aiPlayers.get(i);
            DrawableMovable playerGraphics = aiPlayersGraphics.get(i);
            updatePlayer(player, moveCommands.get(i+1));
        }
    }

    @Override
    public void render() {
        ArrayList<MoveCommand> moveCommands = initiateMoving();
        startRendering();

        healthBarSuppressor.update(kl.captureLKey());

        // update player
        updatePlayer(humanPlayer, moveCommands.get(0));

        // update AU players
        updateAIPlayers(moveCommands);

        // render player
        renderPlayer(humanPlayer, humanPlayerGraphics);

        // render AI players
        renderAIPlayers();

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
