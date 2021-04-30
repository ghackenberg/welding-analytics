package com.hyperkit.analysis.events.values;

import com.hyperkit.analysis.events.ValueEvent;

public class OffsetChangeEvent extends ValueEvent<Integer>
{

	public OffsetChangeEvent(Integer value)
	{
		super(value);
	}

}
