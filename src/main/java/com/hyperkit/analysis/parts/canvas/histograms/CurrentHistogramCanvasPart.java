package com.hyperkit.analysis.parts.canvas.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.HistogramCanvasPart;

public class CurrentHistogramCanvasPart extends HistogramCanvasPart
{
	
	public CurrentHistogramCanvasPart()
	{
		super("Current histogram");
	}

	@Override
	protected double[][] getData(ASDFile file, int step)
	{
		return file.getCurrentDensity(step);
	}

	@Override
	protected boolean hasDomainMarkerValue(ASDFile file, int frame)
	{
		return frame < file.getLengthDisplayed();
	}

	@Override
	protected boolean hasRangeMarkerValue(ASDFile file, int frame)
	{
		return frame < file.getLengthDisplayed();
	}

	@Override
	protected double getDomainMarkerValue(ASDFile file, int frame)
	{
		return file.getCurrentDisplayed(frame);
	}

	@Override
	protected double getRangeMarkerValue(ASDFile file, int step, int frame)
	{
		return file.getCurrentProbability(step, frame);
	}

}
