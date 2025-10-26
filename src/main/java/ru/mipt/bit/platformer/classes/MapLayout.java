package ru.mipt.bit.platformer.classes;

import java.util.Set;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class MapLayout {
    public HashMap<String, ArrayList<GridPoint2>> layout;
    private ArrayList<Integer> availableTiles;
    private int mapWidth;
    private int mapHeight;
    private Random rand = new Random();

    public MapLayout(TiledMapTileLayer tiledLayer) {
        layout = new HashMap<>();

        mapWidth = tiledLayer.getWidth();
        mapHeight = tiledLayer.getHeight();

        availableTiles = new ArrayList<>();
        for (int i = 0; i < mapWidth * mapHeight; i++) {
            availableTiles.add(i);
        }
        int nObstacles = rand.nextInt(10);

        ArrayList<GridPoint2> playerCoordinates = new ArrayList<>();
        playerCoordinates.add(getUnoccupiedTile());

        ArrayList<GridPoint2> obstacleCoordinates = new ArrayList<>();
        for (int i = 0; i < nObstacles; i++) {
            obstacleCoordinates.add(getUnoccupiedTile());
        }

        updateLayout(playerCoordinates, obstacleCoordinates);
    }

    public MapLayout(String layoutMapPath) {
        int flatTile = 0;
        layout = new HashMap<>();
        ArrayList<GridPoint2> playerCoordinates = new ArrayList<>();
        ArrayList<GridPoint2> obstacleCoordinates = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(layoutMapPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            if (is == null) {
                throw new IOException("Resource not found: " + layoutMapPath);
            }
            List<String> rowsReversed = reader.lines().collect(Collectors.toList());
            mapHeight = rowsReversed.size();
            mapWidth = rowsReversed.get(0).length();

            for (int i = rowsReversed.size() - 1; i >= 0; i--) {
                String row = rowsReversed.get(i);
                for (int j = 0; j < row.length(); j++) {
                    char c = row.charAt(j);
                    if (c == 'X') {
                        GridPoint2 unoccupiedTile = gridPoint2FromFlatTile(flatTile);
                        playerCoordinates.add(unoccupiedTile);
                    }
                    else if (c == 'T') {
                        GridPoint2 unoccupiedTile = gridPoint2FromFlatTile(flatTile);
                        obstacleCoordinates.add(unoccupiedTile);
                    }
                    flatTile++;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
        updateLayout(playerCoordinates, obstacleCoordinates);
    }

    private GridPoint2 gridPoint2FromFlatTile(int flatTile) {
        GridPoint2 gp2 = new GridPoint2(
            flatTile % mapHeight,
            flatTile / mapHeight
        );
        return gp2;
    }

    private GridPoint2 getUnoccupiedTile() {
        int unoccupiedTileFlatIdx = rand.nextInt(availableTiles.size());
        int unoccupiedFlatTile = availableTiles.remove(unoccupiedTileFlatIdx);
        GridPoint2 unoccupiedTile = gridPoint2FromFlatTile(unoccupiedFlatTile);
        return unoccupiedTile;
    }

    private void updateLayout(ArrayList<GridPoint2> playerCoordinates, ArrayList<GridPoint2> obstacleCoordinates) {
        layout.put("Player", playerCoordinates);
        layout.put("Obstacles", obstacleCoordinates);
    }
}
