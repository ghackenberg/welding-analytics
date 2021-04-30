package com.hyperkit.analysis.events.values;

import com.hyperkit.analysis.events.ValueEvent;

public class WindowChangeEvent extends ValueEvent<Integer>
{

	public WindowChangeEvent(Integer value)
	{
		super(value);
	}

}
