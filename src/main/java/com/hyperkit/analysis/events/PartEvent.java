package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;
import com.hyperkit.analysis.Part;

public abstract class PartEvent<P extends Part> extends Event
{
	
	private P part;
	
	public PartEvent(P part)
	{
		this.part = part;
	}
	
	public P getPart()
	{
		return part;
	}

}
