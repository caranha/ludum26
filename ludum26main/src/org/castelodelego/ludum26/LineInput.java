package org.castelodelego.ludum26;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * This class holds the data for a dividing line, as it gets information from the touch input. 
 * It draws the line in progress, as well as test for line completness.
 * 
 * @author caranha
 *
 */
public class LineInput {
	
	static double bordersnap = 5; //How close the line has to be to the border before it will snap into it.
	static double segmentdelta = 2; // what is the minimum distance that characterizes a new segment?
	static double angledelta = 2; // if the angle difference in degrees between two segments is smaller than this, then the segments will be merged

	public Array<Vector2> dividingLine;
	public Vector2[] borders;
	
	ShapeRenderer lineDrawer;
	boolean valid;
	int bordercross = 0;
	
	public LineInput()
	{
		dividingLine = new Array<Vector2>();
		lineDrawer = new ShapeRenderer();
		valid = false;
		bordercross = 0;
		borders = new Vector2[4];
		// TODO Give this assignment a parameter
		borders[0] = new Vector2(50,50);
		borders[1] = new Vector2(50,430);
		borders[2] = new Vector2(750,430);
		borders[3] = new Vector2(750,50);
	}
	
	/**
	 * Clears all the data in this line
	 */
	public void clear()
	{
		dividingLine.clear();
		valid = false;
		bordercross = 0;
	}
	
	/**
	 * Returns the number of segments that the line has
	 * @return
	 */
	public int getSize()
	{
		return dividingLine.size;
	}
	
	public Array<Vector2> getDivLine()
	{
		return dividingLine;
	}
	
	/**
	 * Draws the line as it stands now
	 */
	public void drawCurrent(OrthographicCamera c)
	{
		lineDrawer.setProjectionMatrix(c.combined);
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
	
	/**
	 * Tests whether the line connects to at least two borders, or whether it has an internal circle.
	 * @return
	 */
	public boolean isValid()
	{
		return valid;
	}
	
	public void addInput(Vector2 input)
	{
		if (dividingLine.size > 0 && input.epsilonEquals(dividingLine.peek(), 2f))
			return; // 
		Vector2 prein = null;
		
		if (dividingLine.size > 0) 
			prein = dividingLine.peek();
		
		dividingLine.add(input);		
		// TODO: Eventually make this more efficient by removing more "dupe" vectors

		
		// No point testing this if this is already valid
		if (!valid && dividingLine.size > 1)
		{
			//FIXME: Not perfect - if the border is crossed "slowly" (how slowly?) one "segment" will cross the border "twice".
			//But it seems "good enough" for now - needs playtesting
			
			Vector2 tmp = new Vector2();
			
			// Test for crossing the border
			for (int i = 0; i < 4; i++)
				if (Intersector.intersectSegments(prein, input, borders[i], borders[(i+1)%4], tmp))
				{
					bordercross ++;
					valid = (bordercross > 1);
				}

			// Test for self-crossing			
			for (int i = 1; i < dividingLine.size - 2; i++)
				if (Intersector.intersectSegments(prein, input, dividingLine.get(i-1), dividingLine.get(i), tmp))
				{
					valid = true;
				}
		}
	}
	
	public void finish()
	{
		// calculate distance between first and last points and the borders
		Vector2 first = dividingLine.first();
		Vector2 last = dividingLine.peek();
		float maxdist = 15;
		
		int closefirst;
		int closelast;
		for (int i = 0; i < 4; i++)
		{
			Intersector.distanceLinePoint(borders[i], borders[(i+1)%4], first);
			Intersector.pointLineSide(borders[i], borders[(i+1)%4], first);// negative for inside
	
			
//			intersectLines(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 intersection)
//			Intersects the two lines and returns the intersection point in intersection.
			
			Intersector.distanceLinePoint(borders[i], borders[(i+1)%4], last);
			Intersector.pointLineSide(borders[i], borders[(i+1)%4], last); // negative for inside
		}
		
		// TODO: Write this function
		// Indicates that the line is over - needs to test if the borders of the line can be snapped.
	}
	
}
