package com.hyperkit.analysis.events.values;

import java.util.Map;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.ValueEvent;

public class MarkerChangeEvent extends ValueEvent<Map<Dataset, Integer>>
{

	public MarkerChangeEvent(Map<Dataset, Integer> value)
	{
		super(value);
	}

}
