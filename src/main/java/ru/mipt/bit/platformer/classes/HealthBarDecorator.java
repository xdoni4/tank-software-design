package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import ru.mipt.bit.platformer.classes.GameAssets;
import ru.mipt.bit.platformer.classes.MovableEntityGraphics;
import ru.mipt.bit.platformer.classes.DrawableMovable;
import ru.mipt.bit.platformer.classes.MovableEntity;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.classes.ExecutionSuppressor;


public class HealthBarDecorator implements DrawableMovable {
    private MovableEntityGraphics decorated;
    private Tank tank;
    private ExecutionSuppressor executionSuppressor;

    public HealthBarDecorator(MovableEntityGraphics decorated, Tank tank, ExecutionSuppressor executionSuppressor) {
        this.decorated = decorated;
        this.tank = tank;
        this.executionSuppressor = executionSuppressor;
        GameAssets.load();
    }

    public void draw(Batch batch, MovableEntity entity, TileMovement tileMovement) {
        decorated.draw(batch, entity, tileMovement);
        if (executionSuppressor.suppressed == 0) {
            drawHealthBar(batch);
        }
    }

    private void drawHealthBar(Batch batch) {
        float healthPercent = (float) tank.currentHealth / tank.maxHealth;

        float barWidth = decorated.graphics.getRegionWidth();
        float barHeight = 10f;
        float barX = decorated.rectangle.x;
        float barY = decorated.rectangle.y + decorated.graphics.getRegionHeight() + barHeight;

        Color color = healthPercent > 0.5f ? Color.GREEN : 
                     healthPercent > 0.25f ? Color.YELLOW : Color.RED;

        batch.setColor(Color.GRAY);
        batch.draw(GameAssets.whitePixel, barX, barY, barWidth, barHeight);

        batch.setColor(color);
        float fillWidth = barWidth * healthPercent;
        batch.draw(GameAssets.whitePixel, barX, barY, fillWidth, barHeight);

        batch.setColor(Color.WHITE);
    }
}
