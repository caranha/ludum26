package org.castelodelego.ludum26;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main implements WebpageCaller{
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "The Fair King";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 480;
		
		
		new LwjglApplication(new ludum26entry(new Main()), cfg);
	}

	@Override
	public void callWebpage(String address) {
		Gdx.app.log("Calling Intent","Trying to open "+address);
	}
}
