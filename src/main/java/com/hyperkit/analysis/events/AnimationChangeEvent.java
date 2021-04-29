package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class AnimationChangeEvent extends Event
{
	
	private int progress;
	
	public AnimationChangeEvent(int progress)
	{
		this.progress = progress;
	}
	
	public int getProgress()
	{
		return progress;
	}

}
