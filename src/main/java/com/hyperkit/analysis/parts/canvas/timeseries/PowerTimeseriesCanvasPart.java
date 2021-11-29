package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class PowerTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public PowerTimeseriesCanvasPart(int frame, int window, int average)
	{
		super("Power timeseries", "Power (in V*A)", frame, window, average);
	}

	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return file.getMinPowerDisplayed();
	}

	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return file.getMaxPowerDisplayed();
	}

	@Override
	protected double getRawRangeValue(ASDFile file, int index)
	{
		return file.getAveragePowerDisplayed(index, getAverage());
	}

}
