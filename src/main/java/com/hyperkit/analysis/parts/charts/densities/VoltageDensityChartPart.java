package com.hyperkit.analysis.parts.charts.densities;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.charts.DensityChartPart;

public class VoltageDensityChartPart extends DensityChartPart
{

	public VoltageDensityChartPart()
	{
		super("Voltage probability density function", "Voltage (in V)");
	}

	@Override
	protected double[][] getData(ASDFile file, int step)
	{
		return file.getVoltageDensity(step);
	}

}
