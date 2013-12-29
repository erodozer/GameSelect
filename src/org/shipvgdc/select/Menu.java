package org.shipvgdc.select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.JsonValue;

public class Menu {
	
	private ArrayList<String> names;
	private HashMap<String, ProcessBuilder> commands;
	
	public Menu(JsonValue menu)
	{
		commands = new HashMap<String, ProcessBuilder>();
		names = new ArrayList<String>();
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
			names.add(name);
		}
	}
	
	public int size()
	{
		return commands.size();
	}
	
	
	public Process launch(String command) throws IOException
	{
		return commands.get(command).start();
	}
	
	public String get(int i)
	{
		return names.get(i);
	}

	public Object[] names() {
		return names.toArray();
	}
}
