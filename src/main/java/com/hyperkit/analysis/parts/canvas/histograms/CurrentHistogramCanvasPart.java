package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class CurrentHistogramCanvasPart extends HistogramCanvasPart
{
	
	public CurrentHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Current histogram", "Current (in A)", frame, average, histogram);
	}

	@Override
	protected double getRawMinimum(ASDFile file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRawMaximum(ASDFile file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRawValue(ASDFile file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

	@Override
	protected void updatePercentage(ASDFile file, double min, double max)
	{
		file.setMinCurrentPercentage(min == -Double.MAX_VALUE ? file.getMinCurrentDisplayed() : min);
		file.setMaxCurrentPercentage(max == +Double.MAX_VALUE ? file.getMaxCurrentDisplayed() : max);
	}

}
