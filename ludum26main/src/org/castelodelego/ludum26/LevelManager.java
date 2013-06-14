package org.castelodelego.ludum26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LevelManager {
	
	// TODO: Make the levelManager calculate the level list dynamically
	
	// Include all level filenames here, 
	static String[][] levelList = { 
			{ "levels/level0.png", "In the Beginning" },
			{ "levels/level1.png", "A Decision" },
			{ "levels/level2.png", "Perspective" },
			{ "levels/level3.png", "Planets" },
			{ "levels/level5.png", "Settlers" },
			{ "levels/level6.png", "Art" },
			{ "levels/level7.png", "The Commons" },
			{ "levels/level8.png", "The Cave" },
			{ "levels/level9.png", "Festival" },
			{ "levels/level10.png", "Night Forest" },
			{ "levels/level11.png", "Loving You" },
			{ "levels/level12.png", "Party" },
			{ "levels/level4.png", "Snake" },
			{ "levels/level13.png", "Emotions" },
			{ "levels/level14.png", "Challenge" },
			{ "levels/level15.png", "Passion" },
			{ "levels/level16.png", "Strength" },
			{ "levels/level17.png", "Final Revenge" }
	};
	
	
	int totalLevels;
	PuzzleImage[] levels;
	
	// public boolean[] unlocked;
	public int[] score;
	int unlockindex;
	Preferences savedscores;
	
	public LevelManager()
	{
		totalLevels = levelList.length;
		savedscores = Gdx.app.getPreferences("Scores");
		
//		unlocked = new boolean[totalLevels];
//		unlocked[0] = true;
		unlockindex = 0;
		
		levels = new PuzzleImage[totalLevels];
		
		score = new int[totalLevels];
	}
	
	public PuzzleImage getLevel(int n)
	{
		if (levels[n] == null)
			levels[n] = new PuzzleImage(LevelManager.levelList[n][0]);
		return levels[n];
	}
	
	/**
	 * Register a new score for a level and, if appropriate, unlocks new levels. 
	 * Returns whether new levels were unlocked or not.
	 * 
	 * @param level The level for which a score is being submitted.
	 * @param sc 0->D 3->A
	 */
	public boolean setScore(int level, int sc)
	{
		int oldunlock = unlockindex;
		
		if (score[level] == 0 && sc > 0) // new non-zero score
			unlockindex++;
		if (score[level] != 3 && sc == 3) // new "A" score
			unlockindex++;

		if (unlockindex >= totalLevels)
			unlockindex = totalLevels-1; // unlockindex is limited by the level array length
		
		if (score[level] < sc)
			score[level] = sc; // update score if necessary
		
		// update preferences
		savedscores.putInteger("score"+level, score[level]);
		savedscores.putInteger("unlockindex", unlockindex);
		savedscores.flush();
		
		return (oldunlock != unlockindex); // new levels were unlocked
	}
	
	/**
	 * Return what is the next level when this level is completed -- ignores whether this level was successfully 
	 * completed or not, and only checks if the next level is available (checks for level limits, or unluck limits)
	 * 
	 * @param n Level that was just completed
	 * @return Level that should be played next, or -1 if N was the last level.
	 */
	public int getNext(int n)
	{
		if (n == totalLevels-1)
			return -1;
	
		return (Math.min(n+1, unlockindex));		
	}
	
	public boolean perfectScore()
	{
		for (int i = 0; i < totalLevels; i++)
			if (score[i] < 3)
				return false;
		return true;
	}
	
	/**
	 * Return the total number of levels unlocked;
	 * @return
	 */
	public int getTotalUnlocked()
	{
		return unlockindex+1;			
	}
	
	public void loadLevelData()
	{
		for (int i = 0; i < totalLevels; i++)
		{
			unlockindex = savedscores.getInteger("unlockindex",0);
			score[i] = savedscores.getInteger("score"+i, 0);
		}
	}
	
	public void resetLevelData()
	{
		unlockindex = 0;
		score = new int[totalLevels];
		savedscores.clear();
	}
	
	public void dispose()
	{
		for (int i = 0; i < totalLevels; i++)
			if (levels[i] != null)
				levels[i].dispose();
	}
	
	/**
	 * Returns true if the level l is unlocked. False if it is not unlocked.
	 * @param l
	 * @return
	 */
	public boolean isLevelUnlocked(int l)
	{
		return (l <= unlockindex);
	}
	
}
