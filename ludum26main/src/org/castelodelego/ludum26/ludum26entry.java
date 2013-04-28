package org.castelodelego.ludum26;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ludum26entry extends Game {
	
	SplashScreen splash;
	MainScreen title;
	AboutScreen about;
	SelectScreen menu;
	DivideScreen play;
	
	static AssetManager manager;
	LevelManager lmanager;
	

	// Called when the game is first started
	@Override
	public void create() {		
		
		splash = new SplashScreen(this);
		title = new MainScreen(this);
		about = new AboutScreen(this);
		play = new DivideScreen(this);
		menu = new SelectScreen(this);
		
		queueAssets();
		
		lmanager = new LevelManager();
		lmanager.loadLevelData();
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		setScreen(splash); 
	
	}
	
	private void queueAssets()
	{
		manager = new AssetManager();
		
		// Loading images
		manager.load("fairking.png", Texture.class);
		manager.load("sawasdee.fnt", BitmapFont.class);
		manager.load("Beaulieux.fnt", BitmapFont.class);
		manager.load("Beaulieux-title.fnt", BitmapFont.class);
		
		// Loading levels
		for (int i = 0; i < LevelManager.levelList.length; i++)
		{
			manager.load(LevelManager.levelList[i][0], Pixmap.class);
		}
		
		//manager.load("data/mymusic.ogg", Music.class);

	}

	//TODO: Add asset Disposal by the end of the game
}
