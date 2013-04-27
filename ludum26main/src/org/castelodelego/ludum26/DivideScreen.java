package org.castelodelego.ludum26;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

	ludum26entry g; // The overall Game class, used leaving this screen if necessary
	OrthographicCamera camera;

	ShapeRenderer lineDrawer;
	SpriteBatch batch;
	
	Array<Vector2> dividingLine; // This array contains the Line created by the player
	boolean nowDrawing; // Turns to true when the player starts drawing, and false when he stops

	PuzzleImage puzzle;
	Vector2 puzzlepos;
	
	// Image to be drawn
	// Geometric data of this image
	
	public DivideScreen(ludum26entry game)
	{
		g = game;		
		camera = new OrthographicCamera();
		lineDrawer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		init();
	}
	
	public void init()
	{
		camera.setToOrtho(false, 800, 480);
		dividingLine = new Array<Vector2>();

		puzzle = new PuzzleImage(700,380);
		puzzlepos = new Vector2(50,50);
		nowDrawing = false;
	}
	
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);


		camera.update();

		// DRAWING THE TEXTURE -- TEMPORARY
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		   batch.draw(puzzle.getTexture(), puzzlepos.x, puzzlepos.y);
		batch.end();
		
		
		
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
				//lineDrawer.circle(P1.x, P1.y, 4);
				if (P2 != null)
					lineDrawer.line(P2.x, P2.y, P1.x, P1.y);
				P2 = P1;
			}
			lineDrawer.end();
		}
		
		
	
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
				
				// DEBUG FIXME INNEFICIENT
				puzzle.dispose();
				puzzle = new PuzzleImage(700,380);
			}
			
			// FIXME - I must take care with the touch resolution!
			if (dividingLine.size < 300)
				dividingLine.add(newpoint);
			
		}
		else 
		{
			if (nowDrawing) // stopped drawing - transfer drawing to the puzzle
			{
				puzzle.addLine(dividingLine, puzzlepos);
				puzzle.floodfill();
				puzzle.updateTexture();
				dividingLine.clear();
				nowDrawing = false;
			}
		}
		
		
		
	}
	
	
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

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
