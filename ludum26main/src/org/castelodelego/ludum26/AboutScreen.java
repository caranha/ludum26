package org.castelodelego.ludum26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class AboutScreen implements Screen {

	ludum26entry g; // The overall Game class, used leaving this screen if necessary
	OrthographicCamera camera;

	ShapeRenderer lineDrawer;
	SpriteBatch batch;
	
	Texture btnTop;
	Rectangle btnTopBox;
	Texture btnLeft;
	Rectangle btnLeftBox;
	Texture btnRight;
	Rectangle btnRightBox;
	Rectangle linkBox;
	
	Texture[] aboutscrns;
	
	float currpos;
	int selectedindex;
	float desiredpos;
	static final float SNAP = 100;
	static final float slidespeed = 1600;
	
	
	// For fading
	static float FADE_T = 0.2f; 
	static final int S_FADEIN = 0;
	static final int S_FADEOUT = 2;
	static final int S_NORMAL = 1;
	int currSTATE;
	float fadestate;
	
	boolean leavescreen;
	boolean initdone;
	
	
	public AboutScreen(ludum26entry game)
	{
		g = game;		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		lineDrawer = new ShapeRenderer();
		batch = new SpriteBatch();
		initdone = false;
	}

	private void init()
	{
		// loading textures
		aboutscrns = new Texture[3];
		aboutscrns[0] = ludum26entry.manager.get("about1.png", Texture.class);
		aboutscrns[1] = ludum26entry.manager.get("about2.png", Texture.class);
		aboutscrns[2] = ludum26entry.manager.get("about3.png", Texture.class);
		
		btnLeft = ludum26entry.manager.get("leftarrow.png", Texture.class);
		btnLeftBox = new Rectangle(16,410,64,64);
		btnRight = ludum26entry.manager.get("rightarrow.png", Texture.class);
		btnRightBox = new Rectangle(720,410,64,64);
		btnTop = ludum26entry.manager.get("toparrow.png", Texture.class);
		btnTopBox = new Rectangle(368,410,64,64);
		initdone = true;
		
		linkBox = new Rectangle(0,0,800,100);
	}
	
	
	
	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(1, 1, 1, 1); // clearing the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Drawing Sprites
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setColor(1, 1, 1, 1f);
		for (int i = 0; i < 3; i++)
		{
			batch.draw(aboutscrns[i], 800*i - currpos, 0);
		}

		// drawing text
		batch.setColor(1, 1, 1, 0.7f);
		batch.draw(btnTop, btnTopBox.x, btnTopBox.y);
		batch.draw(btnLeft, btnLeftBox.x, btnLeftBox.y);
		batch.draw(btnRight, btnRightBox.x, btnRightBox.y);
		batch.end();
		
//		lineDrawer.setProjectionMatrix(camera.combined);
//		lineDrawer.begin(ShapeType.Rectangle);
//		lineDrawer.setColor(Color.BLACK);
//		lineDrawer.rect(linkBox.x, linkBox.y, linkBox.width, linkBox.height);	
//		lineDrawer.end();
		
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
		
		
		
		
		/*** INPUT ***/
		if (currSTATE == S_NORMAL) // IGNORE INPUT DURING FADE_IN/OUT
		{
			if(Gdx.input.justTouched())
			{
				Vector3 rawtouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
				camera.unproject(rawtouch);
				Vector2 touchpos = new Vector2(rawtouch.x, rawtouch.y);
				
				// Top Button, Return
				if (btnTopBox.contains(touchpos.x, touchpos.y))
				{
					leavescreen = true;
				}
				
				if (btnRightBox.contains(touchpos.x, touchpos.y))
					selectedindex++;
				
				if (btnLeftBox.contains(touchpos.x, touchpos.y))
					selectedindex--;
				
				selectedindex = (selectedindex+3)%3;
				desiredpos = selectedindex*800;
				
				if (linkBox.contains(touchpos.x, touchpos.y) && selectedindex == 2)
					g.w.callWebpage("http://claus.castelodelego.org/flyingrock");
			}
		}
		
		
		/*** UPDATES ***/
		
		// UPDATE THE POSITION OF THE SCREEN
		if (desiredpos != currpos)
		{
			if (Math.abs(currpos - desiredpos) < SNAP)
			{
				currpos = desiredpos;
			}
			else
			{
				if (desiredpos < currpos)
					currpos -= slidespeed*delta;
				else
					currpos += slidespeed*delta;
			}
		}
		
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
				g.setScreen(g.title);
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
		currSTATE = S_FADEIN;
		fadestate = 0;
		leavescreen = false;
		selectedindex = 0;
		currpos = 0;
		desiredpos = 0;
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
