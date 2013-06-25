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
	
	BitmapFont text;
	Texture king;
	Texture kingest;
	
	BitmapFontCache titletext;
	BitmapFontCache ld26text;

	BitmapFontCache playbtn;
	Rectangle playbtnBox;
	
	BitmapFontCache aboutbtn;
	Rectangle aboutbtnBox;
		
	BitmapFontCache erasebtn;
	Rectangle erasebtnBox;
	
	BitmapFontCache erasebtnConfirm;
	Rectangle erasebtnConfirmBox;
	
	SelectBox menu;
	Boolean master = false;
	
	Music mainmusic; // FIXME: no idea where to put this;
	
	
	static float FADE_T = 0.2f; 
	static final int S_FADEIN = 0;
	static final int S_FADEOUT = 2;
	static final int S_NORMAL = 1;

	
	int currSTATE;
	float fadestate;
		
	static final int SS_main = 0;
	static final int SS_select = 1;
	static final int SS_delete = 2;
	int substate;
	
	
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
		kingest = ludum26entry.manager.get("fairking2.png", Texture.class);
		text = ludum26entry.manager.get("sawasdee32.fnt", BitmapFont.class);
		
		titletext = new BitmapFontCache(ludum26entry.manager.get("Beaulieux-title.fnt", BitmapFont.class),true);
		titletext.addText("The Fair Kingo", 325, 430);
		titletext.setColor(Color.DARK_GRAY);
		
		ld26text = new BitmapFontCache(ludum26entry.manager.get("sawasdee18.fnt", BitmapFont.class),true);
		ld26text.addMultiLineText("A game for Ludum Dare 26, by caranha\n"+ludum26entry.VERSION, 830, 60, -60, HAlignment.RIGHT);
		ld26text.setColor(Color.DARK_GRAY);
		
		mainmusic = ludum26entry.manager.get("purplewah.ogg",Music.class);
		mainmusic.setLooping(true);
		mainmusic.play();
		

		
		playbtn = new BitmapFontCache(text,true);
		aboutbtn = new BitmapFontCache(text,true);
		erasebtn = new BitmapFontCache(text,true);
		erasebtnConfirm = new BitmapFontCache(text,true);
		
		// hardcoded offsets to make the touch areas bigger than the text
		playbtn.addText("Play", 420, 290);
		playbtnBox = new Rectangle(420-30, 290 - playbtn.getBounds().height-30, playbtn.getBounds().width+60,playbtn.getBounds().height+60);
		
		aboutbtn.addText("Help", 600, 290);
		aboutbtnBox = new Rectangle(600-30, 290 - aboutbtn.getBounds().height-30, aboutbtn.getBounds().width+60,aboutbtn.getBounds().height+60);
		
		erasebtn.addText("Clear all data", 450, 160);
		erasebtnBox = new Rectangle(450-15, 160 - erasebtn.getBounds().height-15, erasebtn.getBounds().width+30,erasebtn.getBounds().height+30);
		
		erasebtnConfirm.addMultiLineText("Erase the game's Internal Data?\nClick here to proceed.\nClick outside the box to cancel.", 400, 300, 0, HAlignment.CENTER);
		erasebtnConfirmBox = new Rectangle(400 - erasebtnConfirm.getBounds().width/2 - 20, 300 - erasebtnConfirm.getBounds().height - 20, 
										    erasebtnConfirm.getBounds().width+40, erasebtnConfirm.getBounds().height + 40);
		

		playbtn.setColor(Color.DARK_GRAY);
		aboutbtn.setColor(Color.DARK_GRAY);
		erasebtn.setColor(Color.DARK_GRAY);
		erasebtnConfirm.setColor(Color.DARK_GRAY);

		
		menu = new SelectBox(335,16,450,450);
		
		initdone = true;
	}
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1); // clearing the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Drawing Sprites
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setColor(1, 1, 1,1f);
		if (!master)	
			batch.draw(king, 0, 0);
		else
			batch.draw(kingest,0,0);
		batch.end();

		// drawing text
		switch(substate)
		{
		case SS_main:
			batch.begin();
			batch.setColor(1, 1, 1,1f);
			titletext.draw(batch);
			playbtn.draw(batch);
			aboutbtn.draw(batch);	
			erasebtn.draw(batch);
			ld26text.draw(batch);
			batch.end();
//			DEBUG: uncomment this to get the bounding box for the buttons			
//			lineDrawer.setProjectionMatrix(camera.combined);
//			lineDrawer.begin(ShapeType.Rectangle);
//			lineDrawer.setColor(Color.BLACK);
//			lineDrawer.rect(playbtnBox.x, playbtnBox.y, playbtnBox.width, playbtnBox.height);
//			lineDrawer.rect(aboutbtnBox.x, aboutbtnBox.y, aboutbtnBox.width, aboutbtnBox.height);
//			lineDrawer.rect(erasebtnBox.x, erasebtnBox.y, erasebtnBox.width, erasebtnBox.height);
//			lineDrawer.end();
		break;
		
		case SS_delete:	
			batch.begin();
			titletext.draw(batch);
			playbtn.draw(batch);
			aboutbtn.draw(batch);	
			erasebtn.draw(batch);
			ld26text.draw(batch);
			batch.end();
			
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			lineDrawer.setProjectionMatrix(camera.combined);
			lineDrawer.begin(ShapeType.FilledRectangle);
			lineDrawer.setColor(1f, 1f, 1f, 0.8f);
			lineDrawer.filledRect(0, 0, 800, 480);	
			lineDrawer.setColor(1f, 1f, 1f, 1f);
			lineDrawer.filledRect(erasebtnConfirmBox.x, erasebtnConfirmBox.y, erasebtnConfirmBox.width, erasebtnConfirmBox.height);
			lineDrawer.end();

			lineDrawer.begin(ShapeType.Rectangle);
			lineDrawer.setColor(Color.BLACK);
			lineDrawer.rect(erasebtnConfirmBox.x, erasebtnConfirmBox.y, erasebtnConfirmBox.width, erasebtnConfirmBox.height);
			lineDrawer.end();

			batch.begin();
			erasebtnConfirm.draw(batch);
			batch.end();
			
		break;
		case SS_select:
			menu.Render(camera, lineDrawer, batch);
			break;
		}
		
		// Drawing Fade
		if (currSTATE == S_FADEIN || currSTATE == S_FADEOUT) 
		{
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			lineDrawer.setProjectionMatrix(camera.combined);
			lineDrawer.begin(ShapeType.FilledRectangle);
			lineDrawer.setColor(1f, 1f, 1f, 1 - (fadestate/FADE_T));
			lineDrawer.filledRect(0, 0, 800, 480);		
			lineDrawer.end();
		}
		// End Drawing Fade
		
		
		/*** INPUT ***/
		if (currSTATE == S_NORMAL) // IGNORE INPUT DURING FADE_IN/OUT
		{
			
			// FIXME: This code is uglier than my morning commute
			// All clicks are touches, but not all touches are clicks
			

			if(Gdx.input.justTouched()) // processing "clicks"
			{
				Vector3 rawtouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
				camera.unproject(rawtouch);
				Vector2 touchpos = new Vector2(rawtouch.x, rawtouch.y);
				
				switch (substate)
				{
				case SS_main:
					if (playbtnBox.contains(touchpos.x, touchpos.y))
					{
						menu.start(g.lmanager);
						substate = SS_select;
					}
					if (aboutbtnBox.contains(touchpos.x, touchpos.y))
					{
						leavescreen = true;
						nextScreen = g.about;
					}
					if (erasebtnBox.contains(touchpos.x, touchpos.y))
					{
						substate = SS_delete;
					}
					break;
				case SS_select:
					int ret = menu.catchClick(touchpos.x, touchpos.y);
					if (ret != -1 && g.lmanager.isLevelUnlocked(ret))
					{
						leavescreen = true;
						nextScreen = g.play;
						g.play.curLevel = ret;
					}
					break;
				case SS_delete:
					substate = SS_main;
					if (erasebtnConfirmBox.contains(touchpos.x, touchpos.y))
					{
						g.lmanager.resetLevelData();
					}
					break;
				}
			}
			else if(Gdx.input.isTouched()) // processing "presses"
			{
				Vector3 rawtouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
				camera.unproject(rawtouch);
				Vector2 touchpos = new Vector2(rawtouch.x, rawtouch.y);
				switch (substate)
				{
				case SS_select:
					menu.catchPress(touchpos.x, touchpos.y);
					break;
				}
			}
			else //release all touches
			{
				menu.releasePress();
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
			if (substate == SS_select)
				menu.update(delta);
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
		
		substate = SS_main;
		
		master = g.lmanager.perfectScore();
		
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
