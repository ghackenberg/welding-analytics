package com.hyperkit.analysis.parts.charts.histograms;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.charts.HistogramChartPart;

public class VoltageHistogramChartPart extends HistogramChartPart
{

	public VoltageHistogramChartPart()
	{
		super("Voltage histogram", "Voltage (in V)");
	}

	@Override
	protected double[][] getData(ASDFile file, int step)
	{
		return file.getVoltageDensity(step);
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
		return file.getVoltageDisplayed(frame);
	}

	@Override
	protected double getRangeMarkerValue(ASDFile file, int step, int frame)
	{
		return file.getVoltageProbability(step, frame);
	}

}
