package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import ru.mipt.bit.platformer.GameAssets;
import ru.mipt.bit.platformer.DrawableMovable;
import ru.mipt.bit.platformer.MovableEntity;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.ExecutionSuppressor;
import ru.mipt.bit.platformer.Tank;


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
        float healthPercent = (float) tank.getHealth() / tank.getMaxHealth();

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
