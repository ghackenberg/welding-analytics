package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class CurrentTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public CurrentTimeseriesCanvasPart(int frame, int window, int average)
	{
		super("Current timeseries", "Current", "A", frame, window, average);
	}

	@Override
	protected double getRangeMinimum(Dataset file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRangeMaximum(Dataset file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRawRangeValue(Dataset file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

}
