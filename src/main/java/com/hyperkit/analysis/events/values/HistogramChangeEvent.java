package com.hyperkit.analysis.events.values;

import com.hyperkit.analysis.events.ValueEvent;

public class HistogramChangeEvent extends ValueEvent<Integer>
{

	public HistogramChangeEvent(Integer value)
	{
		super(value);
	}

}
