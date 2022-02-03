package com.hyperkit.analysis.parts.canvas;

import com.hyperkit.analysis.Dataset;

public abstract class TimeseriesCanvasPart extends TraceCanvasPart
{
	
	public TimeseriesCanvasPart(String title, String range, String rangeUnit, int frame, int window, int average)
	{
		super(title, "Time", "s", range, rangeUnit, "icons/parts/timeseries.png", false, true, frame, window, average, 0);
	}
	
	@Override
	protected double getDomainMinimum(Dataset file)
	{
		return getDataLength(file) > 0 ? getDomainValue(file, 0) : Double.MAX_VALUE;
	}
	
	@Override
	protected double getDomainMaximum(Dataset file)
	{	
		return getDataLength(file) > 0 ? getDomainValue(file, getDataLength(file) - 1) : -Double.MAX_VALUE;
	}
	
	@Override
	protected double getRawDomainValue(Dataset file, int index)
	{
		return file.getTimestampDisplayed(index);
	}
	
}
