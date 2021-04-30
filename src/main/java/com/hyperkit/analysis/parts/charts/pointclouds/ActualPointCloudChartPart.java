package com.hyperkit.analysis.parts.charts.pointclouds;

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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.adapters.FileXYSeriesLabelGenerator;
import com.hyperkit.analysis.events.AnimationChangeEvent;
import com.hyperkit.analysis.events.PointChangeEvent;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.helpers.StatisticsHelper;
import com.hyperkit.analysis.parts.ChartPart;

public class ActualPointCloudChartPart extends ChartPart
{

	private List<ASDFile> file_list = new ArrayList<>();
	private Map<String, ASDFile> file_map = new HashMap<>();
	
	private int point = 1000;
	private int progress = 0;
	
	private DefaultXYDataset dataset_points;
	private DefaultXYDataset dataset_lines;

	public ActualPointCloudChartPart()
	{
		super("Point cloud (actual)");
		
		JSpinner pointSpinner = new JSpinner(new SpinnerNumberModel(point, 100, 100000, 100));
		pointSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new PointChangeEvent((int) pointSpinner.getValue()));
			}
		);
		
		JSpinner progressSpinner = new JSpinner(new SpinnerNumberModel(progress, 0, Integer.MAX_VALUE, 1));
		progressSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new AnimationChangeEvent((int) progressSpinner.getValue()));
			}
		);
		
		getToolBar().add(new JLabel("Point count:"));
		getToolBar().add(pointSpinner);
		getToolBar().add(new JLabel("Frame number:"));
		getToolBar().add(progressSpinner);
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset_points = new DefaultXYDataset();
		dataset_lines = new DefaultXYDataset();
		
		JFreeChart chart = ChartFactory.createScatterPlot("Point cloud (actual)", "Current (in A)", "Voltage (in V)", null, PlotOrientation.VERTICAL, true, true, true);
		
		XYPlot plot = chart.getXYPlot();
		
		plot.setDataset(0, dataset_points);
		plot.setDataset(1, dataset_lines);
		
		plot.setRenderer(0, new XYLineAndShapeRenderer(false, true));
		plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
		
		plot.mapDatasetToDomainAxis(0, 0);
		plot.mapDatasetToDomainAxis(1, 0);
		
		plot.mapDatasetToRangeAxis(0, 0);
		plot.mapDatasetToRangeAxis(1, 0);
		
		((NumberAxis) plot.getRangeAxis()).setAutoRange(false);
		((NumberAxis) plot.getDomainAxis()).setAutoRange(false);
		
		return chart;
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		getChart().getXYPlot().getRenderer(0).setSeriesPaint(dataset_points.getSeriesCount(), event.getASDFile().getColor());
		getChart().getXYPlot().getRenderer(1).setSeriesPaint(dataset_lines.getSeriesCount(), event.getASDFile().getColor());
		
		getChart().getXYPlot().getRenderer(0).setSeriesStroke(dataset_points.getSeriesCount(), new BasicStroke(1.0f));
		getChart().getXYPlot().getRenderer(1).setSeriesStroke(dataset_lines.getSeriesCount(), new BasicStroke(3.0f));
	
		getChart().getXYPlot().getRenderer(0).setSeriesVisibleInLegend(dataset_points.getSeriesCount(), false);
		getChart().getXYPlot().getRenderer(1).setSeriesVisibleInLegend(dataset_lines.getSeriesCount(), true);
		
		file_list.add(event.getASDFile());
		
		file_map.put(event.getASDFile().getName() + " (Points)", event.getASDFile());
		file_map.put(event.getASDFile().getName() + " (Regression)", event.getASDFile());
		
		dataset_points.addSeries(event.getASDFile().getName() + " (Points)", event.getASDFile().getCurrentVoltage(point, progress));
		dataset_lines.addSeries(event.getASDFile().getName() + " (Regression)", StatisticsHelper.getRegressionData(event.getASDFile()));
		
		update();
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset_points.removeSeries(event.getASDFile().getName() + " (Points)");
		dataset_lines.removeSeries(event.getASDFile().getName() + " (Regression)");
		
		file_list.remove(event.getASDFile());
		
		file_map.remove(event.getASDFile() + " (Points)");
		file_map.remove(event.getASDFile() + " (Regression)");
		
		update();
		
		return true;
	}
	public boolean handleEvent(PointChangeEvent event)
	{
		// Update diagram series
		
		point = event.getPoint();
		
		for (ASDFile file : file_list)
		{
			dataset_points.addSeries(file.getName() + " (Points)", file.getCurrentVoltage(point, progress));
		}
		
		// Update colors
		
		update();
		
		// Return true
		
		return true;
	}
	public boolean handleEvent(AnimationChangeEvent event)
	{
		// Update diagram series

		progress = event.getProgress();
		
		for (ASDFile file : file_list)
		{
			dataset_points.addSeries(file.getName() + " (Points)", file.getCurrentVoltage(point, progress));
		}
		
		// Update colors
		
		update();
		
		// Return true
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset_points.addSeries(event.getASDFile().getName() + " (Points)", event.getASDFile().getCurrentVoltage(point, progress));
		dataset_lines.addSeries(event.getASDFile().getName() + " (Regression)", StatisticsHelper.getRegressionData(event.getASDFile()));
		
		update();
		
		return true;
	}

	protected void update()
	{
		// Update colors
		
		for (int series = 0; series < dataset_points.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset_points.getSeriesKey(series);
			
			getChart().getXYPlot().getRenderer(0).setSeriesPaint(series, file_map.get(key).getColor());
			getChart().getXYPlot().getRenderer(0).setSeriesStroke(series, new BasicStroke(1f));
			
			getChart().getXYPlot().getRenderer(0).setSeriesVisibleInLegend(series, false);
		}
		
		for (int series = 0; series < dataset_lines.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset_lines.getSeriesKey(series);
			
			getChart().getXYPlot().getRenderer(1).setSeriesPaint(series, file_map.get(key).getColor());
			getChart().getXYPlot().getRenderer(1).setSeriesStroke(series, new BasicStroke(3f));
			
			getChart().getXYPlot().getRenderer(1).setSeriesVisibleInLegend(series, true);
		}
		
		// Update label generator
		
		getChart().getXYPlot().getRenderer(0).setLegendItemLabelGenerator(new FileXYSeriesLabelGenerator(file_map));
		getChart().getXYPlot().getRenderer(1).setLegendItemLabelGenerator(new FileXYSeriesLabelGenerator(file_map));
		
		// Update axes ranges
		
		double range_lower = +Double.MAX_VALUE;
		double range_upper = -Double.MAX_VALUE;
		
		double domain_lower = +Double.MAX_VALUE;
		double domain_upper = -Double.MAX_VALUE;
		
		for (ASDFile file : file_list) {
			range_lower = Math.min(range_lower, file.getMinVoltageDisplayed());
			range_upper = Math.max(range_upper, file.getMaxVoltageDisplayed());
			
			domain_lower = Math.min(domain_lower, file.getMinCurrentDisplayed());
			domain_upper = Math.max(domain_upper, file.getMaxCurrentDisplayed());
		}
		
		double range_delta = range_upper - range_lower;
		double domain_delta = domain_upper - domain_lower;
		
		getChart().getXYPlot().getRangeAxis().setLowerBound(range_lower - range_delta * 0.1);
		getChart().getXYPlot().getRangeAxis().setUpperBound(range_upper + range_delta * 0.1);
		
		getChart().getXYPlot().getDomainAxis().setLowerBound(domain_lower - domain_delta * 0.1);
		getChart().getXYPlot().getDomainAxis().setUpperBound(domain_upper + domain_delta * 0.1);
	}
	
}
