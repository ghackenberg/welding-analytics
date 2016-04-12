package com.hyperkit.analysis.parts.charts;

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
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
	/*
	private Map<ASDFile, ValueMarker> minMarkers;
	private Map<ASDFile, ValueMarker> maxMarkers;
	*/
	private Map<ASDFile, IntervalMarker> markers;
	private DefaultXYDataset dataset;

	public VoltageDensityChartPart(int step)
	{
		super("Voltage probability density function");
		
		this.step = step;
		this.files = new HashMap<>();
		/*
		this.minMarkers = new HashMap<>();
		this.maxMarkers = new HashMap<>();
		*/
		this.markers = new HashMap<>();
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		return ChartFactory.createXYLineChart("Voltage probability density function", "Voltage (in V)", "Probability (in %)", dataset, PlotOrientation.VERTICAL, true, true, true);
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		ASDFile file = event.getASDFile();
		
		XYPlot plot = getChart().getXYPlot();
		
		plot.getRenderer().setSeriesPaint(dataset.getSeriesCount(), file.getColor());
		
		/*
		plot.addDomainMarker(getMinMarker(file));
		plot.addDomainMarker(getMaxMarker(file));
		*/
		plot.addDomainMarker(getMarker(file));
		
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
		XYPlot plot = getChart().getXYPlot();
		
		for (int series = 0; series < dataset.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset.getSeriesKey(series);
			
			ASDFile file = files.get(key);
			
			plot.getRenderer().setSeriesPaint(series, file.getColor());
			
			/*
			getMinMarker(file).setValue(file.getMinVoltagePercentage());
			getMaxMarker(file).setValue(file.getMaxVoltagePercentage());
			*/
			
			getMarker(file).setStartValue(file.getMinVoltagePercentage());
			getMarker(file).setEndValue(file.getMaxVoltagePercentage());
		}
	}
	
	/*
	private ValueMarker getMinMarker(ASDFile file)
	{
		if (!minMarkers.containsKey(file))
		{
			ValueMarker marker = new ValueMarker(file.getMinVoltagePercentage(), file.getColor(), new BasicStroke(1));
			
			marker.setLabel("Min ");
			marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
			marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
			marker.setLabelPaint(file.getColor());
			
			minMarkers.put(file, marker);
		}
		return minMarkers.get(file);
	}
	
	private ValueMarker getMaxMarker(ASDFile file)
	{
		if (!maxMarkers.containsKey(file))
		{
			ValueMarker marker = new ValueMarker(file.getMaxVoltagePercentage(), file.getColor(), new BasicStroke(1));
			
			marker.setLabel(" Max");
			marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
			marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
			marker.setLabelPaint(file.getColor());
			
			maxMarkers.put(file, marker);
		}
		return maxMarkers.get(file);
	}
	*/
	
	private IntervalMarker getMarker(ASDFile file)
	{
		if (!markers.containsKey(file))
		{
			IntervalMarker marker = new IntervalMarker(file.getMinVoltagePercentage(), file.getMaxVoltagePercentage());
			
			marker.setOutlinePaint(file.getColor());
			marker.setOutlineStroke(new BasicStroke(1));
			marker.setPaint(file.getColor());
			marker.setStroke(new BasicStroke(1));
			marker.setAlpha(0.1f);
			
			markers.put(file, marker);
		}
		return markers.get(file);
	}

}
