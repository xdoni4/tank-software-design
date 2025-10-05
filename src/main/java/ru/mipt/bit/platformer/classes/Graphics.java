package ru.mipt.bit.platformer.classes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createBoundingRectangle;


public class Graphics {
    public Texture texture;
    // TextureRegion represents Texture portion, there may be many TextureRegion instances of the same Texture
    public TextureRegion graphics;
    public Rectangle rectangle;

    public Graphics(String imagePath) {
        this.texture = new Texture(imagePath);
        this.graphics = new TextureRegion(this.texture);
        this.rectangle = createBoundingRectangle(this.graphics);
    }

    public void dispose() {
        texture.dispose();
    }
}