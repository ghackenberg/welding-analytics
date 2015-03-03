package com.hyperkit.analysis.parts.charts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.events.StepChangeEvent;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.ChartPart;

public class VoltageDensityChartPart extends ChartPart
{
	
	private int step;
	private Map<String, ASDFile> files;
	private DefaultXYDataset dataset;

	public VoltageDensityChartPart(int step)
	{
		super("Voltage probability density function");
		
		this.step = step;
		this.files = new HashMap<>();
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		return ChartFactory.createXYLineChart("Voltage probability density function", "Voltage (in V)", "Probability (in %)", dataset, PlotOrientation.VERTICAL, true, true, true);
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		getChart().getXYPlot().getRenderer().setSeriesPaint(dataset.getSeriesCount(), event.getASDFile().getColor());
		
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getVoltageDensity(step));
		
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
	public boolean handleEvent(StepChangeEvent event)
	{
		step = event.getStep();
		
		for (Entry<String, ASDFile> file : files.entrySet())
		{
			dataset.addSeries(file.getKey(), file.getValue().getVoltageDensity(step));
		}
		
		update();
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), event.getASDFile().getVoltageDensity(step));
		
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
