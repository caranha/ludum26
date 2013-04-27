package org.castelodelego.ludum26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MainScreen implements Screen {

	ludum26entry g; // The overall Game class, used leaving this screen if necessary
	OrthographicCamera camera;

	ShapeRenderer lineDrawer;
	SpriteBatch batch;
	
	boolean initdone = false;
	boolean dofade = true;
	
	BitmapFont text;
	BitmapFont title;
	Texture king;
	
	BitmapFontCache titletext;
	BitmapFontCache playbtn;
	BitmapFontCache aboutbtn;
	BitmapFontCache ld26text;
	
	public MainScreen(ludum26entry game)
	{
		g = game;		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		lineDrawer = new ShapeRenderer();
		batch = new SpriteBatch();
	}
	
	
	private void init()
	{
		Gdx.app.log("MainScreen", "initialized");
		// loading textures
		king = g.manager.get("fairking.png", Texture.class);
		
		//title = g.manager.get("Beaulieux.fnt", BitmapFont.class);
		titletext = new BitmapFontCache(g.manager.get("Beaulieux.fnt", BitmapFont.class),true);
		titletext.addText("The Fair King", 310, 430);
		titletext.setColor(Color.DARK_GRAY);
		
		text = g.manager.get("sawasdee.fnt", BitmapFont.class);
		playbtn = new BitmapFontCache(text,true);
		aboutbtn = new BitmapFontCache(text,true);
		ld26text = new BitmapFontCache(text,true);
		
		playbtn.addText("Play", 360, 250);
		aboutbtn.addText("About", 600, 250);
		ld26text.addMultiLineText("A game for Ludum Dare 26\nby caranha", 800, 100, -60, HAlignment.RIGHT);
		
		playbtn.setColor(Color.DARK_GRAY);
		aboutbtn.setColor(Color.DARK_GRAY);
		ld26text.setColor(Color.DARK_GRAY);
		
		initdone = true;
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1); // clearing the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//batch.setColor(1f, 1f, 1f, 1f);
		batch.draw(king, 0, 0);

		// drawing text
		titletext.draw(batch);
		playbtn.draw(batch);
		aboutbtn.draw(batch);
		ld26text.draw(batch);
		
		
		batch.end();
		
		if(Gdx.input.isTouched())
			g.setScreen(g.play);
		
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		if (initdone == false)
			init();
		dofade = true;

		
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
