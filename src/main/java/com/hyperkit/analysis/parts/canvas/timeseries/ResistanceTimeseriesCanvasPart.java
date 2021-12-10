package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class ResistanceTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public ResistanceTimeseriesCanvasPart(int frame, int window, int average)
	{
		super("Resistance timeseries", "Resistance", "V/A", frame, window, average);
	}

	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return file.getMinResistanceDisplayed();
	}

	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return file.getMaxResistanceDisplayed();
	}

	@Override
	protected double getRawRangeValue(ASDFile file, int index)
	{
		return file.getAverageResistanceDisplayed(index, getAverage());
	}

}
