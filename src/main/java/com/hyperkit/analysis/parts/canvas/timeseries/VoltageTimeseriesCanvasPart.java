package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class VoltageTimeseriesCanvasPart extends TimeseriesCanvasPart
{
	
	public VoltageTimeseriesCanvasPart()
	{
		super("Voltage timeseries", "Voltage (in V)");
	}

	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return file.getMaxVoltageDisplayed();
	}

	@Override
	protected double getRawRangeValue(ASDFile file, int index)
	{
		return file.getVoltageDisplayed(index);
	}

}
