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
	
	private Set<Part> parts = new HashSet<>();
	
	public boolean addPart(Part part)
	{
		return parts.add(part);
	}
	public boolean removePart(Part part)
	{
		return parts.remove(part);
	}
	
	public boolean broadcastEvent(Event event)
	{
		boolean result = true;
		
		for (Part part : parts)
		{
			result = result && part.handleEvent(event);
		}
		
		return result;
	}

}
