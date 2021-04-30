package com.hyperkit.analysis.parts.charts.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.charts.TimeseriesChartPart;

public class VoltageTimeseriesChartPart extends TimeseriesChartPart
{

	public VoltageTimeseriesChartPart()
	{
		super("Voltage timeseries", "Voltage (in V)");
	}

	@Override
	protected double[][] getData(ASDFile file)
	{
		return file.getVoltageTimeseries();
	}

}
