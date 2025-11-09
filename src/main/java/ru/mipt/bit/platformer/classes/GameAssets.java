package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class GameAssets {
    public static Texture whitePixel;

    public static void load() {
        // Create a 1x1 white pixel texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    public static void dispose() {
        if (whitePixel != null) {
            whitePixel.dispose();
        }
    }
}