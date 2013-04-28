package org.castelodelego.ludum26;

import java.util.Iterator;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
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
	//float timeaccum;
	int GL10offset;
	
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
	private float timeaccum; // used to calculate flooding time
	
	BitmapFontCache returnButton;
	BitmapFontCache titleText;
	BitmapFont scoreFont;
	BitmapFont textFont;
	
	Rectangle returnButtonBox;

	Vector3 rawtouch; // sexy!
	Vector2 touchpos;
	boolean wastouched;
	
	private final static int STATE_FADEIN = 0;
	private final static int STATE_DRAW = 1;
	private final static int STATE_CALCULATE = 2;
	private final static int STATE_WAIT = 3;
	private final static int STATE_FADEOUT = 4;
	
	int curLevel;
	private int curState;
	
	private boolean leaveScreen;
	private Screen nextScreen;
	
	Sound fillingsnd;
	Sound drawingsnd;
	
	float fadestate; // used to calculate fading time
	static float FADETIME = 0.2f;
	
	float scoretimer; // timer for the alpha blend of the score images
	static float SCORETIME = 0.2f;
	
	public DivideScreen(ludum26entry game)
	{
		g = game;		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		dividingLine = new Array<Vector2>();
		rawtouch = new Vector3();
		touchpos = new Vector2();
		
		lineDrawer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		initialized = false;
	}
	
	public void init()
	{
		if (!initialized)
		{
			Gdx.app.log("DivideScreen","initialized");
			curState = STATE_FADEIN;
			curLevel = 0;
			

			
			dividingLine.clear();
			
			returnButton = new BitmapFontCache(ludum26entry.manager.get("sawasdee.fnt", BitmapFont.class),true);
			titleText = new BitmapFontCache(ludum26entry.manager.get("sawasdee.fnt", BitmapFont.class),true);
			scoreFont = ludum26entry.manager.get("sawasdee.fnt", BitmapFont.class);
			textFont = ludum26entry.manager.get("Beaulieux.fnt", BitmapFont.class);
			
			fillingsnd = ludum26entry.manager.get("filling.ogg", Sound.class);
			drawingsnd = ludum26entry.manager.get("drawing.ogg", Sound.class);
			
			
			// FIXME: Put the position of the return button somewhere saner;
			float xpos = 400-(197.0f/2);
			float ypos = 40;
			
			returnButton.addText("Return to Title", xpos, ypos); // This is ugly
			returnButton.setColor(Color.DARK_GRAY);
			//returnButtonBox = new Rectangle(returnButton.getX(),returnButton.getY(),returnButton.getBounds().width,returnButton.getBounds().height);	
			returnButtonBox = new Rectangle(xpos,ypos-returnButton.getBounds().height,returnButton.getBounds().width,returnButton.getBounds().height);
			// this is even uglier - how do I find out the position of a BitmapFontCache on the scren?
			
		
			puzzle = g.lmanager.getLevel(curLevel);
			
			if (Gdx.graphics.isGL20Available())
			{
				puzzleDrawing = new Texture(puzzle.orig);
				puzzleFlood = new Texture(puzzle.flood);
				GL10offset = 0;
			}
			else
			{
				puzzleDrawing = new Texture(1024,512,Format.RGBA8888);
				puzzleFlood = new Texture(1024,512,Format.RGBA8888);
				GL10offset = 132;
			}

			puzzlepos = new Vector2(50,50);
			nowDrawing = false;		
			startflood = false;			
		}
		initialized = true;
	}
	
	public void reset()
	{
		puzzle = g.lmanager.getLevel(curLevel);
		puzzle.reset();
	
		titleText.setText(LevelManager.levelList[curLevel][1], 400-(scoreFont.getBounds(LevelManager.levelList[curLevel][1]).width/2), 470); // This is ugly
		
		curState = STATE_FADEIN;
		timeaccum = 0;
		fadestate = 0;
		scoretimer = 0;
		
		dividingLine.clear();
		wastouched = false;

		leaveScreen = false;
		nextScreen = g.play;
		
		puzzleDrawing.draw(puzzle.orig, 0, GL10offset);
	}
	
	
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		//camera.update();

		/*** DRAWING ***/
		
		// Drawing the puzzle Textures
		// puzzleDrawing.draw(puzzle.orig, 0, 0); // IF PUZZLE DOES NOT UPDATE, TRY UNCOMMENTING THIS
		puzzleFlood.draw(puzzle.flood,0,GL10offset);
		
		//
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			//batch.disableBlending();
			batch.draw(puzzleDrawing, puzzlepos.x, puzzlepos.y);
			//batch.enableBlending();
			batch.draw(puzzleFlood, puzzlepos.x, puzzlepos.y);
			returnButton.draw(batch);
			titleText.setColor(Color.DARK_GRAY);
			titleText.draw(batch);
		batch.end();
		
		// Drawing Guide // TODO: REPLACE THIS WITH SOMETHING BETTER
		lineDrawer.setProjectionMatrix(camera.combined);
		lineDrawer.begin(ShapeType.Rectangle);
		lineDrawer.setColor(Color.DARK_GRAY);
		lineDrawer.rect(puzzlepos.x, puzzlepos.y, 700, 380);
		lineDrawer.end();
		
		// Drawing INPUT LINE
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

		// Score drawing
		if (scoretimer > 0)
			drawScore();
		
		// General Fade-in/out
		if (curState == STATE_FADEIN || curState == STATE_FADEOUT) 
		{
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			lineDrawer.setProjectionMatrix(camera.combined);
			lineDrawer.begin(ShapeType.FilledRectangle);
			lineDrawer.setColor(1f, 1f, 1f, 1 - (fadestate/FADETIME));
			lineDrawer.filledRect(0, 0, 800, 480);		
			lineDrawer.end();
		}
		
		/*** INPUT BLOCK ***/
				
		if(wastouched = Gdx.input.isTouched()) // Cnam style!
		{			
			rawtouch.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(rawtouch);
			touchpos.set(rawtouch.x, rawtouch.y);
			wastouched = true;
		}
			
		if (wastouched && curState != STATE_FADEIN && curState != STATE_FADEOUT)
		{
			// THIS ALWAYS HAPPENS -- except when dr
			if (returnButtonBox.contains(touchpos.x, touchpos.y) && !nowDrawing)
			{
				Gdx.app.debug("touchtest", "Touched Return Button");
				leaveScreen = true;
				nextScreen = g.title;
			}
			else // if touch anywhere other than the leave button
			{
				switch(curState)
				{				
				case STATE_DRAW:
					if (!nowDrawing) // Creating a new Line
					{
						nowDrawing = true;
						dividingLine.clear();
						drawingsnd.loop(0.2f);
					}
					if (dividingLine.size < MAXLINESIZE)
						dividingLine.add(new Vector2(touchpos));					
					break;
				case STATE_WAIT:
					// FIXME: Remember to set up the next level at the UPDATE
					leaveScreen = true;
					// nextScreen = g.play; -- possibly not needed
					break;
				}
			}
		}
		
		if (curState==STATE_DRAW && !wastouched && nowDrawing) // Testing for finish dragging the line
		{
			puzzle.reset(); // TODO -- this shouldn't be needed, if we do it once per screen call
			Gdx.app.log("input:play","stopped drawing");
			
			// This probably should go to UPDATE
			puzzle.setLine(dividingLine, puzzlepos);
			puzzle.drawDivLine(Color.BLACK);
			dividingLine.clear();

			nowDrawing = false;
			drawingsnd.stop();
			startflood = true; // SIGNAL TO CHANGE THE STATE TO STATE_CALCULATE
		}
		


		/*** UPDATE BLOCK ***/

		if (leaveScreen) // This Always happens
		{
			curState = STATE_FADEOUT;
			fillingsnd.stop();
			drawingsnd.stop();
			leaveScreen = false;
		}
		
		switch(curState)
		{
		case STATE_FADEIN:			
			fadestate += delta;
			if (fadestate/FADETIME >= 1)
				curState = STATE_DRAW;
			break;
			
		case STATE_FADEOUT:
			fadestate -= delta;
			if (fadestate/FADETIME <= 0)
				g.setScreen(nextScreen);
			break;

		case STATE_DRAW:
			if (startflood)
			{
				curState = STATE_CALCULATE;
				startflood = false;
				
				long t = fillingsnd.loop(0.2f);
				fillingsnd.setPitch(t, 0.5f);
			}
			break;
			
			
		case STATE_WAIT:
			if (scoretimer/SCORETIME < 1)
				scoretimer += delta;
			break;
			
		case STATE_CALCULATE:
			timeaccum += delta;
			while (timeaccum > flooddelta)
			{
				timeaccum -= flooddelta;
				if (puzzle.floodfill(floodsteps))
				{
					curState = STATE_WAIT;
					fillingsnd.stop();
					puzzle.setScore();
					setScoreScene();					
				}
			}
			break;		
		}
	}
	

	/**
	 * Set up things nescessary for the score scene -- texts, and whether to advance levels or not;
	 */
	public void setScoreScene()
	{
		nextScreen = g.play;
		g.lmanager.setScore(curLevel, puzzle.score);
		if (puzzle.score > 0)
		{
			curLevel = g.lmanager.getNext(curLevel);
			Gdx.app.log("setScoreScene", "lmanager suggests next level to be: "+curLevel);
			if (curLevel == -1)
			{
				curLevel = 0;
				nextScreen = g.title;
			}
		}
	}
	
	public void drawScore()
	{
		// fading the main screen
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		lineDrawer.setProjectionMatrix(camera.combined);
		lineDrawer.begin(ShapeType.FilledRectangle);
		lineDrawer.setColor(1f, 1f, 1f, ((scoretimer/SCORETIME)/3));
		lineDrawer.filledRect(50, 50, 700, 380);		
		lineDrawer.end();
		
		// drawing the circles

		int len = puzzle.scorecolor.length;
		int circletop = 260 - (len-1)*22;
		Color circledraw = new Color();
		String scoretext = "";
		
		lineDrawer.begin(ShapeType.FilledCircle);
		for (int i = 0; i < len; i++)
		{
			lineDrawer.setColor(Color.BLACK);
			lineDrawer.filledCircle(350, circletop + i*44, 20);
			Color.rgba8888ToColor(circledraw, puzzle.colortable[i]);
			lineDrawer.setColor(circledraw);
			lineDrawer.filledCircle(350, circletop + i*44, 16);
		}		
		lineDrawer.end();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		textFont.setColor(Color.BLACK);
		textFont.drawMultiLine(batch, "Score:", 400, circletop + len*44 + 20, 0, BitmapFont.HAlignment.CENTER);
		
		switch(puzzle.score)
		{
		case 0:
			scoretext = "Rank D\nTry again...";
			break;
		case 1:
			scoretext = "Rank C\nSuccess!";
			break;
		case 2:
			scoretext = "Rank B\nSuccess!";
			break;
		case 3:
			scoretext = "Rank A\nSuccess!";
			break;
		}

		if (puzzle.score == 0)
			textFont.setColor(Color.RED);
		else
			textFont.setColor(Color.GREEN);
			
		textFont.drawMultiLine(batch, scoretext, 400, circletop - 40, 0, BitmapFont.HAlignment.CENTER);
		textFont.setColor(Color.BLACK);
		textFont.drawMultiLine(batch, scoretext, 400+1, circletop - 41, 0, BitmapFont.HAlignment.CENTER);
		
		for (int i = 0; i < len; i++)
		{
			if (puzzle.scorecolor[i] < 0.45)
				scoreFont.setColor(Color.BLUE);
			else if (puzzle.scorecolor[i] > 0.55)
				scoreFont.setColor(Color.RED);
			else
				scoreFont.setColor(Color.BLACK);
			
			scoretext = (Math.round(puzzle.scorecolor[i]*10000)/100f)+"%";
			scoreFont.draw(batch, scoretext, 380, circletop + i*44 + (scoreFont.getCapHeight()/2));
		}
		batch.end();
		
	}
	

	@Override
	public void resize(int width, int height) {
		// TODO CHANGE RESIZE TO HOLD THE SCREEN SIZE -- PROBABLY CHANGE THIS IN "GAME"
	}

	@Override
	public void show() {
		init();
		reset();
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
	/**
	 * When resuming on Android, the game returns to the title screen
	 */
	public void resume() {
		g.setScreen(g.title);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
