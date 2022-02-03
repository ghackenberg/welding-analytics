package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class PowerTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public PowerTimeseriesCanvasPart(int frame, int window, int average)
	{
		super("Power timeseries", "Power", "V*A", frame, window, average);
	}

	@Override
	protected double getRangeMinimum(Dataset file)
	{
		return file.getMinPowerDisplayed();
	}

	@Override
	protected double getRangeMaximum(Dataset file)
	{
		return file.getMaxPowerDisplayed();
	}

	@Override
	protected double getRawRangeValue(Dataset file, int index)
	{
		return file.getAveragePowerDisplayed(index, getAverage());
	}

}
