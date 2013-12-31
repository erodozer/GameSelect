package org.shipvgdc.select;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.JsonReader;

public class Utils {
	public static AssetManager assets;
	public static JsonReader json;
	
	public static Music bgm;
	public static InputProcessor utilInput;
	
	static
	{
		assets = new AssetManager();
		json = new JsonReader();
		
		utilInput = new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				//switch between fullscreen and windowed
				if (keycode == Keys.F11)
				{
					if (Gdx.graphics.isFullscreen())
					{
						Gdx.graphics.setDisplayMode(1280, 720, false);
					}
					else
					{
						// set resolution to default and set full-screen to true
						Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
					}
					return true;
				}
				//Boss key, kill the select screen
				else if (keycode == Keys.F9)
				{
					Gdx.app.exit();
				}
				return false;
			}

			public boolean keyUp(int keycode) { return false; }
			public boolean keyTyped(char character) { return false; }
			public boolean touchDown(int screenX, int screenY, int pointer,	int button) { return false; }
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {	return false; }
			public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
			public boolean mouseMoved(int screenX, int screenY) { return false; }
			public boolean scrolled(int amount) { return false; }
		};
	}
}
