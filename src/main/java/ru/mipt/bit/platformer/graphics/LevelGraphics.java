package ru.mipt.bit.platformer.graphics;

import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import ru.mipt.bit.platformer.LevelData;
import ru.mipt.bit.platformer.util.TileMovement;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

import ru.mipt.bit.platformer.MovableEntity;
import ru.mipt.bit.platformer.Obstacle;
import ru.mipt.bit.platformer.Tank;
import ru.mipt.bit.platformer.DrawableMovable;
import ru.mipt.bit.platformer.ShootableEntity;
import ru.mipt.bit.platformer.Level;
import ru.mipt.bit.platformer.ExecutionSuppressor;

import ru.mipt.bit.platformer.Observer;


public class LevelGraphics implements Observer {
    private Batch batch;

    private TiledMap level;
    private TiledMapTileLayer groundLayer;
    private TileMovement tileMovement;
    private MapRenderer levelRenderer;

    private HashMap<String, DrawableMovable> humanPlayersGraphics;
    private HashMap<String, DrawableMovable> aiPlayersGraphics;
    private HashMap<String, DrawableMovable> projectilesGraphics;
    private HashMap<String, Graphics> obstaclesGraphics;

    private ExecutionSuppressor healthBarSuppressor;

    public LevelGraphics(Level level) {
        batch = new SpriteBatch();
        levelRenderer = createSingleLayerMapRenderer(level.level, batch);
        groundLayer = level.groundLayer;
        tileMovement = level.tileMovement;

        healthBarSuppressor = level.healthBarSuppressor;


        humanPlayersGraphics = new HashMap<>();
        aiPlayersGraphics = new HashMap<>();
        obstaclesGraphics = new HashMap<>();
        projectilesGraphics = new HashMap<>();

        for (String key : level.humanPlayers.keySet()) {
            Tank humanPlayer = level.humanPlayers.get(key);
            humanPlayersGraphics.put(
                key,
                new HealthBarDecorator(
                    new MovableEntityGraphics("images/tank_blue.png"),
                    humanPlayer,
                    healthBarSuppressor
                )
            );
        }

        for (String key : level.aiPlayers.keySet()) {
            aiPlayersGraphics.put(
                key,
                new HealthBarDecorator(
                    new MovableEntityGraphics("images/tank_safari_mesh.png"),
                    level.aiPlayers.get(key),
                    healthBarSuppressor
                )
            );
        }

        for (String key : level.obstacles.keySet()) {
            obstaclesGraphics.put(
                key,
                new Graphics("images/greenTree.png")
            );
        }

        for (String key : level.obstacles.keySet()) {
            Obstacle obstacle = level.obstacles.get(key);
            Graphics obstacleG = obstaclesGraphics.get(key);
            moveRectangleAtTileCenter(groundLayer, obstacleG.rectangle, obstacle.coordinates);
        }
    }

    private void glClearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void update(HashMap<String, LevelData> dataUpdate) {
        LevelData addedData = dataUpdate.get("add");
        LevelData removedData = dataUpdate.get("remove");
        LevelData updatedData = dataUpdate.get("update");

        for (String key : addedData.humanPlayers.keySet()) {
            Tank humanPlayer = addedData.humanPlayers.get(key);
            humanPlayersGraphics.put(
                key,
                new HealthBarDecorator(
                    new MovableEntityGraphics("images/tank_blue.png"),
                    humanPlayer,
                    healthBarSuppressor
                )
            );
        }

        for (String key : addedData.aiPlayers.keySet()) {
            Tank aiPlayer = addedData.aiPlayers.get(key);
            aiPlayersGraphics.put(
                key,
                new HealthBarDecorator(
                    new MovableEntityGraphics("images/tank_blue.png"),
                    aiPlayer,
                    healthBarSuppressor
                )
            );
        }

        for (String key : addedData.projectiles.keySet()) {
            ShootableEntity projectile = addedData.projectiles.get(key);
            projectilesGraphics.put(
                key,
                new MovableEntityGraphics("images/bullet.png")
            );
        }

        for (String key : addedData.obstacles.keySet()) {
            Obstacle obstacle = addedData.obstacles.get(key);
            Graphics obstacleG = new Graphics("images/greenTree.png");
            obstaclesGraphics.put(
                key,
                obstacleG
            );
            moveRectangleAtTileCenter(groundLayer, obstacleG.rectangle, obstacle.coordinates);
        }

        for (String key : removedData.humanPlayers.keySet()) {
            humanPlayersGraphics.remove(key);
        }

        for (String key : removedData.aiPlayers.keySet()) {
            aiPlayersGraphics.remove(key);
        }

        for (String key : removedData.projectiles.keySet()) {
            projectilesGraphics.remove(key);
        }

        for (String key : updatedData.projectiles.keySet()) {
            MovableEntityGraphics projectileGraphics = (MovableEntityGraphics) projectilesGraphics.get(key);
            projectileGraphics.texture = new Texture("images/explosion.png");
            projectileGraphics.graphics = new TextureRegion(projectileGraphics.texture);
        }
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

    public void render(Level level) {
        startRendering();

        // render human players
        renderPlayers(level.humanPlayers, humanPlayersGraphics);

        // render AI players
        renderPlayers(level.aiPlayers, aiPlayersGraphics);

        // render obstacles
        renderObstacles(obstaclesGraphics);

        // render projectiles
        renderProjectiles(level.projectiles, projectilesGraphics);

        finishRendering();
    }

    public void dispose() {
        batch.dispose();
    }
}