package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class SpreadChangeEvent extends Event
{
	
	private int spread;
	
	public SpreadChangeEvent(int spread)
	{
		this.spread = spread;
	}
	
	public int getSpread()
	{
		return spread;
	}

}
