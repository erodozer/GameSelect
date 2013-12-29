package org.shipvgdc.select;

import java.io.IOException;
import java.util.Calendar;

import sun.util.calendar.BaseCalendar.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
	
	Stage menu;

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
		
		menu = new Stage();
		
		//create the table to hold the ui elements
		Table container = new Table();
		menu.addActor(container);
		container.setFillParent(true);
		
		//set graphics
		Color highlight = Color.YELLOW;
		Color menuText = Color.WHITE;
		NinePatch select = new NinePatch(new Texture(Gdx.files.internal("images/select.png")), 3, 3, 3, 3);
		NinePatch front = new NinePatch(new Texture(Gdx.files.internal("images/scrollbar.png")), 3, 3, 3, 3);
		NinePatch background = new NinePatch(new Texture(Gdx.files.internal("images/scrollback.png")), 3, 3, 3, 3);
		Texture t = new Texture(Gdx.files.internal("images/scrollbar_back.png"));
		t.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		NinePatch back = new NinePatch(t, 3, 3, 3, 3);
		
		//create skins
		List.ListStyle skinL = new List.ListStyle(menuFont, highlight, menuText, new NinePatchDrawable(select));
		ScrollPane.ScrollPaneStyle skin = new ScrollPane.ScrollPaneStyle(new NinePatchDrawable(background), new NinePatchDrawable(back), new NinePatchDrawable(front), new NinePatchDrawable(back), new NinePatchDrawable(front));
		
		//create list and scrollpane
		final List l = new List(games.names(), skinL);
		final ScrollPane pane = new ScrollPane(l, skin);
		pane.setScrollbarsOnTop(true);
		
		//set the container and add the pane
		container.pad(20);
		container.padBottom(52);
		container.add(pane).fill().expand();
		
		menu.addListener(new InputListener(){
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (!hideMenu){
					switch(keycode) {
					 	 case Keys.DPAD_UP: {
							 int index = l.getSelectedIndex() - 1;
							 if(index < 0)
								 index = l.getItems().length - 1;
							 l.setSelectedIndex(index);
							 float y = l.getItemHeight()*index;
							 if (y < pane.getScrollY() || y > pane.getScrollY() + pane.getHeight())
							 {
								 pane.setScrollY(y);
							 }
							 break;
					 	 }
						 case Keys.DPAD_DOWN: {
							 int index = l.getSelectedIndex() + 1;
							 if(index > l.getItems().length - 1)
								 index = 0;
							 l.setSelectedIndex(index);
							 float y = l.getItemHeight()*index;
							 if (y < pane.getScrollY() || y > pane.getScrollY() + pane.getHeight())
							 {
								 pane.setScrollY(y);
							 }
							 break;
						 }
				 	 }
						
					//launch a game
					if (keycode == Keys.ENTER)
					{
						try {
							game = games.launch(games.get(l.getSelectedIndex()));
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
						Gdx.app.exit();
					}
				}
				return false;
			}
		});
		menu.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }
        });
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(menu);
		
		Gdx.input.setInputProcessor(im);
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
		menu.act(delta);
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
			menu.setCamera(camera);
			menu.setViewport(FOV[0], FOV[1]);
			menu.draw();
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
