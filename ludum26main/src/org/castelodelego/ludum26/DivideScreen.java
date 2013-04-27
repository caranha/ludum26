package org.castelodelego.ludum26;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/***
 * Main game class. In this screen, the player is presented with a puzzle (an image), 
 * and has to divide this image with a single touch.
 * 
 * @author caranha
 */

public class DivideScreen implements Screen {
	
	static int MAXLINESIZE = 500;
	
	boolean initialized;
	boolean startflood;
	float timeaccum;
	
	static float flooddelta = 0.05f;
	static int floodsteps = 5000;

	ludum26entry g; // The overall Game class, used leaving this screen if necessary
	OrthographicCamera camera;

	ShapeRenderer lineDrawer;
	SpriteBatch batch;
	
	Array<Vector2> dividingLine; // This array contains the Line created by the player
	boolean nowDrawing; // Turns to true when the player starts drawing, and false when he stops

	PuzzleImage puzzle;
	Vector2 puzzlepos;
	Texture puzzleDrawing;
	Texture puzzleFlood;
	
	// Image to be drawn
	// Geometric data of this image
	
	public DivideScreen(ludum26entry game)
	{
		g = game;		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		dividingLine = new Array<Vector2>();
		
		lineDrawer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		initialized = false;
	}
	
	public void init()
	{
		if (!initialized)
		{
			Gdx.app.log("DivideScreen","initialized");
			
			dividingLine.clear();
		
			puzzle = new PuzzleImage("levels/testlevel.png",g);
			
			puzzleDrawing = new Texture(puzzle.orig);
			puzzleDrawing.draw(puzzle.orig, 0, 0);
			
			puzzleFlood = new Texture(puzzle.flood);
			puzzleFlood.draw(puzzle.flood,0,0);

			puzzlepos = new Vector2(50,50);
			nowDrawing = false;		
			startflood = false;
			timeaccum = 0;
		}
		initialized = true;
	}
	
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);


		camera.update();

		// Re-drawing the textures from the puzzle
		puzzleDrawing.draw(puzzle.orig, 0, 0);
		puzzleFlood.draw(puzzle.flood,0,0);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			batch.draw(puzzleDrawing, puzzlepos.x, puzzlepos.y);
			//batch.setColor(1, 1, 1, 0.5f);
			batch.draw(puzzleFlood, puzzlepos.x, puzzlepos.y);
		batch.end();
		
		
		// FLOODING
		if (startflood)
		{
			timeaccum += delta;
			while (timeaccum > flooddelta)
			{
				timeaccum -= flooddelta;
				if (puzzle.floodfill(floodsteps))
					startflood = false;
			}
		}
		
		
		
		
		
		// Drawing the local line
		if (dividingLine.size > 0)
		{
			lineDrawer.setProjectionMatrix(camera.combined);
			lineDrawer.begin(ShapeType.Line);
			lineDrawer.setColor(1, 0, 0, 1);

			Vector2 P1, P2 = null;
			Iterator<Vector2> it = dividingLine.iterator();
			
			while (it.hasNext())
			{
				P1 = it.next();
				if (P2 != null)
					lineDrawer.line(P2.x, P2.y, P1.x, P1.y);
				P2 = P1;
			}
			lineDrawer.end();
		}
		
		
	
		// ** FIXME: Separate input from rendering -- LOW PRIORITY
		if(Gdx.input.isTouched()) 
		{
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(touchPos);
			Vector2 newpoint = new Vector2(touchPos.x, touchPos.y);
			
			if (!nowDrawing) // Creating a new Line
			{
				nowDrawing = true;
				dividingLine.clear();
			}
			
			if (dividingLine.size < MAXLINESIZE)
				dividingLine.add(newpoint);			
		}
		else 
		{
			// STOPPED DRAWING
			if (nowDrawing) // stopped drawing - transfer drawing to the puzzle
			{
				puzzle.reset();
				Gdx.app.log("ping","ping");
				puzzle.setLine(dividingLine, puzzlepos);
				puzzle.drawDivLine(Color.BLACK);

				dividingLine.clear();
				nowDrawing = false;
				startflood = true;
			}
		}
		
		
		
	}
	
	
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		init();

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
