package ru.mipt.bit.platformer.classes;

import java.util.HashMap;

import ru.mipt.bit.platformer.classes.Tank;
import ru.mipt.bit.platformer.classes.Obstacle;
import ru.mipt.bit.platformer.classes.ShootableEntity;

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