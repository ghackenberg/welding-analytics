package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class CurrentHistogramCanvasPart extends HistogramCanvasPart
{
	
	public CurrentHistogramCanvasPart()
	{
		super("Current histogram", "Current (in A)");
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
		return file.getCurrentDisplayed(index);
	}

}
