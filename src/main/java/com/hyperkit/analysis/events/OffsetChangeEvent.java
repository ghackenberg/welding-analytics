package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class OffsetChangeEvent extends Event
{
	
	private int offset;
	
	public OffsetChangeEvent(int offset)
	{
		this.offset = offset;
	}
	
	public int getOffset()
	{
		return offset;
	}

}
