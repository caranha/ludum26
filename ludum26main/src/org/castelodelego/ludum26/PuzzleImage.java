package org.castelodelego.ludum26;

import java.util.Iterator;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


/***
 * This class contains and process a single Puzzle Image
 * @author caranha
 *
 * The "DivideScreen" sends 
 *
 */
public class PuzzleImage {
	
	private static int[][] dir = {{1,0},{0,1},{-1,0},{0,-1}};	

	Pixmap flood;
	Pixmap orig;
	
	int H, W;
	
	// Colors for this puzzle Image
	static int lineColor = Color.rgba8888(0f,0f,0f,1f); // black
	static int RedColor = Color.rgba8888(1f,0f,0f,0.7f); // red
	static int BlueColor = Color.rgba8888(0f,0f,1f,0.7f); // blue
	private boolean paintingRed; // which colors we are painting now
	
	public int colortable[];
	int colorcount[][];
	
	public float scorecolor[];
	public float redbluescore;
	public int score;
	
	private Array<Point> divLine;
	private boolean[][] visited;
	private Stack<Point> currStack;
	private Stack<Point> nextStack;
	
	/**
	 * Default Constructor, uses pre-set images and colors
	 */
	public PuzzleImage(String name)
	{
		orig = ludum26entry.manager.get(name, Pixmap.class); // static call!
				
		W = orig.getWidth();
		H = orig.getHeight();
		
		flood = new Pixmap(W,H,Pixmap.Format.RGBA8888);
		divLine = new Array<Point>();

		// finding the number of colors
		int ncolor = 0;
		while (orig.getPixel(ncolor, 0)!=Color.rgba8888(1, 1, 1, 1))
			ncolor++;
		
		colortable = new int[ncolor];
		colorcount = new int[ncolor][2];
		scorecolor = new float[ncolor];
		
		for (int i = 0; i < ncolor; i++) // setting the relevant colors
		{
			colortable[i] = orig.getPixel(i, 0);
			orig.drawPixel(i, 0,Color.rgba8888(1, 1, 1, 1));
		}
		
		currStack = new Stack<Point>();
		nextStack = new Stack<Point>();
		
		reset();		
	}
	
	/** 
	 * Resets the puzzle to be played again;
	 */
	public void reset()
	{
		flood.setColor(0, 0, 0, 0);
		flood.fill();
				
		colorcount = new int[colorcount.length][2];
		visited = new boolean[W][H];

		// resetting flooding algorithm
		currStack.clear();
		nextStack.clear();
		currStack.push(new Point(0,0)); // adding the initial point
		visited[0][0] = true;
		paintingRed = true;
		
		scorecolor = new float[scorecolor.length];
		redbluescore = 0.5f;
		score = 0;
	}
	
	/**
	 * Performs floodfilling for "int" steps
	 * Returns true if flood filling has finished, or false if it has not.
	 * @param steps
	 * @return
	 */
	public boolean floodfill(int steps)
	{
		Point cur;
		Point t;
		int curcolor;
	
		int stepcount = 0;
		
		while(!currStack.empty() || !nextStack.empty())
		{
			while(!currStack.empty())
			{
				cur = currStack.pop();
				curcolor = orig.getPixel(cur.x,cur.y);
				
				// Paint this position if necessary:
				for (int i = 0; i < colortable.length; i++)
					if (curcolor == colortable[i])
						if (paintingRed)
						{
							colorcount[i][0] += 1;
							flood.drawPixel(cur.x, cur.y,RedColor);
						}
						else
						{
							colorcount[i][1] += 1;
							flood.drawPixel(cur.x, cur.y,BlueColor);
						}
															
				// Put pixels around this one in the stack
				for (int i = 0; i < 4; i++)
				{
					t = new Point(cur.x + dir[i][0], cur.y+dir[i][1]);
					if ((t.x >= 0 && t.x < W && t.y >=0 && t.y < H) && (!visited[t.x][t.y]))
					{ // valid and not visited point
						visited[t.x][t.y] = true;
						if (flood.getPixel(t.x,t.y) == lineColor) // on the line, reserve for the next Stack
							nextStack.push(t);
						else // not on the line
							currStack.push(t);
					}
				}
				
				stepcount += 1;
				if (stepcount >= steps)
					return false; // ran out of steps
			}			
			paintingRed = !paintingRed;
			currStack = nextStack;
			nextStack = new Stack<Point>();
		}
		return true;
	}
	
	/**
	 * Sets three public values:
	 * Score (0-3)
	 * redbluescore (who is bigger between red and blue)
	 * scorecolor (score per color)
	 */
	public void setScore()
	{
		if (colorcount[0][0] == 0 && colorcount[0][1] == 0)
			return;

		float maxunbalance = 0;
		int unbalanceindex = 0;
		int currentgrade = 3;

		float[] divisions = new float[colorcount.length];
		for (int i = 0; i < divisions.length; i++)
		{
			divisions[i] = (colorcount[i][0]*1f)/(colorcount[i][0]+colorcount[i][1]);
			float unbalance = (float) Math.abs(divisions[i]-0.5);
			Gdx.app.log("Score", "Score Color " +i + ": " + divisions[i]);
			
			scorecolor[i] = divisions[i];
			if (unbalance > maxunbalance)
			{
				maxunbalance = unbalance;
				unbalanceindex = i;
			}
			
			if (currentgrade > 2 && unbalance > 0.01)
				currentgrade = 2;
			if (currentgrade > 1 && unbalance > 0.025)
				currentgrade = 1;
			if (currentgrade > 0 && unbalance > 0.05)
				currentgrade = 0;
		}
		redbluescore = divisions[unbalanceindex];
		score = currentgrade;
		Gdx.app.log("Score", "Total Score: " + score +", Max unbalance: "+ redbluescore);
	}
	
	/**
	 *  Sets the Div Line
	 * @param line
	 * @param offset
	 */
	public void setLine(Array<Vector2> line, Vector2 offset)
	{
		Iterator<Vector2> it = line.iterator();
		Vector2 tmp;
		divLine.clear();

		while (it.hasNext())
		{
			tmp = it.next().cpy();
			tmp.sub(offset);
			divLine.add(new Point(Math.round(tmp.x), H - Math.round(tmp.y)));
		}
	}

	/**
	 *  Draws the Div Line in a certain color
	 * @param c
	 */
	public void drawDivLine(Color c)
	{
		Point P1,P2 = null;
		Iterator<Point> it = divLine.iterator();

		 flood.setColor(c);
		while (it.hasNext())
		{
			P1 = it.next();
			if (P2 != null)
				flood.drawLine(P2.x, P2.y, P1.x, P1.y);
			P2 = P1;
		}
	}
	
	public void dispose()
	{
		flood.dispose();
		orig.dispose();
	}
}
