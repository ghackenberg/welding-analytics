package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class VoltageHistogramCanvasPart extends HistogramCanvasPart
{
	
	public VoltageHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Voltage histogram", "Voltage", "V", frame, average, histogram);
	}

	@Override
	protected double getRawMinimum(Dataset file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getRawMaximum(Dataset file)
	{
		return file.getMaxVoltageDisplayed();
	}

	@Override
	protected double getRawValue(Dataset file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

	@Override
	protected void updateZoom(Dataset file, double min, double max)
	{
		file.setMinVoltagePercentage(min == -Double.MAX_VALUE ? file.getMinVoltageDisplayed() : min);
		file.setMaxVoltagePercentage(max == +Double.MAX_VALUE ? file.getMaxVoltageDisplayed() : max);
	}

	@Override
	protected double getPercentage(Dataset file)
	{
		return file.getVoltagePercentage();
	}

	@Override
	protected double getMean(Dataset file)
	{
		return file.getMeanVoltage();
	}

	@Override
	protected double getStdev(Dataset file)
	{
		return file.getStdevVoltage();
	}

	@Override
	protected double getMedian(Dataset file)
	{
		return file.getMedianVoltage();
	}

	@Override
	protected double getMode(Dataset file)
	{
		return file.getModeVoltage();
	}

	@Override
	protected double getRootMeanSquare(Dataset file)
	{
		return file.getRootMeanSquareVoltage();
	}

}
