package com.hyperkit.analysis.events.values;

import com.hyperkit.analysis.events.ValueEvent;

public class StrokeChangeEvent extends ValueEvent<Integer>
{

	public StrokeChangeEvent(Integer value)
	{
		super(value);
	}

}
