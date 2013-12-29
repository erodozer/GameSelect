package org.shipvgdc.select;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class WaveFont {

	BitmapFont font;
	float waveSize;
	float timer;
	float duration;
	
	public WaveFont(BitmapFont font, float size, float speed)
	{
		this.font = font;
		waveSize = size;
		timer = 0;
		duration = speed;
	}
	
	public void update(float delta)
	{
		timer += delta;
		if (timer > duration)
			timer -= duration;
	}
	
	public void draw(SpriteBatch batch, String str, float x, float y)
	{
		float sigma = MathUtils.PI2 / str.length();
		float theta = (timer/duration) * MathUtils.PI2;
		float width = font.getBounds(str).width;
		float wave = y + waveSize * MathUtils.sin(theta);
		for (int i = 0; i < str.length(); i++)
		{
			String c = str.substring(i, i+1);
			TextBounds t = font.getBounds(c);
			font.draw(batch, c, x, wave + t.height/2);
			
			x += t.width;
			theta += sigma;	
			wave = y + waveSize * MathUtils.sin(theta);
		}
	}
}
