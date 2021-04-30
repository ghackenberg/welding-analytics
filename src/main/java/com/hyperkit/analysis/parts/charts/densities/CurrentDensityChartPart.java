package com.hyperkit.analysis.parts.charts.densities;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.charts.DensityChartPart;

public class CurrentDensityChartPart extends DensityChartPart
{

	public CurrentDensityChartPart()
	{
		super("Current probability density function", "Current (in A)");
	}

	@Override
	protected double[][] getData(ASDFile file, int step)
	{
		return file.getCurrentDensity(step);
	}
	
}
