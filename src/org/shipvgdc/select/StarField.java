package org.shipvgdc.select;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static org.shipvgdc.select.Scene.FOV;

public class StarField{

	Array<Star> stars;
	private final int AMOUNT = 100;
	
	public StarField()
	{
		stars = new Array<Star>(AMOUNT);
		for (int i = 0; i < AMOUNT; i++)
		{
			Star s = new Star(new Vector2(FOV[0], MathUtils.random(32, FOV[1])), MathUtils.random(1, 15));
			stars.add(s);
		}
	}
	
	public void update(float delta)
	{
		for (Star s : stars)
		{
			if (s.pos.x + s.xLength < 0)
			{
				s.set(new Vector2(FOV[0], MathUtils.random(32, FOV[1])), MathUtils.random(1, 15));
			}
			s.update(delta);
		}
	}

	public void draw(ShapeRenderer sr)
	{
		for (Star s : stars)
		{
			s.draw(sr);
		}
	}
	
	private static class Star
	{
		float vX;
		//star stems
		Vector2 pos, vert;
		float xLength, yLength;
		Color c;
		
		private static Color[] STARCOLORS;
		static
		{
			STARCOLORS = new Color[]{
					Color.BLUE,
					Color.GREEN,
					Color.RED,
					Color.PINK,
					Color.MAGENTA,
					Color.CYAN,
					Color.LIGHT_GRAY,
					Color.ORANGE
			};
		}
		
		public Star(Vector2 xy, float velocity)
		{
			set(xy, velocity);
		}
		
		public void set(Vector2 xy, float velocity)
		{
			pos = xy;
			vX = velocity;
			
			xLength = 5+vX;
			
			float yLength = 5/vX * 2;
			vert = pos.cpy();
			vert.add(xLength / 2, yLength/2);
			
			c = STARCOLORS[MathUtils.random(0, STARCOLORS.length-1)];
		}
		
		/**
		 * Shifts the star over
		 * @param delta
		 */
		public void update(float delta)
		{
			float move = vX * delta * FOV[0]/10;
			pos.x -= move;
			vert.x -= move;
		}
		
		
		public void draw(ShapeRenderer sr)
		{
			sr.line(pos.x, pos.y, pos.x + xLength * .75f, pos.y, c, Color.WHITE);
			sr.line(pos.x + xLength * .25f, pos.y, pos.x + xLength, pos.y, Color.WHITE, c);
			
			sr.line(vert.x, vert.y, vert.x, vert.y + yLength * .75f, c, Color.WHITE);
			sr.line(vert.x, vert.y + yLength * .25f, vert.x, vert.y + yLength, Color.WHITE, c);
		}
	}
}
