package com.hyperkit.analysis.events.values;

import java.util.Map;

import com.hyperkit.analysis.events.ValueEvent;
import com.hyperkit.analysis.files.ASDFile;

public class MarkerChangeEvent extends ValueEvent<Map<ASDFile, Integer>>
{

	public MarkerChangeEvent(Map<ASDFile, Integer> value)
	{
		super(value);
	}

}
