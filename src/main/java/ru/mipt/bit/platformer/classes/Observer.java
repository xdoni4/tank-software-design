package ru.mipt.bit.platformer.classes;

import java.util.HashMap;
import java.util.ArrayList;

import ru.mipt.bit.platformer.classes.LevelData;

public interface Observer {
    public void update(HashMap<String, LevelData> dataUpdate);
}
