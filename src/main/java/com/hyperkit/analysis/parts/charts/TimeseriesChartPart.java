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
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.WindowChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.ChartPart;

public abstract class TimeseriesChartPart extends ChartPart
{
	
	private int frame = 0;
	private int window = 5000;
	private List<ASDFile> file_list = new ArrayList<>();
	private Map<String, ASDFile> file_map = new HashMap<>();
	private Map<String, ValueMarker> domain_marker_map = new HashMap<>();
	private Map<String, ValueMarker> range_marker_map = new HashMap<>();

	private String axis;
	private DefaultXYDataset dataset;

	public TimeseriesChartPart(String title, String axis)
	{
		super(title);
		
		this.axis = axis;
		
		// Point
		
		JSpinner windowSpinner = new JSpinner(new SpinnerNumberModel(window, 100, 100000, 100));
		windowSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new WindowChangeEvent((int) windowSpinner.getValue()));
			}
		);
		
		getToolBar().add(new JLabel("Point count:"));
		getToolBar().add(windowSpinner);
	}

	@Override
	protected JFreeChart createChart()
	{	
		dataset = new DefaultXYDataset();
		
		JFreeChart chart = ChartFactory.createXYLineChart(getTitle(), "Time (in s)", axis, dataset, PlotOrientation.VERTICAL, true, true, true);
		
		chart.getXYPlot().setRenderer(new SamplingXYLineRenderer());
		
		return chart;
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		getChart().getXYPlot().getRenderer().setSeriesPaint(dataset.getSeriesCount(), event.getASDFile().getColor());
		
		dataset.addSeries(event.getASDFile().getName(), getData(event.getASDFile(), frame, window));
		
		file_list.add(event.getASDFile());
		file_map.put(event.getASDFile().getName(), event.getASDFile());
		
		double domain_value = hasDomainMarkerValue(event.getASDFile(), frame) ? getDomainMarkerValue(event.getASDFile(), frame) : Double.MAX_VALUE;
		double range_value = hasRangeMarkerValue(event.getASDFile(), frame) ? getRangeMarkerValue(event.getASDFile(), frame) : Double.MAX_VALUE;
		
		ValueMarker domain_marker = new ValueMarker(domain_value);
		domain_marker.setPaint(event.getASDFile().getMarkerColor());
		domain_marker.setStroke(new BasicStroke(1));
		
		ValueMarker range_marker = new ValueMarker(range_value);
		range_marker.setPaint(event.getASDFile().getMarkerColor());
		range_marker.setStroke(new BasicStroke(1));
		
		getChart().getXYPlot().addDomainMarker(domain_marker);
		getChart().getXYPlot().addRangeMarker(range_marker);
		
		domain_marker_map.put(event.getASDFile().getName(), domain_marker);
		range_marker_map.put(event.getASDFile().getName(), range_marker);
		
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
	
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), getData(event.getASDFile(), frame, window));
		
		domain_marker_map.get(event.getASDFile().getName()).setValue(hasDomainMarkerValue(event.getASDFile(), frame) ? getDomainMarkerValue(event.getASDFile(), frame) : Double.MAX_VALUE);
		domain_marker_map.get(event.getASDFile().getName()).setPaint(event.getASDFile().getMarkerColor());

		range_marker_map.get(event.getASDFile().getName()).setValue(hasRangeMarkerValue(event.getASDFile(), frame) ? getRangeMarkerValue(event.getASDFile(), frame) : Double.MAX_VALUE);
		range_marker_map.get(event.getASDFile().getName()).setPaint(event.getASDFile().getMarkerColor());
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		this.frame = event.getValue();
		
		for (ASDFile file : file_list)
		{
			dataset.addSeries(file.getName(), getData(file, frame, window));
			
			domain_marker_map.get(file.getName()).setValue(hasDomainMarkerValue(file, frame) ? getDomainMarkerValue(file, frame) : Double.MAX_VALUE);
			range_marker_map.get(file.getName()).setValue(hasRangeMarkerValue(file, frame) ? getRangeMarkerValue(file, frame) : Double.MAX_VALUE);
		}
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(WindowChangeEvent event)
	{
		this.window = event.getValue();
		
		for (ASDFile file : file_list)
		{
			dataset.addSeries(file.getName(), getData(file, frame, window));
		}
		
		update();
		
		return true;
	}
	
	private void update()
	{
		for (int series = 0; series < dataset.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset.getSeriesKey(series);
			
			getChart().getXYPlot().getRenderer().setSeriesPaint(series, file_map.get(key).getColor());
		}
	}
	
	protected abstract double[][] getData(ASDFile file, int frame, int window_backward);
	
	protected abstract boolean hasDomainMarkerValue(ASDFile file, int frame);
	protected abstract boolean hasRangeMarkerValue(ASDFile file, int frame);
	
	protected abstract double getDomainMarkerValue(ASDFile file, int frame);
	protected abstract double getRangeMarkerValue(ASDFile file, int frame);

}
