package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import ru.mipt.bit.platformer.classes.Level;
import ru.mipt.bit.platformer.classes.LevelGraphics;


public class GameDesktopLauncher implements ApplicationListener {
    private Level level;
    private LevelGraphics levelGraphics;

    @Override
    public void create() {
        level = new Level();
        level.initialize();
        levelGraphics = new LevelGraphics(level);
        level.subscribe(levelGraphics);
    }

    @Override
    public void render() {
        level.runGameLoop();
        levelGraphics.render(level);
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
        level.level.dispose();
        levelGraphics.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
