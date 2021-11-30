package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class ResistanceHistogramCanvasPart extends HistogramCanvasPart
{
	
	public ResistanceHistogramCanvasPart(int frame, int average, int histogram)
	{
		super("Resistance histogram", "Resistance (in V/A)", frame, average, histogram);
	}

	@Override
	protected double getRawMinimum(ASDFile file)
	{
		return file.getMinResistanceDisplayed();
	}

	@Override
	protected double getRawMaximum(ASDFile file)
	{
		return file.getMaxResistanceDisplayed();
	}

	@Override
	protected double getRawValue(ASDFile file, int index)
	{
		return file.getAverageResistanceDisplayed(index, getAverage());
	}

	@Override
	protected void updateZoom(ASDFile file, double min, double max)
	{
		file.setMinResistancePercentage(min == -Double.MAX_VALUE ? file.getMinResistanceDisplayed() : min);
		file.setMaxResistancePercentage(max == +Double.MAX_VALUE ? file.getMaxResistanceDisplayed() : max);
	}

	@Override
	protected double getPercentage(ASDFile file)
	{
		return file.getResistancePercentage();
	}

	@Override
	protected double getMean(ASDFile file)
	{
		return file.getMeanResistance();
	}

	@Override
	protected double getStdev(ASDFile file)
	{
		return file.getStdevResistance();
	}

	@Override
	protected double getMedian(ASDFile file)
	{
		return file.getMedianResistance();
	}

	@Override
	protected double getRootMeanSquare(ASDFile file)
	{
		return file.getRootMeanSquareResistance();
	}

}
