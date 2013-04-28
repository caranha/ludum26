package org.castelodelego.ludum26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MainScreen implements Screen {

	ludum26entry g; // The overall Game class, used leaving this screen if necessary
	OrthographicCamera camera;

	ShapeRenderer lineDrawer;
	SpriteBatch batch;
	
	boolean initdone = false;
	boolean dofade = true;
	
	BitmapFont text;
	Texture king;
	
	BitmapFontCache titletext;

	BitmapFontCache playbtn;
	Rectangle playbtnBox;
	
	BitmapFontCache aboutbtn;
	Rectangle aboutbtnBox;
	
	BitmapFontCache ld26text;
	
	Music mainmusic; // FIXME: no idea where to put this;
	
	
	static float FADE_T = 0.2f; 
	static final int S_FADEIN = 0;
	static final int S_FADEOUT = 2;
	static final int S_NORMAL = 1;
	int currSTATE;
	float fadestate;
	
	boolean leavescreen;
	Screen nextScreen;
	
	
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
		king = ludum26entry.manager.get("fairking.png", Texture.class);
		
		titletext = new BitmapFontCache(ludum26entry.manager.get("Beaulieux-title.fnt", BitmapFont.class),true);
		titletext.addText("The Fair Kingo", 325, 430);
		titletext.setColor(Color.DARK_GRAY);
		
		mainmusic = ludum26entry.manager.get("purplewah.ogg",Music.class);
		mainmusic.setLooping(true);
		mainmusic.play();
		
		text = ludum26entry.manager.get("sawasdee.fnt", BitmapFont.class);
		playbtn = new BitmapFontCache(text,true);
		aboutbtn = new BitmapFontCache(text,true);
		ld26text = new BitmapFontCache(text,true);
		
		playbtn.addText("Play", 360, 250);
		playbtnBox = new Rectangle(360, 250 - playbtn.getBounds().height, playbtn.getBounds().width,playbtn.getBounds().height);
		
		aboutbtn.addText("About", 600, 250);
		aboutbtnBox = new Rectangle(600, 250 - aboutbtn.getBounds().height, aboutbtn.getBounds().width,aboutbtn.getBounds().height);
		
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
		
		// Drawing Sprites
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(king, 0, 0);

		// drawing text
		titletext.draw(batch);
		playbtn.draw(batch);
		aboutbtn.draw(batch);
		ld26text.draw(batch);
		batch.end();

		
		// Drawing Fade
		if (currSTATE == S_FADEIN || currSTATE == S_FADEOUT) 
		{
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			lineDrawer.setProjectionMatrix(camera.combined);
			lineDrawer.begin(ShapeType.FilledRectangle);
			lineDrawer.setColor(1f, 1f, 1f, 1 - (fadestate/FADE_T));
			// FIXME: Do I need to specify changing sides here, or does the camera takes care of this for me?
			// What does the above mean??
			lineDrawer.filledRect(0, 0, 800, 480);		
			lineDrawer.end();
		}
		// End Drawing Fade
		
		
		/*** INPUT ***/
		if (currSTATE == S_NORMAL) // IGNORE INPUT DURING FADE_IN/OUT
		{
			if(Gdx.input.isTouched())
			{
				Vector3 rawtouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
				camera.unproject(rawtouch);
				Vector2 touchpos = new Vector2(rawtouch.x, rawtouch.y);
				
				if (playbtnBox.contains(touchpos.x, touchpos.y))
				{
					leavescreen = true;
					nextScreen = g.play;
				}
			}
		}
		
		/*** UPDATES ***/
		//Gdx.app.debug("statemachine", "state: " + currSTATE + " fadestate: " + fadestate/FADE_T + " Delta: " + delta);
		switch(currSTATE)
		{
		case S_FADEIN:
			fadestate += delta;
			if (fadestate/FADE_T >= 1)
				currSTATE = S_NORMAL;
			break;
		case S_FADEOUT:
			fadestate -= delta;
			if (fadestate/FADE_T <= 0)
				g.setScreen(nextScreen);
			break;
		case S_NORMAL:
			if (leavescreen)
				currSTATE = S_FADEOUT;
			break;
		}
				
	}
	
	

	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		if (initdone == false)
			init();
		Gdx.app.log("transition", "Entered Main Screen");

		currSTATE = S_FADEIN;
		fadestate = 0;
		leavescreen = false;
		
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
