package com.hyperkit.analysis.parts.canvas.traces;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TraceCanvasPart;

public class CurrentVoltageTraceCanvasPart extends TraceCanvasPart
{

	public CurrentVoltageTraceCanvasPart()
	{
		super("Current-voltage trace", "Current (in A)", "Voltage (in V)", CurrentVoltageTraceCanvasPart.class.getClassLoader().getResource("icons/parts/scatter.png"), 1000, 0);
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
	protected double getRawDomainValue(ASDFile file, int index)
	{
		return file.getCurrentDisplayed(index);
	}

	@Override
	protected double getRawRangeValue(ASDFile file, int index)
	{
		return file.getVoltageDisplayed(index);
	}

}
