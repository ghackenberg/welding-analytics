package com.hyperkit.analysis.parts.charts;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.events.StepChangeEvent;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.ChartPart;

public class CurrentDensityChartPart extends ChartPart
{
	
	private int step;
	private List<ASDFile> files;
	private DefaultXYDataset dataset;

	public CurrentDensityChartPart(int step)
	{
		super("Current probability density function");
		
		this.step = step;
		this.files = new ArrayList<>();
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		return ChartFactory.createXYAreaChart("Current probability density function", "Current", "Probability", dataset, PlotOrientation.VERTICAL, true, true, true);
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getCurrentDensity(step));
		
		files.add(event.getASDFile());
		
		/*
		for (int i = 0; i < files.size(); i++)
		{
			getChart().getXYPlot().getRenderer().setSeriesPaint(i, files.get(i).getColor());
		}
		*/
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName());
		
		files.remove(event.getASDFile());
		
		/*
		for (int i = 0; i < files.size(); i++)
		{
			getChart().getXYPlot().getRenderer().setSeriesPaint(i, files.get(i).getColor());
		}
		*/
		
		return true;
	}
	public boolean handleEvent(StepChangeEvent event)
	{
		step = event.getStep();
		
		for (ASDFile file : files)
		{
			dataset.addSeries(file.getName(), file.getCurrentDensity(step));
		}
		
		return true;
	}

}
