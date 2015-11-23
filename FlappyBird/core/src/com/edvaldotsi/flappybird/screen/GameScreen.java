package com.edvaldotsi.flappybird.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
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

    private Texture[] birdTextures;
    private Texture topTubeTexture, bottomTubeTexture;
    private Texture groundTexture;
    private Texture backgroundTexture;
    private Texture btnPlayTexture;
    private Texture btnGameOverTexture;

    private SpriteBatch batch;

    private Sprite ground1, ground2;

    private ImageButton btnPlay, btnGameOver;

    private boolean gameOver = false;
    private boolean gameStarted = true;

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
            public void endContact(Contact contact) {}
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
        batch = new SpriteBatch();

        initTextures();
        initGround();
        initBird();
        initInformation();
        tubes = new Array<Tube>();
    }

    private void initTextures() {
        birdTextures = new Texture[3];
        birdTextures[0] = new Texture("sprites/bird-1.png");
        birdTextures[1] = new Texture("sprites/bird-2.png");
        birdTextures[2] = new Texture("sprites/bird-3.png");

        topTubeTexture = new Texture("sprites/toptube.png");
        bottomTubeTexture = new Texture("sprites/bottomtube.png");

        backgroundTexture = new Texture("sprites/bg.png");
        groundTexture = new Texture("sprites/ground.png");

        btnPlayTexture = new Texture("sprites/playbtn.png");
        btnGameOverTexture = new Texture("sprites/gameover.png");
    }

    // Check if the bird collided
    private void detectCollision(Fixture fixtureA, Fixture fixtureB) {
        if ("bird".equals(fixtureA.getUserData()) || "bird".equals(fixtureB.getUserData())) {
            gameOver = true; // Game over
        }
    }

    private void initGround() {
        ground = Helper.createBody(world, BodyDef.BodyType.StaticBody, 0, 0);

        float cameraX = 0;
        float height = (Helper.BORDER_HEIGHT* Helper.PIXEL_METER / Helper.SCALE);

        ground1 = new Sprite(groundTexture);
        ground1.setBounds(cameraX, 0, camera.viewportWidth, height);

        ground2 = new Sprite(groundTexture);
        ground2.setBounds(cameraX + camera.viewportWidth, 0, camera.viewportWidth, height);
    }

    private void initBird() {
        bird = new Bird(world, camera, birdTextures);
    }

    private void initInformation() {
        infoStage = new Stage(new FillViewport(infoCamera.viewportWidth, infoCamera.viewportHeight, infoCamera));

        Label.LabelStyle style = new Label.LabelStyle();
        //style.font = new BitmapFont(Gdx.files.local("fonts/roboto.ttf"));
        //lbScore = new Label("0", style);

        ImageButton.ImageButtonStyle btnStyle = new ImageButton.ImageButtonStyle();
        btnStyle.up = new SpriteDrawable(new Sprite(btnPlayTexture));
        btnPlay = new ImageButton(btnStyle);
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameStarted = true;
            }
        });
        infoStage.addActor(btnPlay);

        btnStyle = new ImageButton.ImageButtonStyle();
        btnStyle.up = new SpriteDrawable(new Sprite(btnGameOverTexture));
        btnGameOver = new ImageButton(btnStyle);
        btnGameOver.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
            }
        });
        infoStage.addActor(btnGameOver);
    }

    private void restartGame() {
        // restart the game
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        control();

        draw(delta);
        update(delta);
        updateInformations();
        if (gameStarted)
            updateTubes();

        if (!gameOver) {
            updateCamera();
            updateGround();
        }

        //debug.render(world, camera.combined.cpy().scl(Helper.PIXEL_METER));
    }

    private void updateInformations() {
        //lbScore.setText(score + "");
        //lbScore.setPosition(infoCamera.viewportWidth / 2 - lbScore.getPrefWidth() / 2, infoCamera.viewportHeight - lbScore.getPrefHeight());

        btnPlay.setPosition(
                camera.viewportWidth / 2 - btnPlay.getPrefWidth() / 2,
                camera.viewportHeight / 2 - btnPlay.getPrefHeight()
        );
        btnPlay.setVisible(!gameOver);

        btnPlay.setPosition(
                camera.viewportWidth / 2 - btnGameOver.getPrefWidth() / 2,
                camera.viewportHeight / 2 - btnGameOver.getPrefHeight() / 2
        );
        btnPlay.setVisible(gameOver);
    }

    private void updateTubes() {
        while (tubes.size < 4) {

            Tube lastTube = null;
            if (tubes.size > 0)
                lastTube = tubes.peek();

            tubes.add(new Tube(world, camera, lastTube, topTubeTexture, bottomTubeTexture));
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

        bird.getBody().setFixedRotation(!gameOver);
        bird.update(delta, !gameOver);
        if (jumping && !gameOver && gameStarted)
            bird.jump();

        if (gameStarted)
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

        float cameraX = camera.position.x - (camera.viewportWidth / 2) - camera.viewportWidth;
        if (ground1.getX() < cameraX)
            ground1.setBounds(ground2.getX() + camera.viewportWidth, 0, ground1.getWidth(), ground1.getHeight());

        if (ground2.getX() < cameraX)
            ground2.setBounds(ground1.getX() + camera.viewportWidth, 0, ground2.getWidth(), ground2.getHeight());
    }

    /**
     * Draw the images
     *
     * @param delta Delta time
     */
    private void draw(float delta) {
        batch.begin();

        batch.setProjectionMatrix(infoCamera.combined);
        batch.draw(backgroundTexture, 0, 0, infoCamera.viewportWidth, infoCamera.viewportHeight);

        batch.setProjectionMatrix(camera.combined);

        // Draw the bird
        bird.render(batch);

        // Draw the tubes
        for (Tube t : tubes) {
            t.render(batch);
        }

        // Draw the ground
        ground1.draw(batch);
        ground2.draw(batch);

        batch.end();
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
        Helper.createShape(ground, shape, "ground");
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

        birdTextures[0].dispose();
        birdTextures[1].dispose();
        birdTextures[2].dispose();

        topTubeTexture.dispose();
        bottomTubeTexture.dispose();

        btnGameOverTexture.dispose();
        groundTexture.dispose();

        btnPlayTexture.dispose();
        backgroundTexture.dispose();

        batch.dispose();
    }
}
