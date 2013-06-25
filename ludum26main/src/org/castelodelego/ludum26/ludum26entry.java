package org.castelodelego.ludum26;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ludum26entry extends Game {
	
	WebpageCaller w;
	
	SplashScreen splash;
	MainScreen title;
	AboutScreen about;
	DivideScreen play;
	
	static AssetManager manager;
	LevelManager lmanager;
	
	static final String VERSION = "Version 1.1.0";
	
	public ludum26entry(WebpageCaller webcaller)
	{
		super();
		w = webcaller;
	}
	
	// Called when the game is first started
	@Override
	public void create() {		
		
		splash = new SplashScreen(this);
		title = new MainScreen(this);
		about = new AboutScreen(this);
		play = new DivideScreen(this);
		
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
		manager.load("fairking2.png", Texture.class);
		
		manager.load("sawasdee32.fnt", BitmapFont.class);
		manager.load("sawasdee18.fnt", BitmapFont.class);
		manager.load("Beaulieux.fnt", BitmapFont.class);
		manager.load("Beaulieux-title.fnt", BitmapFont.class);
		
		
		manager.load("about1.png", Texture.class);
		manager.load("about2.png", Texture.class);
		manager.load("about3.png", Texture.class);
		manager.load("rightarrow.png", Texture.class);
		manager.load("leftarrow.png", Texture.class);
		manager.load("bottomarrow.png", Texture.class);
		manager.load("toparrow.png", Texture.class);
		
		
		// Loading levels
		for (int i = 0; i < LevelManager.levelList.length; i++)
		{
			manager.load(LevelManager.levelList[i][0], Pixmap.class);
		}		
		manager.load("purplewah.ogg", Music.class);
		manager.load("filling.ogg", Sound.class);
		manager.load("drawing.ogg", Sound.class);

	}

	//TODO: Add asset Disposal by the end of the game
}
