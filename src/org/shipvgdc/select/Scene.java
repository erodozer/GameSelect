package org.shipvgdc.select;

import java.io.IOException;
import java.util.Calendar;

import sun.util.calendar.BaseCalendar.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Scene implements Screen {

	//holds the list of all launchable commands
	Menu games;
	
	boolean started = false;

	boolean hideMenu = true;  //Hides the game select menu
	
	BitmapFont scrollFont;    //font that scrolls at the bottom of the screen
	WaveFont waveFont;        //font that waves in the middle of the screen
	BitmapFont menuFont;      //font used to display the menu
	
	StarField sf;
	
	SpriteBatch batch;
	Camera camera;
	ShapeRenderer sr;
	
	//colors used for navigating the menu
	Color menuBack;
	Color highlight;
	Color menuText;
	
	//default internal resolution
	public static int[] FOV = {800, 480};
	
	//currently running game
	Process game;
	
	//scrolling positions
	float bottomScroll = -30;
	float waveScroll = -30;
	
	//scrolling speeds;
	float bottomSpd;
	float waveSpd;
	
	//main message to show scrolling across the screen with the wave font
	public static String message = "test";
	float messageWidth;
	
	int menuIndex;
	

	private Array<FileHandle> bgmPaths;
	private Music nextBgm;	//preloaded bgm;

	public void init() {
		JsonValue list = Utils.json.parse(Gdx.files.internal("games.json"));
		games = new Menu(list);
	
		batch = new SpriteBatch();
		OrthographicCamera c = new OrthographicCamera();
		c.setToOrtho(false, FOV[0], FOV[1]);
		camera = c;
		
		sr = new ShapeRenderer();
		
		waveFont = new WaveFont(new BitmapFont(Gdx.files.internal("wave.fnt"), false), 50, 4f);
		scrollFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
		menuFont = new BitmapFont(Gdx.files.internal("font.fnt"), false);
		
		bottomScroll = 0;
		waveScroll = 0;
		
		bottomSpd = FOV[0] / 10f;
		waveSpd = FOV[0] / 15f;
		
		messageWidth = waveFont.font.getBounds(message).width;
		
		sf = new StarField();
		
		bgmPaths = new Array<FileHandle>(Gdx.files.internal("music/").list());
		System.out.println(bgmPaths);
		if (bgmPaths.size > 0)
		{
			FileHandle b = bgmPaths.get((int)(Math.random()*bgmPaths.size));
			FileHandle n = bgmPaths.get((int)(Math.random()*bgmPaths.size));
			if (Utils.bgm != null)
				Utils.bgm.dispose();
			Utils.bgm = Gdx.audio.newMusic(b);
			nextBgm = Gdx.audio.newMusic(n);
			nextBgm.setLooping(false);
			Utils.bgm.setLooping(false);
		}
		
		menuBack = new Color(0.0f, 0.0f, .5f, .5f);
		highlight = Color.YELLOW;
		menuText = Color.WHITE;
		
		Gdx.input.setInputProcessor(new InputProcessor(){

			@Override
			public boolean keyDown(int keycode) {
				if (!hideMenu){
					//navigate the menu
					if (keycode == Keys.DOWN)
					{
						menuIndex++;
					}
					else if (keycode == Keys.UP)
					{
						menuIndex--;
					}
					menuIndex = MathUtils.clamp(menuIndex, 0, games.size()-1);
					
					//launch a game
					if (keycode == Keys.ENTER)
					{
						try {
							game = games.launch(games.get(menuIndex));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					//hide the menu
					if (keycode == Keys.ESCAPE || keycode == Keys.BACKSPACE)
					{
						hideMenu = true;
					}
				}
				else
				{
					//show the menu on any key press
					hideMenu = false;
					
					//Boss key, kill the select screen
					if (keycode == Keys.F9)
					{
						System.exit(0);
					}
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
	}
	
	/**
	 * Called once all assets have been loaded
	 */
	public void start() {
		if (Utils.bgm != null)
			Utils.bgm.play();
		
	}
	
	@Override
	public void render(float delta) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		sr.setProjectionMatrix(camera.combined);
		
		GLCommon gl = Gdx.gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		if (!Utils.assets.update())
		{
			waveFont.update(delta);
			
			batch.begin();
			waveFont.draw(batch, "Loading", FOV[0], FOV[1]);
			batch.end();
			return;
		}
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//draw background
		{
			sr.begin(ShapeType.Line);
			sf.update(delta);
			sf.draw(sr);
			sr.end();
			
			sr.setColor(Color.RED);
			sr.begin(ShapeType.Filled);
			sr.rect(0, 0, Gdx.graphics.getWidth(), 32f);
			sr.end();
		}
		gl.glDisable(GL10.GL_BLEND);
		
		//draw scrolling text
		{
			batch.begin();
			//draw bottom scrolling message
			{
				String date = Calendar.getInstance().getTime().toString();
				TextBounds bounds = scrollFont.getBounds(date);
				
				bottomScroll += bottomSpd * delta;
			
				//reset message position
				if (FOV[0] - bottomScroll + bounds.width < 0)
				{
					bottomScroll = -30;
				}
				
				scrollFont.draw(batch, date, FOV[0]-bottomScroll, 38 - bounds.height);
			
			}
			
			//draw waving message
			{
				waveScroll += waveSpd * delta;
				if (FOV[0] - waveScroll + messageWidth < 0)
				{
					waveScroll = -30;
				}
				waveFont.update(delta);
				waveFont.draw(batch, message, FOV[0] - waveScroll, FOV[1]/2);
			}
			batch.end();
		}
		
		//draw menu
		if (!hideMenu)
		{
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			sr.setColor(this.menuBack);
			sr.begin(ShapeType.Filled);
			sr.rect(20, 40, FOV[0] - 40, FOV[1] - 60);
			sr.end();
			gl.glDisable(GL10.GL_BLEND);
			
			batch.begin();
			for (int i = 0, x = 32, y = (int) (FOV[1] - 32 - menuFont.getLineHeight()); i < games.size(); i++, y -= menuFont.getLineHeight())
			{
				if (i == menuIndex)
				{
					menuFont.setColor(highlight);
				}
				else
				{
					menuFont.setColor(menuText);
				}
				menuFont.draw(batch, games.get(i), x, y);
			}
			batch.end();
		}
		
		if (Utils.bgm != null && !Utils.bgm.isPlaying())
		{
			Utils.bgm.dispose();
			Utils.bgm = nextBgm;
			Utils.bgm.play();
			
			FileHandle n = bgmPaths.get((int)(Math.random()*bgmPaths.size));
			nextBgm = Gdx.audio.newMusic(n);
			nextBgm.setLooping(false);
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		if (!started)
		{
			init();
			started = true;
		}	
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		if (Utils.bgm != null)
			Utils.bgm.pause();
	}

	@Override
	public void resume() {
		if (Utils.bgm != null)
			Utils.bgm.play();
	}

	@Override
	public void dispose() {

	}
	
}
