package com.edvaldotsi.flappybird.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.edvaldotsi.flappybird.BodyEditorLoader;
import com.edvaldotsi.flappybird.util.Helper;

/**
 * Created by Edvaldo on 05/10/2015.
 */
public class Bird {

	private final World world;
	private final OrthographicCamera camera;
	private final Texture[] textures;

	private Body body;

	public Bird(World world, OrthographicCamera camera, Texture[] textures) {
		this.world = world;
		this.camera = camera;
		this.textures = textures;

		initBody();
	}
	
	private void initBody() {
		float x = (camera.viewportWidth / 2) / Helper.PIXEL_METER;
		float y = (camera.viewportHeight / 2) / Helper.PIXEL_METER;

        body = Helper.createBody(world, BodyDef.BodyType.DynamicBody, x, y);

        FixtureDef def = new FixtureDef();
        def.density = 1;
        def.friction = 0.4f;
        def.restitution = 0.3f;

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("physics/bird.json"));
        loader.attachFixture(body, "Bird", def, 1, "bird");
	}

    public void update(float delta, boolean move) {
        if (move) {
            body.setLinearVelocity(3f, body.getLinearVelocity().y);
            updateRotation();
        }
    }

    private void updateRotation() {
        float speedY = body.getLinearVelocity().y;
        float rotation;

        if (speedY < 0) {
            rotation = -15; // Going down
        } else if (speedY > 0) {
            rotation = 15; // going up
        } else {
            rotation = 0;
        }
        rotation = (float) Math.toRadians(rotation);
        body.setTransform(body.getPosition(), rotation);
    }

    /**
     * Apply a positive force to simulate the bird jump
     */
    public void jump() {
        body.setLinearVelocity(body.getLinearVelocity().x, 0);
        body.applyForceToCenter(0, 100, false);
    }

    public Body getBody() {
        return body;
    }

    public int getWidth() {
        return 34;
    }
}
