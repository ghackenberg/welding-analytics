package com.hyperkit.analysis.parts.charts;

import java.util.HashMap;
import java.util.Map;

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
	
	private Map<String, ASDFile> files;
	private DefaultXYDataset dataset;

	public VoltageTimeseriesChartPart()
	{
		super("Voltage timeseries");
		
		this.files = new HashMap<>();
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
		getChart().getXYPlot().getRenderer().setSeriesPaint(dataset.getSeriesCount(), event.getASDFile().getColor());
		
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getVoltageTimeseries());
		
		files.put(event.getASDFile().getName(), event.getASDFile());
		
		update();
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName());
		
		files.remove(event.getASDFile());
		
		update();
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getVoltageTimeseries());
		
		update();
		
		return true;
	}
	
	private void update()
	{
		for (int series = 0; series < dataset.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset.getSeriesKey(series);
			
			getChart().getXYPlot().getRenderer().setSeriesPaint(series, files.get(key).getColor());
		}
	}

}
