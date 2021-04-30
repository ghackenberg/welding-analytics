package com.hyperkit.analysis.events.values;

import com.hyperkit.analysis.events.ValueEvent;

public class ProgressChangeEvent extends ValueEvent<Integer>
{

	public ProgressChangeEvent(Integer value)
	{
		super(value);
	}

}
