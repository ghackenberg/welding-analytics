package com.hyperkit.analysis.parts.canvas.clouds;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.CloudCanvasPart;

public class CurrentVoltageCloudCanvasPart extends CloudCanvasPart
{

	public CurrentVoltageCloudCanvasPart(int frame, int window, int average)
	{
		super("Current-voltage cloud", "Current", "A", "Voltage", "V", frame, window, average);
	}

	@Override
	protected double getDomainMinimum(Dataset file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRangeMinimum(Dataset file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getDomainMaximum(Dataset file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRangeMaximum(Dataset file)
	{
		return file.getMaxVoltageDisplayed();
	}

	@Override
	protected double getDomainValue(Dataset file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

	@Override
	protected double getRangeValue(Dataset file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

}
