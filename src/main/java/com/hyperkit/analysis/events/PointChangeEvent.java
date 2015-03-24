package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class PointChangeEvent extends Event
{
	
	private int point;
	
	public PointChangeEvent(int point)
	{
		this.point = point;
	}
	
	public int getPoint()
	{
		return point;
	}

}
