package com.hyperkit.analysis.parts.canvas.traces;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.TraceCanvasPart;

public class CurrentVoltageTraceCanvasPart extends TraceCanvasPart
{

	public CurrentVoltageTraceCanvasPart(int frame, int window, int average)
	{
		super("Current-voltage average trace", "Current", "A", "Voltage", "V", "icons/parts/scatter.png", true, true, frame, window, average, 0);
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
	protected double getRawDomainValue(Dataset file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

	@Override
	protected double getRawRangeValue(Dataset file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

}
