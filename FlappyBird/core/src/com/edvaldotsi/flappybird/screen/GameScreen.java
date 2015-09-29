package com.edvaldotsi.flappybird.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.edvaldotsi.flappybird.MainGame;

/**
 * Created by Edvaldo on 28/09/2015.
 */
public class GameScreen extends BaseScreen {

    private static final float SCALE = 2;
    private static final float PIXEL_METERS = 32;

    private OrthographicCamera camera;
    private World world;

    /**
     * Desenha os objetos na tela
     * Auxilia no desenvolvimento, não precisando colocar imagens para serem exibidas
     */
    private Box2DDebugRenderer debug;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
        debug = new Box2DDebugRenderer();
        world = new World(new Vector2(0, -9.8f), false);

        initBird();
    }

    private void initBird() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;

        float y = (Gdx.graphics.getHeight() / SCALE / 2) / PIXEL_METERS + 10;
        float x = (Gdx.graphics.getHeight() / SCALE / 2) / PIXEL_METERS + 2;
        def.position.set(x, y);
        def.fixedRotation = true;

        Body body = world.createBody(def);
        CircleShape shape = new CircleShape();
        shape.setRadius(20 / PIXEL_METERS);

        Fixture fixture = body.createFixture(shape, 1);
        shape.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        camera.update();

        world.step(delta, 6, 2);
        debug.render(world, camera.combined.scl(PIXEL_METERS));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
