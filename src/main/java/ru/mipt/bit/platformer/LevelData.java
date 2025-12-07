package ru.mipt.bit.platformer;

import java.util.HashMap;


public class LevelData {
    public HashMap<String, Tank> humanPlayers;
    public HashMap<String, Tank> aiPlayers;
    public HashMap<String, Obstacle> obstacles;
    public HashMap<String, ShootableEntity> projectiles;

    public LevelData() {
        humanPlayers = new HashMap<>();
        aiPlayers = new HashMap<>();
        obstacles = new HashMap<>();
        projectiles = new HashMap<>();
    }
}