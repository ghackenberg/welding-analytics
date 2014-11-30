package com.hyperkit.analysis.parts.charts;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.ChartPart;

public class VoltageTimeseriesChartPart extends ChartPart
{
	
	private List<ASDFile> files;
	private DefaultXYDataset dataset;

	public VoltageTimeseriesChartPart()
	{
		super("Voltage timeseries");
		
		this.files = new ArrayList<>();
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		JFreeChart chart = ChartFactory.createXYLineChart("Voltage timeseries", "Time (in s)", "Voltage (in V)", dataset, PlotOrientation.VERTICAL, true, true, true);
		
		chart.getXYPlot().setRenderer(new SamplingXYLineRenderer());
		
		return chart;
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getVoltageTimeseries());
		
		files.add(event.getASDFile());
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName());
		
		files.remove(event.getASDFile());
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName());
		
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getVoltageTimeseries());
		
		return true;
	}

}
