package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class ResistanceTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public ResistanceTimeseriesCanvasPart()
	{
		super("Resistance timeseries", "Resistance (in Ohm)");
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
		return file.getResistanceDisplayed(index);
	}

}
