package com.hyperkit.analysis.events.values;

import com.hyperkit.analysis.events.ValueEvent;

public class AverageChangeEvent extends ValueEvent<Integer>
{

	public AverageChangeEvent(Integer value)
	{
		super(value);
	}

}
