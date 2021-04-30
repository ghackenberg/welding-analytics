package com.hyperkit.analysis.parts.charts.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.charts.HistogramChartPart;

public class CurrentHistogramChartPart extends HistogramChartPart
{

	public CurrentHistogramChartPart()
	{
		super("Current histogram", "Current (in A)");
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
