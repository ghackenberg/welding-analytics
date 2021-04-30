package com.hyperkit.analysis.parts.charts.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.charts.TimeseriesChartPart;

public class CurrentTimeseriesChartPart extends TimeseriesChartPart
{

	public CurrentTimeseriesChartPart()
	{
		super("Current timeseries", "Current (in A)");
	}

	@Override
	protected double[][] getData(ASDFile file, int frame, int window_backward)
	{
		return file.getCurrentTimeseries(frame, window_backward);
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
		return file.getTimestampDisplayed(frame);
	}

	@Override
	protected double getRangeMarkerValue(ASDFile file, int frame)
	{
		return file.getCurrentDisplayed(frame);
	}

}
