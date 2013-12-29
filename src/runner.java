import org.shipvgdc.select.Scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

/**
 * Main application runner for the game
 * @author nhydock
 *
 */
public class runner  {
	
	private static class GameSelect extends Game
	{
		public GameSelect()
		{
			
		}
		
		@Override
		public void create() {
			Scene s = new Scene();
			
			this.setScreen(s);
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game g = new GameSelect();
		String title = "Game Select";
		if (args.length > 0)
		{
		    // define the window's title
	        title = args[0];
	 	}
		
		Scene.message = title;
 
        // define the window's size
        final int width = 1280;
        final int height = 720;
        
        // use ES2 to allow for textures that are not powers of 2
        final boolean useOpenGLES2 = true;
        
        // create the game using Lwjgl starter class
        LwjglApplication app = new LwjglApplication(g, title, width, height, useOpenGLES2 );
	}
}
