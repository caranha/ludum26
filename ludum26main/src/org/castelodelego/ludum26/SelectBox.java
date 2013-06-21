package org.castelodelego.ludum26;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class SelectBox {

	
	public float x,y;
	public float w,h;
	Rectangle bbox;
	
	float curpos;
	int direction;
	
	float speed = 160;
	float SNAP = 3;
	
	float minifade;
	
	Texture btnTop;
	Rectangle btnTopBox;
	Texture btnBottom;
	Rectangle btnBottomBox;
	
	BitmapFontCache options;
	String list;
	int listlen;
	
	Preferences savedscores;
	
	public SelectBox(int px, int py, int wh, int hg)
	{
		x = px; 
		y = py;
		w = wh;
		h = hg;
		bbox = new Rectangle(x,y,w-64,h);
		
		options = new BitmapFontCache(ludum26entry.manager.get("sawasdee32.fnt", BitmapFont.class),true);
		options.setColor(Color.DARK_GRAY);
		
		btnTop = ludum26entry.manager.get("toparrow.png", Texture.class);
		btnTopBox = new Rectangle(x+w-64,y+h-64,64,64);
		btnBottom = ludum26entry.manager.get("bottomarrow.png", Texture.class);
		btnBottomBox = new Rectangle(x+w-64,y,64,64);
	}
	
	
	public void Render(OrthographicCamera camera, ShapeRenderer lineDrawer, SpriteBatch batch)
	{
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		Rectangle scissors = new Rectangle();
		ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), bbox, scissors);

		batch.begin();
		ScissorStack.pushScissors(scissors);
		options.draw(batch);
		batch.flush();
		ScissorStack.popScissors();
		
		if (curpos == 0)
			batch.setColor(1, 1, 1, 0.3f);
		else
			batch.setColor(1, 1, 1, 0.7f);
		batch.draw(btnTop, btnTopBox.x, btnTopBox.y);

		
		if (curpos >= (listlen-9)*options.getFont().getLineHeight())
			batch.setColor(1, 1, 1, 0.3f);
		else
			batch.setColor(1, 1, 1, 0.7f);
		batch.draw(btnBottom, btnBottomBox.x, btnBottomBox.y);
		batch.end();
		
//		// DEBUG
//		lineDrawer.setProjectionMatrix(camera.combined);
//		lineDrawer.begin(ShapeType.Rectangle);
//		lineDrawer.setColor(Color.DARK_GRAY);
//		lineDrawer.rect(bbox.x, bbox.y, bbox.width, bbox.height);		
//		lineDrawer.end();
		
		
		
		// FADE IN
		if (minifade > 0) 
		{
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			lineDrawer.setProjectionMatrix(camera.combined);
			lineDrawer.begin(ShapeType.FilledRectangle);
			lineDrawer.setColor(1f, 1f, 1f, minifade);
			lineDrawer.filledRect(x, y, h, w);		
			lineDrawer.end();
		}
	}
	
	/**
	 * Updates internal movement of the menu
	 * 
	 * @param Delta: The change in time from the render() call
	 */
	public void update(float delta)
	{
		if (minifade > 0)
			minifade -= delta;
		
		// moving curpos
		if (direction > 0)
			curpos += speed*delta;
		if (direction < 0)
			curpos -= speed*delta;
		
		// clipping curpos
		// Since the bottom clip is more strict, I do the bottom clip first
		// Then the top clip. Order matters.
		if (curpos > (listlen-9)*options.getFont().getLineHeight())
			curpos = (listlen-9)*options.getFont().getLineHeight();
		if (curpos < 0)
			curpos = 0;

		
		options.setPosition(0, curpos);
	}
	
	/**
	 * 
	 * @param x coordinate of the click
	 * @param y coordinate of the click
	 * @return -1 if nothing happens, X= 0 or more if we need to go to level X
	 */
	public int catchClick(float x, float y)
	{
		
		Gdx.app.log("Select Click", "caught: "+x+","+y);
		
		
		// Selecting a stage
		if (bbox.contains(x, y))
		{
			int ret = (int) Math.floor((this.y+this.h+curpos - y)/options.getFont().getLineHeight());
			if ((this.y+this.h+curpos - y)/options.getFont().getLineHeight() - ret < 0.7)
				return ret;
			

		}
		
		return -1;
	}
	
	public boolean catchPress(float x, float y)
	{
		// clicking on the top or bottom position
		if (btnBottomBox.contains(x,y))
		{
			direction = 1;
			return true;
		}
		if (btnTopBox.contains(x,y))
		{
			direction = -1;
			return true;
		}		
		direction = 0;
		return false;
	}
	public boolean releasePress()
	{
		direction = 0;
		return true;
	}
	
	
	
	/** Initialize the level selector **/
	public void start(LevelManager l)
	{
		savedscores = Gdx.app.getPreferences("Scores");
		
		listlen = l.getTotalUnlocked();
		Gdx.app.log("ListLen",listlen+"");
		
		list = "";
		for (int i = 0; i < listlen; i++)
		{
			int score = l.score[i];
			
			switch(score)
			{
			case 0:
				list = list + "  - ";
				break;
			case 1:
				list = list + "C - ";
				break;
			case 2:
				list = list + "B - ";
				break;
			case 3:
				list = list + "A - ";
				break;
			}
			
			list = list + LevelManager.levelList[i][1]+"\n";
		}


		curpos = 0; 
		
		// set the maximum "currpos" places
		// for debug, add everyone
		options.setMultiLineText(list, x, y+h);
		minifade = 1;
	}
	
}
