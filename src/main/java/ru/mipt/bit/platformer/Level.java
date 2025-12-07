package ru.mipt.bit.platformer;

import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;

import ru.mipt.bit.platformer.GameObject;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.TileMovement;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import static com.badlogic.gdx.Input.Keys.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

import ru.mipt.bit.platformer.commands.Command;
import ru.mipt.bit.platformer.commands.MoveCommand;
import ru.mipt.bit.platformer.commands.ShootCommand;

import ru.mipt.bit.platformer.keyboard.KeyboardListener;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

@Component
@Scope("prototype")
public class Level extends GameObject implements Observable {
    public TiledMapTileLayer groundLayer;
    public TiledMap level;
    public TileMovement tileMovement;

    private MapLayout mapLayout;

    public HashMap<String, Tank> humanPlayers;
    public HashMap<String, Tank> aiPlayers;
    public HashMap<String, Obstacle> obstacles;
    public HashMap<String, ShootableEntity> projectiles;

    public ExecutionSuppressor healthBarSuppressor;

    private KeyboardListener kl;

    private HashMap<String, Observer> subscribers;
    private HashMap<String, LevelData> dataUpdate;


    public Level(GameProperties gameProps) {
        humanPlayers = new HashMap<>();
        aiPlayers = new HashMap<>();
        obstacles = new HashMap<>();
        projectiles = new HashMap<>();

        kl = new KeyboardListener();

        subscribers = new HashMap<>();
        dataUpdate = new HashMap<>();
    }

    public Level initialize() {
        // load level tiles
        level = new TmxMapLoader().load("level.tmx");
        groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
        mapLayout = new MapLayout(groundLayer);
        // mapLayout = new MapLayout("mapLayout.txt");
        healthBarSuppressor = new ExecutionSuppressor();
        
        LevelData addedData = new LevelData();

        GridPoint2 humanPlayerCoordinates = mapLayout.layout.get("Player").get(0);
        String humanPlayerKey = UUID.randomUUID().toString();

        humanPlayers.put(
            humanPlayerKey,
            new Tank(humanPlayerCoordinates.x, humanPlayerCoordinates.y, 0.4f)
        );
        addedData.humanPlayers.put(humanPlayerKey, humanPlayers.get(humanPlayerKey));

        for (GridPoint2 aiPlayersCoordinates : mapLayout.layout.get("AI")) {
            String aiPlayerKey = UUID.randomUUID().toString();
            aiPlayers.put(
                aiPlayerKey,
                new Tank(aiPlayersCoordinates.x, aiPlayersCoordinates.y, 0.4f)
            );
            addedData.aiPlayers.put(aiPlayerKey, aiPlayers.get(aiPlayerKey));
        }

        for (GridPoint2 obstacleCoordinates : mapLayout.layout.get("Obstacles")) {
            String obstacleKey = UUID.randomUUID().toString();
            obstacles.put(
                obstacleKey,
                new Tree(obstacleCoordinates.x, obstacleCoordinates.y)
            );
            addedData.obstacles.put(obstacleKey, obstacles.get(obstacleKey));
        }

        for (GridPoint2 bordersCoordinates : mapLayout.layout.get("Borders")) {
            String borderKey = UUID.randomUUID().toString();
            obstacles.put(
                borderKey,
                new Obstacle(bordersCoordinates.x, bordersCoordinates.y)
            );
            addedData.obstacles.put(borderKey, obstacles.get(borderKey));
        }

        dataUpdate.put("add", addedData);
        return this;
    }

    @Override
    public String subscribe(Observer newSubscriber) {
        String key = UUID.randomUUID().toString();
        subscribers.put(key, newSubscriber);
        return key;
    }

    @Override
    public void unsubscribe(String key) {
        subscribers.remove(key);
    }

    @Override
    public void notifyObservers() {
        for (Observer subscriber : subscribers.values()) {
            subscriber.update(dataUpdate);
            dataUpdate.clear();
        }
    }

    public void addProjectile(ShootableEntity projectile) {
        String projectileKey = UUID.randomUUID().toString();
        projectiles.put(projectileKey, projectile);
        dataUpdate.get("add").projectiles.put(projectileKey, projectile);
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
                        this
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
                        this
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

    private void updateAIPlayers() {
        ArrayList<String> keysToRemove = new ArrayList<>();

        for (String key : aiPlayers.keySet()) {
            Tank player = aiPlayers.get(key);
            if (player.getHealth() == 0) {
                keysToRemove.add(key);
                dataUpdate.get("remove").aiPlayers.put(key, player);
            }
        }

        aiPlayers.keySet().removeAll(keysToRemove);
    }

    private void updateHumanPlayers() {
        ArrayList<String> keysToRemove = new ArrayList<>();

        for (String key : humanPlayers.keySet()) {
            Tank player = humanPlayers.get(key);
            if (player.getHealth() == 0) {
                keysToRemove.add(key);
                dataUpdate.get("remove").humanPlayers.put(key, player);
            }
        }

        humanPlayers.keySet().removeAll(keysToRemove);
    }

    private void updateProjectiles() {
        ArrayList<String> keysToRemove = new ArrayList<>();
        // ArrayList<String> keysToUpdate = new ArrayList<>();

        for (String key : projectiles.keySet()) {
            ShootableEntity projectile = projectiles.get(key);
            if (((Bullet)projectile).exploded) {
                if (((Bullet)projectile).explosionLifetime == 0) {
                    dataUpdate.get("update").projectiles.put(key, projectile);
                }
                else if (((Bullet)projectile).explosionLifetime == ((Bullet)projectile).explosionLifetimeMax) {
                    keysToRemove.add(key);
                    dataUpdate.get("remove").projectiles.put(key, projectile);
                }
            }
        }
        projectiles.keySet().removeAll(keysToRemove);
    }

    private void executeCommands(ArrayList<Command> commands) {
        for (Command command : commands) {
            if (command instanceof ShootCommand) {
                command.execute();
            }
        }
        for (Command command : commands) {
            if (command instanceof MoveCommand) {
                MovableEntity entity = ((MoveCommand)command).entity;
                if (isEqual(entity.movementProgress, 1f)) {
                    // record that the player has reached his/her destination
                    entity.coordinates.set(entity.destinationCoordinates);
                    command.execute();
                }
            }
        }
    }

    public void tick() {
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

    public void runGameLoop() {
        dataUpdate.put("add", new LevelData());
        dataUpdate.put("remove", new LevelData());
        dataUpdate.put("update", new LevelData());

        ArrayList<Command> commands = initiateCommands();

        if (healthBarSuppressor.updateCoolDown == 0) {
            healthBarSuppressor.update(kl.captureLKey());
            healthBarSuppressor.updateCoolDown = healthBarSuppressor.updateCoolDownMax;
        }

        executeCommands(commands);

        // update human players
        updateHumanPlayers();

        // update AI players
        updateAIPlayers();

        // update projectiles
        updateProjectiles();

        tick();
        notifyObservers();
    }
}
