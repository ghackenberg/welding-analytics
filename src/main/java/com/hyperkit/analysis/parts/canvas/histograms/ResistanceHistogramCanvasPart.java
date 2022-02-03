package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class ResistanceHistogramCanvasPart extends HistogramCanvasPart
{
	
	public ResistanceHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Resistance histogram", "Resistance", "V/A", frame, average, histogram);
	}

	@Override
	protected double getRawMinimum(Dataset file)
	{
		return file.getMinResistanceDisplayed();
	}

	@Override
	protected double getRawMaximum(Dataset file)
	{
		return file.getMaxResistanceDisplayed();
	}

	@Override
	protected double getRawValue(Dataset file, int index)
	{
		return file.getAverageResistanceDisplayed(index, getAverage());
	}

	@Override
	protected void updateZoom(Dataset file, double min, double max)
	{
		file.setMinResistancePercentage(min == -Double.MAX_VALUE ? file.getMinResistanceDisplayed() : min);
		file.setMaxResistancePercentage(max == +Double.MAX_VALUE ? file.getMaxResistanceDisplayed() : max);
	}

	@Override
	protected double getPercentage(Dataset file)
	{
		return file.getResistancePercentage();
	}

	@Override
	protected double getMean(Dataset file)
	{
		return file.getMeanResistance();
	}

	@Override
	protected double getStdev(Dataset file)
	{
		return file.getStdevResistance();
	}

	@Override
	protected double getMedian(Dataset file)
	{
		return file.getMedianResistance();
	}

	@Override
	protected double getMode(Dataset file)
	{
		return file.getModeResistance();
	}

	@Override
	protected double getRootMeanSquare(Dataset file)
	{
		return file.getRootMeanSquareResistance();
	}

}
