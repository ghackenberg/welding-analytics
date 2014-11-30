package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class ProgressChangeEvent extends Event
{
	
	private int progress;
	
	public ProgressChangeEvent(int progress)
	{
		this.progress = progress;
	}
	
	public int getProgress()
	{
		return progress;
	}

}
