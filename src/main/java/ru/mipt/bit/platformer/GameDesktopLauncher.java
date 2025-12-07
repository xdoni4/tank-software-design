package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import ru.mipt.bit.platformer.graphics.LevelGraphics;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;

public class GameDesktopLauncher extends ApplicationAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Level level;
    private LevelGraphics levelGraphics;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void create() {
        GameProperties props = applicationContext.getBean(GameProperties.class);

        level = applicationContext.getBean(Level.class);
        levelGraphics = applicationContext.getBean(LevelGraphics.class);

        level.initialize();
        levelGraphics.initialize(level);
        level.subscribe(levelGraphics);
    }

    @Override
    public void render() {
        level.runGameLoop();
        levelGraphics.render(level);
    }

    @Override
    public void dispose() {
        if (levelGraphics != null) levelGraphics.dispose();
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext springContext = SpringApplication.run(GameConfig.class, args);

        String[] names = springContext.getBeanDefinitionNames();
        GameProperties props = springContext.getBean(GameProperties.class);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(
            props.getWindow().getWidth(),
            props.getWindow().getHeight()
        );

        GameDesktopLauncher launcher = new GameDesktopLauncher();
        launcher.setApplicationContext(springContext);

        new Lwjgl3Application(launcher, config);
    }
}
