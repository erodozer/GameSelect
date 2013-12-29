package org.shipvgdc.select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.JsonValue;

public class Menu {
	
	HashMap<String, ProcessBuilder> commands;
	
	public Menu(JsonValue menu)
	{
		commands = new HashMap<String, ProcessBuilder>();
		
		for (int i = 0; i < menu.size; i++)
		{
			JsonValue command = menu.get(i);
			ProcessBuilder b;
			String name = command.name;
			String exec = command.getString("command");
			String[] args;
			//parse out the command args
			{
				ArrayList<String> parsedArgs = new ArrayList<String>();
				parsedArgs.add(exec);
				for (JsonValue a : command.get("args"))
				{
					parsedArgs.add(a.asString());
				}
				args = new String[parsedArgs.size()];
				parsedArgs.toArray(args);
			}
			
			b = new ProcessBuilder(args);
			commands.put(name, b);
		}
	}
	
	
	public Process launch(String command) throws IOException
	{
		return commands.get(command).start();
	}
	
}
