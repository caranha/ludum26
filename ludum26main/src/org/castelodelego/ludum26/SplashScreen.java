package org.castelodelego.ludum26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class SplashScreen implements Screen {

	
	ludum26entry g; // The overall Game class, used leaving this screen if necessary
	OrthographicCamera camera;

	ShapeRenderer lineDrawer;
	SpriteBatch batch;
	
	Texture splashimg;
	
	float time;
	float fade;
	
	boolean loaddone;
	float loadprogress;
	
	public SplashScreen(ludum26entry game)
	{
		g = game;		
		loaddone = false;
		loadprogress = 0;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		lineDrawer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		splashimg = new Texture(Gdx.files.internal("splash.png"));
		fade = 0;
	}
	
	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(1, 1, 1, 1); // clearing the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		loaddone = g.manager.update();		
		loadprogress = g.manager.getProgress();

		// splash screen fade crontrol
		time = time+delta;		
		if (time < 0.5)
		{
			fade = time*2;
		}
		if (time > 1.5 && loaddone)
		{
			fade = fade - delta*3;
		}
				
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(splashimg, 0,0);
		batch.end();

		
		// Drawing Fading
		if (fade < 1.0f)
			{
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				lineDrawer.setProjectionMatrix(camera.combined);
				lineDrawer.begin(ShapeType.FilledRectangle);
				lineDrawer.setColor(1f, 1f, 1f, 1-fade);
				// FIXME: Do I need to specify changing sides here, or does the camera takes care of this for me?
				lineDrawer.filledRect(0, 0, 800, 480);		
				lineDrawer.end();
			}
		// End Drawing Fading
		

		if ((fade <= 0) && (loaddone))
			g.setScreen(g.title);
	}
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		time = 0;
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
