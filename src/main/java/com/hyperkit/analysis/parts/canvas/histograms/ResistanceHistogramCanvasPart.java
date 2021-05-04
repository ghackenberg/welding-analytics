package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class ResistanceHistogramCanvasPart extends HistogramCanvasPart
{
	
	public ResistanceHistogramCanvasPart()
	{
		super("Resistance histogram", "Resistance (in V/A)");
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
		return file.getResistanceDisplayed(index);
	}

}
