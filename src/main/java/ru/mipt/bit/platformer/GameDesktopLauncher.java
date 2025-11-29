package ru.mipt.bit.platformer;

import java.util.UUID;
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
import ru.mipt.bit.platformer.classes.MapLayout;
import ru.mipt.bit.platformer.classes.KeyboardListener;
import ru.mipt.bit.platformer.classes.Drawable;
import ru.mipt.bit.platformer.classes.DrawableMovable;
import ru.mipt.bit.platformer.classes.HealthBarDecorator;
import ru.mipt.bit.platformer.classes.ExecutionSuppressor;
import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.Bullet;

import ru.mipt.bit.platformer.commands.Command;
import ru.mipt.bit.platformer.commands.MoveCommand;
import ru.mipt.bit.platformer.commands.ShootCommand;

import ru.mipt.bit.platformer.graphics.Graphics;
import ru.mipt.bit.platformer.graphics.MovableEntityGraphics;
import ru.mipt.bit.platformer.classes.CollisionHandler;


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

    private HashMap<String, Tank> humanPlayers;
    private HashMap<String, Tank> aiPlayers;
    private HashMap<String, Obstacle> obstacles;
    private HashMap<String, ShootableEntity> projectiles;

    private HashMap<String, DrawableMovable> humanPlayersGraphics;
    private HashMap<String, DrawableMovable> aiPlayersGraphics;
    private HashMap<String, DrawableMovable> projectilesGraphics;
    private HashMap<String, Graphics> obstaclesGraphics;

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

        humanPlayers = new HashMap<>();
        aiPlayers = new HashMap<>();
        obstacles = new HashMap<>();
        projectiles = new HashMap<>();


        humanPlayersGraphics = new HashMap<>();
        aiPlayersGraphics = new HashMap<>();
        obstaclesGraphics = new HashMap<>();
        projectilesGraphics = new HashMap<>();

        kl = new KeyboardListener();
        healthBarSuppressor = new ExecutionSuppressor();

        GridPoint2 humanPlayerCoordinates = mapLayout.layout.get("Player").get(0);
        humanPlayers.put(
            UUID.randomUUID().toString(),
            new Tank(humanPlayerCoordinates.x, humanPlayerCoordinates.y, 0.4f)
        );

        for (GridPoint2 aiPlayersCoordinates : mapLayout.layout.get("AI")) {
            aiPlayers.put(
                UUID.randomUUID().toString(),
                new Tank(aiPlayersCoordinates.x, aiPlayersCoordinates.y, 0.4f)
            );
        }

        for (GridPoint2 obstacleCoordinates : mapLayout.layout.get("Obstacles")) {
            obstacles.put(
                UUID.randomUUID().toString(),
                new Tree(obstacleCoordinates.x, obstacleCoordinates.y)
            );
        }

        for (String key : humanPlayers.keySet()) {
            Tank humanPlayer = humanPlayers.get(key);
            humanPlayersGraphics.put(
                key,
                new HealthBarDecorator(
                    new MovableEntityGraphics("images/tank_blue.png"),
                    humanPlayer,
                    healthBarSuppressor
                )
            );
        }

        for (String key : aiPlayers.keySet()) {
            aiPlayersGraphics.put(
                key,
                new HealthBarDecorator(
                    new MovableEntityGraphics("images/tank_safari_mesh.png"),
                    aiPlayers.get(key),
                    healthBarSuppressor
                )
            );
        }

        for (String key : obstacles.keySet()) {
            obstaclesGraphics.put(
                key,
                new Graphics("images/greenTree.png")
            );
        }

        for (String key : obstacles.keySet()) {
            Obstacle obstacle = obstacles.get(key);
            Graphics obstacleG = obstaclesGraphics.get(key);
            moveRectangleAtTileCenter(groundLayer, obstacleG.rectangle, obstacle.coordinates);
        }

        for (GridPoint2 bordersCoordinates : mapLayout.layout.get("Borders")) {
            obstacles.put(
                UUID.randomUUID().toString(),
                new Obstacle(bordersCoordinates.x, bordersCoordinates.y)
            );
        }
    }

    private void glClearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    private ArrayList<Command> initiateCommands() {
        ArrayList<Command> commands = new ArrayList<>();
        ArrayList<Positionable> allPositionables = new ArrayList<>();
        int idx = 0;

        for (Tank humanPlayer : humanPlayers.values()) {
            if (kl.captureShootKey() == 1 && humanPlayer.shootCoolDown == 0) {
                commands.add(
                    new ShootCommand(
                        humanPlayer,
                        projectiles
                    )
                );
                humanPlayer.shootCoolDown = humanPlayer.shootCoolDownMax;
            }
            else {
                Direction humanPlayerOrderedDirection = kl.captureMovementKey();
                if (humanPlayerOrderedDirection == Direction.IDLE) {
                    humanPlayerOrderedDirection = humanPlayer.direction;
                    humanPlayer.isMoving = false;
                }
                else {
                    humanPlayer.isMoving = true;
                }
                commands.add(
                    new MoveCommand(
                        humanPlayer,
                        humanPlayerOrderedDirection,
                        new CollisionHandler(
                            allPositionables,
                            new ArrayList<>(Arrays.asList(idx))
                        )
                    )
                );
                allPositionables.add(humanPlayer);
                idx++;
            }
        }
        
        
        for (Tank aiPlayer : aiPlayers.values()) {
            Direction aiPlayerOrderedDirection = Direction.values()[(new Random()).nextInt(Direction.values().length)];
            if (aiPlayerOrderedDirection == Direction.IDLE) {
                aiPlayerOrderedDirection = aiPlayer.direction;
                aiPlayer.isMoving = false;
            }
            else {
                aiPlayer.isMoving = true;
            }
            
            aiPlayer.isMoving = true;

            if ((new Random()).nextInt(5) > 3 && aiPlayer.shootCoolDown == 0) {
                commands.add(
                    new ShootCommand(
                        aiPlayer,
                        projectiles
                    )
                );
                aiPlayer.shootCoolDown = aiPlayer.shootCoolDownMax;
            }
            else {
                commands.add(
                    new MoveCommand(
                        aiPlayer,
                        aiPlayerOrderedDirection,
                        new CollisionHandler(
                            allPositionables,
                            new ArrayList<>(Arrays.asList(idx))
                        )
                    )
                );
                allPositionables.add(aiPlayer);
                idx++;
            }
        }

        for (ShootableEntity projectile : projectiles.values()) {
            allPositionables.add(projectile);
            commands.add(
                new MoveCommand(
                    (MovableEntity)projectile,
                    ((Bullet)projectile).direction,
                    new CollisionHandler(
                        allPositionables,
                        new ArrayList<>(Arrays.asList(idx))
                    )
                )
            );
            idx++;
        }

        for (Obstacle obstacle : obstacles.values()) {
            allPositionables.add(obstacle);
        }

        return commands;
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
        playerGraphics.draw(batch, player, tileMovement);
    }

    private void renderPlayers(HashMap<String, Tank> players, HashMap<String, DrawableMovable> playersGraphics) {
        for (String key : players.keySet()) {
            MovableEntity player = (MovableEntity) players.get(key);
            DrawableMovable playerGraphics = playersGraphics.get(key);
            renderPlayer(player, playerGraphics);
        }
    }

    private void renderObstacles(HashMap<String, Graphics> obstaclesGraphics) {
        for (Graphics obstacleG : obstaclesGraphics.values()) {
            obstacleG.draw(batch, 0f);
        }
    }

    private void renderProjectiles(HashMap<String, ShootableEntity> projectiles, HashMap<String, DrawableMovable> projectilesGraphics) {
        for (String key : projectilesGraphics.keySet()) {
            DrawableMovable projectileGraphics = projectilesGraphics.get(key);
            ShootableEntity projectile = projectiles.get(key);
            projectileGraphics.draw(batch, projectile, tileMovement);
        }
    }

    private void updatePlayers(HashMap<String, Tank> players) {
        ArrayList<String> keysToRemove = new ArrayList<>();

        for (String key : players.keySet()) {
            Tank player = players.get(key);
            if (player.getHealth() == 0) {
                keysToRemove.add(key);
            }
        }

        players.keySet().removeAll(keysToRemove);
    }

    private void updateProjectiles(HashMap<String, ShootableEntity> projectiles) {
        ArrayList<String> keysToRemove = new ArrayList<>();

        for (String key : projectiles.keySet()) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            ShootableEntity projectile = projectiles.get(key);
            ((MovableEntity) projectile).movementProgress = continueProgress(((MovableEntity) projectile).movementProgress, deltaTime, ((MovableEntity) projectile).movementSpeed);
            if (!projectilesGraphics.containsKey(key)) {
                projectilesGraphics.put(key, new MovableEntityGraphics("images/bullet.png"));
            }
            if (((Bullet)projectile).exploded) {
                MovableEntityGraphics projectileGraphics = (MovableEntityGraphics) projectilesGraphics.get(key);
                projectileGraphics.texture = new Texture("images/explosion.png");
                projectileGraphics.graphics = new TextureRegion(projectileGraphics.texture);
                if (((Bullet)projectile).explosionLifetime == ((Bullet)projectile).explosionLifetimeMax) {
                    keysToRemove.add(key);
                }
            }
        }
        projectiles.keySet().removeAll(keysToRemove);
        projectilesGraphics.keySet().removeAll(keysToRemove);
    }

    private void executeCommands(ArrayList<Command> commands) {
        for (Command command : commands) {
            if (command instanceof ShootCommand) {
                command.execute();
            }
        }
        for (Command command : commands) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            if (command instanceof MoveCommand) {
                MovableEntity entity = ((MoveCommand)command).entity;
                entity.movementProgress = continueProgress(entity.movementProgress, deltaTime, entity.movementSpeed);
                if (isEqual(entity.movementProgress, 1f)) {
                    // record that the player has reached his/her destination
                    entity.coordinates.set(entity.destinationCoordinates);
                    command.execute();
                }
            }
        }
    }

    private void tick() {
        for (Tank player : humanPlayers.values()) {
            player.tick();
        }
        for (Tank aiPlayer : aiPlayers.values()) {
            aiPlayer.tick();
        }
        for (ShootableEntity projectile : projectiles.values()) {
            ((Bullet)projectile).tick();
        }
        healthBarSuppressor.tick();
    }

    @Override
    public void render() {
        ArrayList<Command> commands = initiateCommands();
        startRendering();

        if (healthBarSuppressor.updateCoolDown == 0) {
            healthBarSuppressor.update(kl.captureLKey());
            healthBarSuppressor.updateCoolDown = healthBarSuppressor.updateCoolDownMax;
        }

        // update human players
        updatePlayers(humanPlayers);

        // update AI players
        updatePlayers(aiPlayers);

        // update projectiles
        updateProjectiles(projectiles);

        executeCommands(commands);

        // render human players
        renderPlayers(humanPlayers, humanPlayersGraphics);

        // render AI players
        renderPlayers(aiPlayers, aiPlayersGraphics);

        // render obstacles
        renderObstacles(obstaclesGraphics);

        // render projectiles
        renderProjectiles(projectiles, projectilesGraphics);

        finishRendering();

        tick();
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
