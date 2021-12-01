package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class VoltageHistogramCanvasPart extends HistogramCanvasPart
{
	
	public VoltageHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Voltage histogram", "Voltage", "V", frame, average, histogram);
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

	@Override
	protected void updateZoom(ASDFile file, double min, double max)
	{
		file.setMinVoltagePercentage(min == -Double.MAX_VALUE ? file.getMinVoltageDisplayed() : min);
		file.setMaxVoltagePercentage(max == +Double.MAX_VALUE ? file.getMaxVoltageDisplayed() : max);
	}

	@Override
	protected double getPercentage(ASDFile file)
	{
		return file.getVoltagePercentage();
	}

	@Override
	protected double getMean(ASDFile file)
	{
		return file.getMeanVoltage();
	}

	@Override
	protected double getStdev(ASDFile file)
	{
		return file.getStdevVoltage();
	}

	@Override
	protected double getMedian(ASDFile file)
	{
		return file.getMedianVoltage();
	}

	@Override
	protected double getRootMeanSquare(ASDFile file)
	{
		return file.getRootMeanSquareVoltage();
	}

}
