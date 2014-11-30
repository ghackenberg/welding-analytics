package com.hyperkit.analysis;

import java.util.HashSet;
import java.util.Set;

public class Bus
{
	
	private static Bus instance = new Bus();
	
	public static Bus getInstance()
	{
		return instance;
	}
	
	private Set<Handler> handlers = new HashSet<>();
	
	public boolean addHandler(Handler handler)
	{
		return handlers.add(handler);
	}
	public boolean removeHandler(Handler handler)
	{
		return handlers.remove(handler);
	}
	
	public boolean broadcastEvent(Event event)
	{
		boolean result = true;
		
		for (Handler handler : handlers)
		{
			result = result && handler.handleEvent(event);
		}
		
		return result;
	}

}
