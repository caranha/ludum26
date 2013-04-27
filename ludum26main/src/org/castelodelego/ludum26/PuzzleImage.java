package org.castelodelego.ludum26;

import java.util.Iterator;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
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
	
	
	Pixmap puzzle;
	Texture renderable;
	
	int H, W;
	
	// Colors for this puzzle Image
	Color lineColor;
	Color paintColor[]; // Resource 1, before sharing 
	int[] pcount; // Counts for balance;
	
	/**
	 * Default Constructor, uses pre-set images and colors
	 */
	public PuzzleImage(int w, int h)
	{
		H = h;
		W = w;
		pcount = new int[2];
		
		puzzle = new Pixmap(w,h,Format.RGBA8888);
		lineColor = Color.ORANGE;
		paintColor = new Color[2];
		paintColor[0] = Color.BLUE;
		paintColor[1] = Color.CYAN;
		
		puzzle.setColor(Color.GREEN);
		puzzle.fill();
		puzzle.setColor(paintColor[0]);
		puzzle.fillRectangle(50, 50, 500, 125);
		puzzle.fillRectangle(500,170,560,300);
		
		renderable = new Texture(puzzle);
	}
	
	public boolean floodfill()
	{
		
		pcount[0] = 0;
		pcount[1] = 0;
		boolean[][] visited = new boolean[W][H]; // if I have visited some place or not

		Stack<Point> curStack = new Stack<Point>(); // This stack contains the elements that I am painting at this moment;
		Stack<Point> nextStack = new Stack<Point>(); // This stack contains the elements that I will paint next
		
		int[][] dir = {{1,0},{0,1},{-1,0},{0,-1}};
		boolean isPainting = true; //are we painting now?
		
		curStack.push(new Point(0,0)); // adding the initial point
		visited[0][0] = true;
		
		Point cur;
		Point t;

		int lcolor = Color.rgba8888(lineColor);
		int fcolor = Color.rgba8888(paintColor[0]);
		int pcolor = Color.rgba8888(paintColor[1]);
		int curcolor;
		
		while(!curStack.empty() || !nextStack.empty())
		{
			while(!curStack.empty())
			{
				cur = curStack.pop();
				curcolor = puzzle.getPixel(cur.x,cur.y);
				
				// Paint this position if necessary:
				if (curcolor == fcolor)
					if (isPainting)
					{
						pcount[1] += 1;
						puzzle.drawPixel(cur.x,cur.y,pcolor);
					}
					else
					{
						pcount[0] += 1;
					}
							
				
				// Put pixels around this one in the stack
				for (int i = 0; i < 4; i++)
				{
					t = new Point(cur.x + dir[i][0], cur.y+dir[i][1]);
					if ((t.x >= 0 && t.x < W && t.y >=0 && t.y < H) && (!visited[t.x][t.y]))
					{ // valid and not visited point
						visited[t.x][t.y] = true;
						curcolor = puzzle.getPixel(t.x,t.y);
						if (curcolor == lcolor)
							nextStack.push(t);
						else
							curStack.push(t);
					}
				}
				
			}			
			isPainting = !isPainting;
			curStack = nextStack;
			nextStack = new Stack<Point>();
		}
		
		Gdx.app.log("PuzzleScore", "Division: " + ((pcount[0]*1.0)/(pcount[0]+pcount[1])));		
		return true;
	}
	
	
	// Adds a dividing line into this pixmap - the offset is the difference between the line coordinates and the puzzleimage coordinates on the screen
	public void addLine(Array<Vector2> line, Vector2 offset)
	{
		Iterator<Vector2> it = line.iterator();
		Vector2 P1, P2 = null;
		
		
		puzzle.setColor(lineColor);
		while (it.hasNext())
		{
			P1 = it.next().cpy();
			P1.sub(offset);
			
			if (P2 != null)
				puzzle.drawLine(Math.round(P2.x), H - Math.round(P2.y), Math.round(P1.x), H - Math.round(P1.y));
			P2 = P1;
		}
		
		renderable.draw(puzzle, 0, 0);
	}
	

	public Texture updateTexture()
	{
		renderable.draw(puzzle, 0, 0);
		return renderable;
	}
	
	// draw this pixmap
	public Texture getTexture()
	{
		return renderable;
	}
	
	public void dispose()
	{
		puzzle.dispose();
		renderable.dispose();
	}
}
