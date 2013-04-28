package org.castelodelego.ludum26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LevelManager {
	
	// TODO: Make the levelManager calculate the level list dynamically
	
	// Include all level filenames here, 
	static String[][] levelList = { 
			{ "levels/level0.png", "In the Beginning" },
			{ "levels/testlevel.png", "Prototyping" }			
	};
	
	
	int totalLevels;
	PuzzleImage[] levels;
	
	public boolean[] unlocked;
	public int[] score;
	Preferences savedscores;
	
	public LevelManager()
	{
		totalLevels = levelList.length;
		savedscores = Gdx.app.getPreferences("Scores");
		
		unlocked = new boolean[totalLevels];
		unlocked[0] = true;
		
		levels = new PuzzleImage[totalLevels];
		
		score = new int[totalLevels];
	}
	
	public PuzzleImage getLevel(int n)
	{
		if (levels[n] == null)
			levels[n] = new PuzzleImage(LevelManager.levelList[n][0]);
		return levels[n];
	}
	
	public void setScore(int level, int sc)
	{
		if (score[level] < sc)
		{
			score[level] = sc;
			
			if (level+1 < totalLevels)
			unlocked[level+1] = true;
		}		
	}
	// TODO: make later levels only unlock if you have a number of A's
	// But to do this, I must make it clear somewhere that this is the case 
	// ("A's necessary for this level: X)
	
	public int getNext(int n)
	{
		if (n == totalLevels-1)
			return -1;
		
		if (unlocked[n+1])
			return n+1;
		else 
			return n;
	}
	
	public void saveLevelData()
	{
		for (int i = 0; i < totalLevels; i++)
		{
			savedscores.putBoolean("unlocked"+i, unlocked[i]);
			savedscores.putInteger("score"+i, score[i]);
		}
	}
	
	public void loadLevelData()
	{
		for (int i = 0; i < totalLevels; i++)
		{
			unlocked[i] = savedscores.getBoolean("unlocked"+i, false);
			score[i] = savedscores.getInteger("score"+i, 0);
		}
		unlocked[0] = true;
	}
	
	public void resetLevelData()
	{
		unlocked = new boolean[totalLevels];
		unlocked[0] = true;		
		score = new int[totalLevels];
		savedscores.clear();
	}
	
	public void dispose()
	{
		for (int i = 0; i < totalLevels; i++)
			if (levels[i] != null)
				levels[i].dispose();
	}
}
