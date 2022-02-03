package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class PowerHistogramCanvasPart extends HistogramCanvasPart
{
	
	public PowerHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Power histogram", "Power", "VA", frame, average, histogram);
	}

	@Override
	protected double getRawMinimum(Dataset file)
	{
		return file.getMinPowerDisplayed();
	}

	@Override
	protected double getRawMaximum(Dataset file)
	{
		return file.getMaxPowerDisplayed();
	}

	@Override
	protected double getRawValue(Dataset file, int index)
	{
		return file.getAveragePowerDisplayed(index, getAverage());
	}

	@Override
	protected void updateZoom(Dataset file, double min, double max)
	{
		file.setMinPowerPercentage(min == -Double.MAX_VALUE ? file.getMinPowerDisplayed() : min);
		file.setMaxPowerPercentage(max == +Double.MAX_VALUE ? file.getMaxPowerDisplayed() : max);
	}

	@Override
	protected double getPercentage(Dataset file)
	{
		return file.getPowerPercentage();
	}

	@Override
	protected double getMean(Dataset file)
	{
		return file.getMeanPower();
	}

	@Override
	protected double getStdev(Dataset file)
	{
		return file.getStdevPower();
	}

	@Override
	protected double getMedian(Dataset file)
	{
		return file.getMedianPower();
	}

	@Override
	protected double getMode(Dataset file)
	{
		return file.getModePower();
	}

	@Override
	protected double getRootMeanSquare(Dataset file)
	{
		return file.getRootMeanSquarePower();
	}

}
