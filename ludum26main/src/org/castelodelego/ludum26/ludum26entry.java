package org.castelodelego.ludum26;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ludum26entry extends Game {
	
	SplashScreen splash;
	MainScreen title;
	AboutScreen about;
	SelectScreen menu;
	DivideScreen play;
	
	AssetManager manager;
	

	// Called when the game is first started
	@Override
	public void create() {		
		
		splash = new SplashScreen(this);
		title = new MainScreen(this);
		about = new AboutScreen(this);
		play = new DivideScreen(this);
		play = new DivideScreen(this);
		
		// LOADING ASSETS
		manager = new AssetManager();
		manager.load("fairking.png", Texture.class);
		manager.load("sawasdee.fnt", BitmapFont.class);
		manager.load("Beaulieux.fnt", BitmapFont.class);
		//manager.load("data/mymusic.ogg", Music.class);
		
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		setScreen(splash); 
	
	}

}
