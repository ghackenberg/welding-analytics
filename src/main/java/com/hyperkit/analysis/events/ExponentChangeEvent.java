package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class ExponentChangeEvent extends Event
{
	
	private int exponent;
	
	public ExponentChangeEvent(int exponent)
	{
		this.exponent = exponent;
	}
	
	public int getExponent()
	{
		return exponent;
	}

}
