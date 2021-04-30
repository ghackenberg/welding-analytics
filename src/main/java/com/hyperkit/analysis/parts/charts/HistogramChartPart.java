package com.hyperkit.analysis.parts.charts;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.HistogramChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.ChartPart;

public abstract class HistogramChartPart extends ChartPart
{
	
	private int frame = 0;
	private int step = 100;
	
	private List<ASDFile> file_list = new ArrayList<>();
	private Map<String, ASDFile> file_map = new HashMap<>();
	private Map<String, ValueMarker> domain_marker_map = new HashMap<>();
	private Map<String, ValueMarker> range_marker_map = new HashMap<>();
	
	private String axis;
	private DefaultXYDataset dataset;
	
	public HistogramChartPart(String title, String axis)
	{
		super(title);
		
		this.axis = axis;
		
		// Steps
		
		JSpinner stepSpinner = new JSpinner(new SpinnerNumberModel(step, 100, 10000, 100));
		stepSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new HistogramChangeEvent((int) stepSpinner.getValue()));
			}
		);
		
		getToolBar().add(new JLabel("Histogram bins:"));
		getToolBar().add(stepSpinner);
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		return ChartFactory.createXYLineChart(getTitle(), axis, "Probability (in %)", dataset, PlotOrientation.VERTICAL, true, true, true);
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		ASDFile file = event.getASDFile();
		
		XYPlot plot = getChart().getXYPlot();
		
		plot.getRenderer().setSeriesPaint(dataset.getSeriesCount(), file.getColor());
		
		dataset.addSeries(event.getASDFile().getName(), getData(file, step));
		
		double domain_value = hasDomainMarkerValue(file, frame) ? getDomainMarkerValue(file, frame) : Double.MAX_VALUE;
		double range_value = hasRangeMarkerValue(file, frame) ? getRangeMarkerValue(file, step, frame) : Double.MAX_VALUE;
		
		ValueMarker domain = new ValueMarker(domain_value);
		domain.setPaint(file.getMarkerColor());
		domain.setStroke(new BasicStroke(1));
		
		ValueMarker range = new ValueMarker(range_value);
		range.setPaint(file.getMarkerColor());
		range.setStroke(new BasicStroke(1));
		range.setAlpha(0.5f);
		
		plot.addDomainMarker(domain);
		plot.addRangeMarker(range);
		
		file_list.add(event.getASDFile());
		file_map.put(event.getASDFile().getName(), event.getASDFile());
		
		domain_marker_map.put(event.getASDFile().getName(), domain);
		range_marker_map.put(event.getASDFile().getName(), range);
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName());
		
		getChart().getXYPlot().removeDomainMarker(domain_marker_map.get(event.getASDFile().getName()));
		getChart().getXYPlot().removeRangeMarker(range_marker_map.get(event.getASDFile().getName()));
		
		file_list.remove(event.getASDFile());
		file_map.remove(event.getASDFile().getName());
		
		domain_marker_map.remove(event.getASDFile().getName());
		range_marker_map.remove(event.getASDFile().getName());
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		this.frame = event.getValue();
		
		for (ASDFile file : file_list)
		{
			domain_marker_map.get(file.getName()).setValue(hasDomainMarkerValue(file, frame) ? getDomainMarkerValue(file, frame) : Double.MAX_VALUE);
			range_marker_map.get(file.getName()).setValue(hasRangeMarkerValue(file, frame) ? getRangeMarkerValue(file, step, frame) : Double.MAX_VALUE);
		}
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(HistogramChangeEvent event)
	{
		step = event.getValue();
		
		for (ASDFile file : file_list)
		{
			dataset.addSeries(file.getName(), getData(file, step));
		}
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), getData(event.getASDFile(), step));
		
		domain_marker_map.get(event.getASDFile().getName()).setValue(hasDomainMarkerValue(event.getASDFile(), frame) ? getDomainMarkerValue(event.getASDFile(), frame) : Double.MAX_VALUE);
		domain_marker_map.get(event.getASDFile().getName()).setPaint(event.getASDFile().getMarkerColor());
		
		range_marker_map.get(event.getASDFile().getName()).setValue(hasRangeMarkerValue(event.getASDFile(), frame) ? getRangeMarkerValue(event.getASDFile(), step, frame) : Double.MAX_VALUE);
		range_marker_map.get(event.getASDFile().getName()).setPaint(event.getASDFile().getMarkerColor());
		
		update();
		
		return true;
	}
	
	private void update()
	{
		XYPlot plot = getChart().getXYPlot();
		
		for (int series = 0; series < dataset.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset.getSeriesKey(series);
			
			ASDFile file = file_map.get(key);
			
			plot.getRenderer().setSeriesPaint(series, file.getColor());
		}
	}
	
	protected abstract double[][] getData(ASDFile file, int step);
	
	protected abstract boolean hasDomainMarkerValue(ASDFile file, int frame);
	protected abstract boolean hasRangeMarkerValue(ASDFile file, int frame);
	
	protected abstract double getDomainMarkerValue(ASDFile file, int frame);
	protected abstract double getRangeMarkerValue(ASDFile file, int step, int frame);
	
}
