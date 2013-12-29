package org.shipvgdc.select;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.JsonReader;

public class Utils {
	public static AssetManager assets;
	public static JsonReader json;
	
	public static Music bgm;
	
	static
	{
		assets = new AssetManager();
		json = new JsonReader();
	}
}
