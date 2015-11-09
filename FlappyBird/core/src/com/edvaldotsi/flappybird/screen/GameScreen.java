package com.edvaldotsi.flappybird.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
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
    private OrthographicCamera camera, infoCamera;
    private World world;
    private Bird bird;
    private Array<Tube> tubes;

    private Stage infoStage;

    private Label lbScore;
    private int score = 0;

    private boolean gameOver = false;

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
        infoCamera = new OrthographicCamera(Gdx.graphics.getWidth() / Helper.SCALE, Gdx.graphics.getHeight() / Helper.SCALE);
        debug = new Box2DDebugRenderer();
        world = new World(new Vector2(0, -9.8f), false);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                detectCollision(contact.getFixtureA(), contact.getFixtureB());
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        initGround();
        initBird();
        initInformation();
        tubes = new Array<Tube>();
    }

    // Check if the bird collided
    private void detectCollision(Fixture fixtureA, Fixture fixtureB) {
        if ("bird".equals(fixtureA.getUserData()) || "bird".equals(fixtureB.getUserData())) {
            // Game over
        }
    }

    private void initGround() {
        ground = Helper.createBody(world, BodyDef.BodyType.StaticBody, 0, 0);
    }

    private void initBird() {
        bird = new Bird(world, camera, null);
    }

    private void initInformation() {
        infoStage = new Stage(new FillViewport(infoCamera.viewportWidth, infoCamera.viewportHeight, infoCamera));

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont(Gdx.files.external("fonts/roboto.ttf"));
        lbScore = new Label("", style);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        control();

        update(delta);
        updateInformations();
        updateTubes();
        updateCamera();
        updateGround();
        draw(delta);

        debug.render(world, camera.combined.cpy().scl(Helper.PIXEL_METER));
    }

    private void updateInformations() {
        lbScore.setText(score + "");
        lbScore.setPosition(infoCamera.viewportWidth / 2 - lbScore.getPrefWidth() / 2, infoCamera.viewportHeight - lbScore.getPrefHeight());
    }

    private void updateTubes() {
        while (tubes.size < 4) {

            Tube lastTube = null;
            if (tubes.size > 0)
                lastTube = tubes.peek();

            tubes.add(new Tube(world, camera, lastTube));
        }

        for (Tube t : tubes) {
            float cameraLeftPosition = bird.getBody().getPosition().x - (camera.viewportWidth / 2 / Helper.PIXEL_METER);
            if (cameraLeftPosition > t.getX()) {
                t.remove();
                tubes.removeValue(t, true);
            } else if (!t.isPassed() && t.getX() < bird.getBody().getPosition().x) {
                t.setPassed(true);
                score++;
            }
        }
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
        infoStage.act(delta);

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

        infoCamera.setToOrtho(false, width / Helper.SCALE, height / Helper.SCALE);
        infoCamera.update();

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
        //Fixture fixture = Helper.createShape(ground, shape, "ground");
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
        infoStage.dispose();
    }
}
