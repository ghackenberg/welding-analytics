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

public class VoltageTimeseriesChartPart extends ChartPart
{
	
	private int step;
	private List<ASDFile> files;
	private DefaultXYDataset dataset;

	public VoltageTimeseriesChartPart(int step)
	{
		super("Voltage timeseries");
		
		this.step = step;
		this.files = new ArrayList<>();
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		return ChartFactory.createXYLineChart("Voltage timeseries", "Time", "Voltage", dataset, PlotOrientation.VERTICAL, true, true, true);
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getVoltageTimeseries(step));
		
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
			dataset.addSeries(file.getName(), file.getVoltageTimeseries(step));
		}
		
		return true;
	}

}
