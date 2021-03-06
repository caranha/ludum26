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
	
	static float bordersnap = 20; //How close the line has to be to the border before it will snap into it.
	static float segmentdelta = 2; // what is the minimum distance that characterizes a new segment?

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
		if (dividingLine.size > 0 && input.epsilonEquals(dividingLine.peek(), segmentdelta))
			return;
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
		
		
		//FIXME: is the value for bordersnap correct?
		int closefirst = -1;
		float firstdist = bordersnap;
		
		int closelast = -1;
		float lastdist = bordersnap;
		for (int i = 0; i < 4; i++)
		{
			if ((Intersector.pointLineSide(borders[i], borders[(i+1)%4], first) == -1) &&
				(Intersector.distanceLinePoint(borders[i], borders[(i+1)%4], first) < firstdist))
			{
				firstdist = Intersector.distanceLinePoint(borders[i], borders[(i+1)%4], first);
				closefirst = i;
			}
			
			if ((Intersector.pointLineSide(borders[i], borders[(i+1)%4], last) == -1) &&
					(Intersector.distanceLinePoint(borders[i], borders[(i+1)%4], last) < lastdist))
				{
					lastdist = Intersector.distanceLinePoint(borders[i], borders[(i+1)%4], last);
					closelast = i;
				}
		}
			
		if (closefirst != -1)
		{
			Vector2 first2 = dividingLine.get(1);
			Vector2 tmp = new Vector2();
			Intersector.intersectLines(first, first2, borders[closefirst], borders[(closefirst+1)%4], tmp);
			dividingLine.insert(0, tmp);
			bordercross++;
		}
		
		if (closelast != -1)
		{
			Vector2 last2 = dividingLine.get(dividingLine.size-2);
			Vector2 tmp = new Vector2();
			Intersector.intersectLines(last, last2, borders[closelast], borders[(closelast+1)%4], tmp);
			dividingLine.add(tmp);
			bordercross++;
		}
		
		if (bordercross > 1) // test if the new vectors made the line valid
			valid = true;
			
	}
	
}
