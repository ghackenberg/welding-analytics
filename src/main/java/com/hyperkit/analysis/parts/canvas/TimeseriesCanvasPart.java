package com.hyperkit.analysis.parts.canvas;

import com.hyperkit.analysis.files.ASDFile;

public abstract class TimeseriesCanvasPart extends TraceCanvasPart
{
	
	public TimeseriesCanvasPart(String title, String range, int frame, int window, int average)
	{
		super(title, "Time (in s)", range, TimeseriesCanvasPart.class.getClassLoader().getResource("icons/parts/timeseries.png"), frame, window, average, 0);
	}
	
	@Override
	protected double getDomainMinimum(ASDFile file)
	{
		return getDataLength(file) > 0 ? getDomainValue(file, 0) : Double.MAX_VALUE;
	}
	
	@Override
	protected double getDomainMaximum(ASDFile file)
	{	
		return getDataLength(file) > 0 ? getDomainValue(file, getDataLength(file) - 1) : -Double.MAX_VALUE;
	}
	
	@Override
	protected double getRawDomainValue(ASDFile file, int index)
	{
		return file.getTimestampDisplayed(index);
	}
	
}
