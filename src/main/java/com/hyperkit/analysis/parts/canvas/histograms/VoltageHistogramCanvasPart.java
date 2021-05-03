package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class VoltageHistogramCanvasPart extends HistogramCanvasPart
{
	
	public VoltageHistogramCanvasPart()
	{
		super("Voltage histogram");
	}

	@Override
	protected double getRawMinimum(ASDFile file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getRawMaximum(ASDFile file)
	{
		return file.getMaxVoltageDisplayed();
	}

	@Override
	protected double getRawValue(ASDFile file, int index)
	{
		return file.getVoltageDisplayed(index);
	}

}
