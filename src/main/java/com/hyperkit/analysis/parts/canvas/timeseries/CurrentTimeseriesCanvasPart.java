package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class CurrentTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public CurrentTimeseriesCanvasPart(int frame, int window, int average)
	{
		super("Current timeseries", "Current (in A)", frame, window, average);
	}

	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRawRangeValue(ASDFile file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

}
