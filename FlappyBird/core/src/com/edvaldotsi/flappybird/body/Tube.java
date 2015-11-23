package com.edvaldotsi.flappybird.body;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.edvaldotsi.flappybird.util.Helper;

/**
 * Created by Edvaldo on 26/10/2015.
 */
public class Tube  {

    private final World world;
    private final OrthographicCamera camera;
    private Body topBody, bottomBody;

    private float x, topY, bottomY;
    private float width, height;
    private boolean scored = false;

    private Tube lastTube;
    private Texture topTexture;
    private Texture bottomTexture;
    private boolean passed;

    public Tube(World world, OrthographicCamera camera, Tube lastTube, Texture topTexture, Texture bottomTexture) {
        this.world = world;
        this.camera = camera;
        this.lastTube = lastTube;
        this.topTexture = topTexture;
        this.bottomTexture = bottomTexture;

        initPosition();
        initTopBody();
        initBottomBody();
    }

    private void initPosition() {
        width = 40 / Helper.PIXEL_METER;
        height = camera.viewportHeight / Helper.PIXEL_METER;

        float startX = width;
        if (lastTube != null) {
            startX = lastTube.getX();
        }
        x = startX + 8; // Horizontal space between new tubes

        float parse = (height - Helper.BORDER_HEIGHT) / 6; // Screen height divided by 6. Used to find the next tube position Y
        int random = MathUtils.random(1, 3);

        bottomY = Helper.BORDER_HEIGHT + (parse * random) - (height / 2);
        topY = bottomY + height + 2f; // Vertical space between the tubes
    }

    private void initTopBody() {
        topBody = Helper.createBody(world, BodyDef.BodyType.StaticBody, x, topY);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        Helper.createShape(topBody, shape, "TOP_TUBE");
        shape.dispose();
    }

    private void initBottomBody() {
        bottomBody = Helper.createBody(world, BodyDef.BodyType.StaticBody, x, bottomY);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        Helper.createShape(bottomBody, shape, "BOTTOM_TUBE");
        shape.dispose();
    }

    public void remove() {
        world.destroyBody(topBody);
        world.destroyBody(bottomBody);
    }

    public void render(SpriteBatch batch) {
        float x = (topBody.getPosition().x - width / 2) * Helper.PIXEL_METER;
        float y = (topBody.getPosition().y - height / 2) * Helper.PIXEL_METER;
        batch.draw(topTexture, x, y, width * Helper.PIXEL_METER, height * Helper.PIXEL_METER);

        x = (bottomBody.getPosition().x - width / 2) * Helper.PIXEL_METER;
        y = (bottomBody.getPosition().y - height / 2) * Helper.PIXEL_METER;
        batch.draw(bottomTexture, x, y, width * Helper.PIXEL_METER, height * Helper.PIXEL_METER);
    }

    public float getX() {
        return x;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
