package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class ResistanceTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public ResistanceTimeseriesCanvasPart(int frame, int window, int average)
	{
		super("Resistance timeseries", "Resistance", "V/A", frame, window, average);
	}

	@Override
	protected double getRangeMinimum(Dataset file)
	{
		return file.getMinResistanceDisplayed();
	}

	@Override
	protected double getRangeMaximum(Dataset file)
	{
		return file.getMaxResistanceDisplayed();
	}

	@Override
	protected double getRawRangeValue(Dataset file, int index)
	{
		return file.getAverageResistanceDisplayed(index, getAverage());
	}

}
