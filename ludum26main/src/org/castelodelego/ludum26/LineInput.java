package org.castelodelego.ludum26;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
	ShapeRenderer lineDrawer;
	
	public LineInput()
	{
		dividingLine = new Array<Vector2>();
	}
	
	/**
	 * Clears all the data in this line
	 */
	public void clear()
	{
		dividingLine.clear();
	}
	
	/**
	 * Returns the number of segments that the line has
	 * @return
	 */
	public int getSize()
	{
		return dividingLine.size;
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
		// WRONG!
		// Every time I add a new segment, I test it against all others. - this costs N
		// Every time I add a new segment, I test it against crossing the border (keep an in/out flag)
		// I don't want to do all this calculation here.
		
		//TODO: Empty function
		return true;
	}
	
	public void addInput(Vector2 input)
	{
		// see the distance between the current "drawing point" and the head of the array.
		
		// if the distance is above the segmentdelta, add a new line.
		// tests the new line for an internal loop (intersector class)

		// tests the new line for crossing the border (Just test the two points for "inside border" and "ouside border"
		// constructor of the class must include size of the "play area" (or take this from the game environment
		
		
		// input becomes the new "drawing point"
	}
	
	public void finish()
	{
		// TODO: Write this function
		// Indicates that the line is over - needs to test if the borders of the line can be snapped.
	}
	
}
