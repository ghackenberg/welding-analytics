package com.hyperkit.analysis.events.values;

import com.hyperkit.analysis.events.ValueEvent;

public class SpreadChangeEvent extends ValueEvent<Integer>
{

	public SpreadChangeEvent(Integer value)
	{
		super(value);
	}

}
