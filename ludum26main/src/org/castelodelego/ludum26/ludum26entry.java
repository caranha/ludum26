package org.castelodelego.ludum26;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class ludum26entry extends Game {
	
	DivideScreen gamescreen;

	// Called when the game is first started
	@Override
	public void create() {		
		gamescreen = new DivideScreen(this);
		
		// Load Assets Here
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		setScreen(gamescreen); 
	
	}

}
