package org.castelodelego.ludum26;

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

/***
 * Main game class. In this screen, the player is presented with a puzzle (an image), 
 * and has to divide this image with a single touch.
 * 
 * @author caranha
 */

public class DivideScreen implements Screen {
		
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
	
	LineInput divline;
	
	
	boolean nowDrawing; // Turns to true when the player starts drawing, and false when he stops

	PuzzleImage puzzle;
	Vector2 puzzlepos;
	Texture puzzleDrawing;
	Texture puzzleFlood;
	private float timeaccum; // used to calculate flooding time
	
	boolean newlevels; // new levels were unlocked!
	
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
		
		divline = new LineInput();
		
		rawtouch = new Vector3();
		touchpos = new Vector2();
		
		lineDrawer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		initialized = false;
		curLevel = 0;
		
		newlevels = false;
	}
	
	public void init()
	{
		if (!initialized)
		{
			Gdx.app.log("DivideScreen","initialized");
			curState = STATE_FADEIN;
			
			divline.clear();
			
			returnButton = new BitmapFontCache(ludum26entry.manager.get("sawasdee32.fnt", BitmapFont.class),true);
			titleText = new BitmapFontCache(ludum26entry.manager.get("sawasdee32.fnt", BitmapFont.class),true);
			scoreFont = ludum26entry.manager.get("sawasdee32.fnt", BitmapFont.class);
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
			newlevels = false;
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
		
		divline.clear();
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
		
		// Drawing the line
		divline.drawCurrent(camera);

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
			//Black magic!
			rawtouch.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(rawtouch);
			touchpos.set(rawtouch.x, rawtouch.y);
			wastouched = true;
		}
			
		if (wastouched && curState != STATE_FADEIN && curState != STATE_FADEOUT)
		{
			// THIS ALWAYS HAPPENS except while drawing
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
						divline.clear();
						//drawingsnd.loop(0.2f); FIXME: Find a better drawingsnd sound
					}
					divline.addInput(new Vector2(touchpos)); //TODO: Maybe deal with the line being impossible to draw?
					break;
				case STATE_WAIT:
					leaveScreen = true;
					break;
				}
			}
		}
		
		if (curState==STATE_DRAW && !wastouched && nowDrawing) // Testing for finish dragging the line
		{
			puzzle.reset(); // TODO -- this shouldn't be needed, if we do it once per screen call
			Gdx.app.log("input:play","stopped drawing");

			divline.finish();
			if (divline.isValid())
			{
				puzzle.setLine(divline.getDivLine(), puzzlepos);
				puzzle.drawDivLine(Color.BLACK);
				startflood = true; // SIGNAL TO CHANGE THE STATE TO STATE_CALCULATE
			}

			divline.clear();
			nowDrawing = false;
			// drawingsnd.stop();

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
				boolean done = false;
				timeaccum -= flooddelta;
				if (puzzle.floodfill(floodsteps) && !done)
				{
					done = true;
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
		newlevels = g.lmanager.setScore(curLevel, puzzle.score);
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

		String scoretext = "";

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// Success Text

		switch(puzzle.score)
		{
		case 0:
			scoretext = "Rank D: Try again...";
			break;
		case 1:
			scoretext = "Rank C: Success!";
			break;
		case 2:
			scoretext = "Rank B: Success!";
			break;
		case 3:
			scoretext = "Rank A: Success!";
			break;
		}

		if (puzzle.score == 0)
			textFont.setColor(Color.RED);
		else
			textFont.setColor(Color.GREEN);
			
		textFont.drawMultiLine(batch, scoretext, 400, 380, 0, BitmapFont.HAlignment.CENTER);
		if (newlevels) 
			textFont.drawMultiLine(batch, "New Level Unlocked!", 400, 150, 0, BitmapFont.HAlignment.CENTER);
		textFont.drawMultiLine(batch, "Score by color:", 400, 330, 0, BitmapFont.HAlignment.CENTER);
		
		textFont.setColor(Color.BLACK);
		textFont.drawMultiLine(batch, scoretext, 400+1, 380, 0, BitmapFont.HAlignment.CENTER);
		if (newlevels) 
			textFont.drawMultiLine(batch, "New Level Unlocked!", 400+1, 150, 0, BitmapFont.HAlignment.CENTER);
		textFont.drawMultiLine(batch, "Score by color:", 400+1, 330, 0, BitmapFont.HAlignment.CENTER);
		

		
		batch.end();
		
		// Score per color
		int len = puzzle.scorecolor.length;
		Color blockColor = new Color();
		lineDrawer.begin(ShapeType.FilledRectangle);

		// Total width of color bars: 80*len - 10
		// Middle of the screen: 400
		// X coordinate of the color bars: 400 - (80*len-10)/2
		int blockx = 400 - (80*len-10)/2;
		int blocky = 170;
		
		
		for (int i = 0; i < len; i++)
		{
			Color.rgba8888ToColor(blockColor, puzzle.colortable[i]);
			drawColorBlock(blockx,blocky,blockColor,puzzle.scorecolor[i]);
			blockx += 80;
		}
		lineDrawer.end();
		
	}
	
	void drawColorBlock(float posx, float posy, Color c, float score)
	{
		
		int w = 70;
		int h = 100;
		lineDrawer.setColor(Color.BLACK);
		lineDrawer.filledRect(posx, posy, w+1, h);
		lineDrawer.setColor(c);
		lineDrawer.filledRect(posx+3, posy+3, (w-6)/2, h-6);
		
		/* normally the score is 0 for blue, and 1 for red. However, we want to do some
		 * Vertical exaggeration. So any score below 0.3 is 0.05 for blue, and any score above
		 * 0.7 is 0.95 for red.
		 */
		
		float adjscore = score;
		if (adjscore < 0.2)
			adjscore = 0.05f;
		else if (adjscore > 0.8)
			adjscore = 0.95f;
		else
			adjscore = (adjscore - 0.2f)*(0.9f/0.6f) + 0.05f;
		
		
		lineDrawer.setColor(Color.RED);
		lineDrawer.filledRect(posx+(w/2)+3, posy+3, w/2-5, (h-6)*adjscore);
		lineDrawer.setColor(Color.BLUE);
		lineDrawer.filledRect(posx+(w/2)+2.3f, posy+4+(h-6)*adjscore, w/2-5f, (h-7)*(1-adjscore));
		lineDrawer.setColor(Color.GRAY);
		lineDrawer.filledRect(posx+(w/2)+3, posy+h/2-1, w/2-5f, 2);
		
		//System.out.println(adjscore);
		
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
