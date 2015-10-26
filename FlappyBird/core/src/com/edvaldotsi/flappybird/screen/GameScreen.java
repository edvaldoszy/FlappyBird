package com.edvaldotsi.flappybird.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.edvaldotsi.flappybird.MainGame;
import com.edvaldotsi.flappybird.body.Bird;
import com.edvaldotsi.flappybird.body.Tube;
import com.edvaldotsi.flappybird.util.Helper;

/**
 * Created by Edvaldo on 28/09/2015.
 */
public class GameScreen extends BaseScreen {

    private Body ground;

    /**
     * World camera
     *
     * Used to show bodies
     */
    private OrthographicCamera camera;
    private World world;
    private Bird bird;

    /**
     * Draw objects into the camera
     * Help us to develop out game without use images
     */
    private Box2DDebugRenderer debug;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / Helper.SCALE, Gdx.graphics.getHeight() / Helper.SCALE);
        debug = new Box2DDebugRenderer();
        world = new World(new Vector2(0, -9.8f), false);

        initGround();
        initBird();

        new Tube(world, camera, null);
    }

    private void initGround() {
        ground = Helper.createBody(world, BodyDef.BodyType.StaticBody, 0, 0);
    }

    private void initBird() {
        bird = new Bird(world, camera, null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        control();

        update(delta);
        updateCamera();
        updateGround();
        draw(delta);

        debug.render(world, camera.combined.cpy().scl(Helper.PIXEL_METER));
    }

    private boolean jumping = false;

    /**
     * Capture the keys
     */
    private void control() {
        jumping = Gdx.input.justTouched();
    }

    /**
     * Update coordinates
     *
     * @param delta Delta time
     */
    private void update(float delta) {
        bird.update(delta);
        if (jumping)
            bird.jump();

        world.step(1f / 60f, 6, 2);
    }

    private void updateCamera() {
        camera.position.x = (bird.getBody().getPosition().x + bird.getWidth() / Helper.PIXEL_METER) * Helper.PIXEL_METER;
        camera.update();
    }

    /**
     * Update the ground to follow the camera
     */
    private void updateGround() {
        Vector2 pos = bird.getBody().getPosition();
        ground.setTransform(pos.x, 0, 0);
        /*
        float width = camera.viewportWidth / Helper.PIXEL_METER;
        Vector2 position = ground.getPosition();
        position.x = width / 2;
        ground.getPosition().x = bird.getBody().getPosition().x + bird.getWidth() / Helper.PIXEL_METER;
        ground.setTransform(position, 0f);
        */
    }

    /**
     * Draw the images
     *
     * @param delta Delta time
     */
    private void draw(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Helper.SCALE, height / Helper.SCALE);
        camera.update();

        resizeGround();
    }

    /**
     * Configure the ground size according the screen size
     */
    private void resizeGround() {
        ground.getFixtureList().clear();
        float width = camera.viewportWidth / Helper.PIXEL_METER;
        PolygonShape shape= new PolygonShape();
        shape.setAsBox(width / 2, Helper.BORDER_HEIGHT / 2);
        Fixture fixture = Helper.createShape(ground, shape, "ground");
        shape.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        debug.dispose();
        world.dispose();
    }
}
