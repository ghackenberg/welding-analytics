package com.hyperkit.analysis.events;

import com.hyperkit.analysis.Event;

public class ValueEvent<T> extends Event
{
	
	private T value;
	
	public ValueEvent(T value)
	{
		this.value = value;
	}
	
	public T getValue()
	{
		return value;
	}

}
