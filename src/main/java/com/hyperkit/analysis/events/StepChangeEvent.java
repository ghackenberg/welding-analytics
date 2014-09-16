package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class StepChangeEvent extends Event
{
	
	private int step;
	
	public StepChangeEvent(int step)
	{
		this.step = step;
	}
	
	public int getStep()
	{
		return step;
	}

}
