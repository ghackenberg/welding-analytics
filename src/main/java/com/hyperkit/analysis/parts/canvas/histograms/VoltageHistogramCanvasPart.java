package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class VoltageHistogramCanvasPart extends HistogramCanvasPart
{
	
	public VoltageHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Voltage histogram", "Voltage (in V)", frame, average, histogram);
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
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

}
