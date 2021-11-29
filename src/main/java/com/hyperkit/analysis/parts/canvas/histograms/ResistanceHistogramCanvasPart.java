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
	protected void updatePercentage(ASDFile file, double min, double max)
	{
		file.setMinResistancePercentage(min == -Double.MAX_VALUE ? file.getMinResistanceDisplayed() : min);
		file.setMaxResistancePercentage(max == +Double.MAX_VALUE ? file.getMaxResistanceDisplayed() : max);
	}

}
