package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class VoltageTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public VoltageTimeseriesCanvasPart(int frame, int window, int average)
	{
		super("Voltage timeseries", "Voltage", "V", frame, window, average);
	}

	@Override
	protected double getRangeMinimum(Dataset file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getRangeMaximum(Dataset file)
	{
		return file.getMaxVoltageDisplayed();
	}

	@Override
	protected double getRawRangeValue(Dataset file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

}
