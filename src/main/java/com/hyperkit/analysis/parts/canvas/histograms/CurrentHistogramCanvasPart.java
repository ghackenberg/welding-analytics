package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class CurrentHistogramCanvasPart extends HistogramCanvasPart
{
	
	public CurrentHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Current histogram", "Current", "A", frame, average, histogram);
	}

	@Override
	protected double getRawMinimum(Dataset file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRawMaximum(Dataset file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRawValue(Dataset file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

	@Override
	protected void updateZoom(Dataset file, double min, double max)
	{
		file.setMinCurrentPercentage(min == -Double.MAX_VALUE ? file.getMinCurrentDisplayed() : min);
		file.setMaxCurrentPercentage(max == +Double.MAX_VALUE ? file.getMaxCurrentDisplayed() : max);
	}

	@Override
	protected double getPercentage(Dataset file)
	{
		return file.getCurrentPercentage();
	}

	@Override
	protected double getMean(Dataset file)
	{
		return file.getMeanCurrent();
	}

	@Override
	protected double getStdev(Dataset file)
	{
		return file.getStdevCurrent();
	}

	@Override
	protected double getMedian(Dataset file)
	{
		return file.getMedianCurrent();
	}

	@Override
	protected double getMode(Dataset file)
	{
		return file.getModeCurrent();
	}

	@Override
	protected double getRootMeanSquare(Dataset file)
	{
		return file.getRootMeanSquareCurrent();
	}

}
