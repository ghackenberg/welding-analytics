package com.hyperkit.analysis.parts.canvas.clouds;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.CloudCanvasPart;

public class CurrentVoltageCloudCanvasPart extends CloudCanvasPart
{

	public CurrentVoltageCloudCanvasPart(int frame, int window, int average)
	{
		super("Current-voltage cloud", "Current (in A)", "Voltage (in V)", frame, window, average);
	}

	@Override
	protected double getDomainMinimum(ASDFile file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getDomainMaximum(ASDFile file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return file.getMaxVoltageDisplayed();
	}

	@Override
	protected double getDomainValue(ASDFile file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

	@Override
	protected double getRangeValue(ASDFile file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

}
