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
	
	static int[][] dir = {{1,0},{0,1},{-1,0},{0,-1}};	

	Pixmap flood;
	Pixmap orig;

	
	int H, W;
	
	// Colors for this puzzle Image
	static int lineColor = Color.rgba8888(0f,0f,0f,1f); // black

	static int RedColor = Color.rgba8888(1f,0f,0f,0.7f); // red
	static int BlueColor = Color.rgba8888(0f,0f,1f,0.7f); // blue
	boolean paintingRed; // which colors we are painting now
	
	int colortable[];
	int colorcount[][];
	
	Array<Point> divLine;

	boolean[][] visited;
	Stack<Point> currStack;
	Stack<Point> nextStack;
	
	/**
	 * Default Constructor, uses pre-set images and colors
	 */
	public PuzzleImage(String name, ludum26entry g)
	{
		orig = g.manager.get(name, Pixmap.class);
				
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
		
		for (int i = 0; i < ncolor; i++) // setting the relevant colors
		{
			colortable[i] = orig.getPixel(i, 0);
			orig.drawPixel(i, 0,Color.rgba8888(1, 1, 1, 1));
		}
		
		currStack = new Stack<Point>();
		nextStack = new Stack<Point>();
		
		reset();		
	}
	
	// Resets the puzzle to be played again;
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
		
		for (int i = 0; i < colortable.length; i++)
			Gdx.app.log("PuzzleScore", "Color "+i+": " + ((colorcount[i][0]*1.0)/(colorcount[i][0]+colorcount[i][1])));		

		return true;
	}
	
	
	// Sets the Div Line
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

	// Draws the Div Line in a certain color
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
