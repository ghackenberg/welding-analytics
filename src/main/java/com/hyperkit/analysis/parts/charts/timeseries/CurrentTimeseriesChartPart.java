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
	protected double[][] getData(ASDFile file)
	{
		return file.getCurrentTimeseries();
	}

}
