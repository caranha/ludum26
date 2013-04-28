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
			{ "levels/level4.png", "Snake" },
			{ "levels/level5.png", "Settlers" },
			{ "levels/level6.png", "Art" },
			{ "levels/level7.png", "The Commons" },
			{ "levels/level8.png", "The Cave" },
			{ "levels/level9.png", "Festival" },
			{ "levels/level10.png", "Night Forest" },
			{ "levels/level11.png", "Loving You" },
			{ "levels/level12.png", "Party" },
			{ "levels/level13.png", "Emotions" },
			{ "levels/level14.png", "Challenge" },
			{ "levels/level14.png", "Strength" },
			{ "levels/level14.png", "Passion" },
			{ "levels/level14.png", "Final Revenge" }
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
			savedscores.putInteger("score"+level, sc);
			
			if (level+1 < totalLevels)
			{
				Gdx.app.log("levelManager", "New High Score!");
				unlocked[level+1] = true;
				savedscores.putBoolean("unlocked"+(level+1), true);
			}
			
			if (sc == 3)
				unlockExtra();
		}
		savedscores.flush();
	}
	
	public void unlockExtra()
	{
		int l = 0;
		while (l < totalLevels && unlocked[l])
			l++;
		if (l < totalLevels)
		{
			unlocked[l] = true;
			savedscores.putBoolean("unlocked"+l, true);
		}
	}

	
	public int getNext(int n)
	{
		if (n == totalLevels-1)
			return -1;
		
		if (unlocked[n+1])
			return n+1;
		else 
			return n;
	}
	
	public boolean perfectScore()
	{
		for (int i = 0; i < totalLevels; i++)
			if (score[i] < 3)
				return false;
		return true;
	}
	
	public int getTotalUnlocked()
	{
		int ret = 0;
		while (ret < totalLevels && unlocked[ret] == true)
			ret++;
		
		return ret;			
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
