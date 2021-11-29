package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class PowerHistogramCanvasPart extends HistogramCanvasPart
{
	
	public PowerHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Power histogram", "Power (in V*A)", frame, average, histogram);
	}

	@Override
	protected double getRawMinimum(ASDFile file)
	{
		return file.getMinPowerDisplayed();
	}

	@Override
	protected double getRawMaximum(ASDFile file)
	{
		return file.getMaxPowerDisplayed();
	}

	@Override
	protected double getRawValue(ASDFile file, int index)
	{
		return file.getAveragePowerDisplayed(index, getAverage());
	}

	@Override
	protected void updatePercentage(ASDFile file, double min, double max)
	{
		file.setMinPowerPercentage(min == -Double.MAX_VALUE ? file.getMinPowerDisplayed() : min);
		file.setMaxPowerPercentage(max == +Double.MAX_VALUE ? file.getMaxPowerDisplayed() : max);
	}

}
