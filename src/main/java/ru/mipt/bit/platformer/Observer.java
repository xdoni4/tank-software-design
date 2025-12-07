package ru.mipt.bit.platformer;

import java.util.HashMap;
import java.util.ArrayList;


public interface Observer {
    public void update(HashMap<String, LevelData> dataUpdate);
}
