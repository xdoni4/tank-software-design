package ru.mipt.bit.platformer;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game")
public class GameProperties {
    private Window window = new Window();
    private Level level = new Level();

    public Window getWindow() { return window; }
    public Level getLevel() { return level; }

    public static class Window {
        private int width = 1280;
        private int height = 1024;

        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
    }

    public static class Level {
        private int widthTiles = 10;
        private int heightTiles = 8;
        private int tileSize = 128;

        public int getWidthTiles() { return widthTiles; }
        public void setWidthTiles(int widthTiles) { this.widthTiles = widthTiles; }
        public int getHeightTiles() { return heightTiles; }
        public void setHeightTiles(int heightTiles) { this.heightTiles = heightTiles; }
        public int getTileSize() { return tileSize; }
        public void setTileSize(int tileSize) { this.tileSize = tileSize; }
    }
}