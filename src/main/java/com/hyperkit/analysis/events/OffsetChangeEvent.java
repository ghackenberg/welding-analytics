package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class OffsetChangeEvent extends Event
{
	
	private double offset;
	
	public OffsetChangeEvent(double offset)
	{
		this.offset = offset;
	}
	
	public double getOffset()
	{
		return offset;
	}

}
