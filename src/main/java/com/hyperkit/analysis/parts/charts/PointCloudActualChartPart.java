package com.hyperkit.analysis.parts.charts;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.adapters.FileXYSeriesLabelGenerator;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.helpers.StatisticsHelper;
import com.hyperkit.analysis.parts.ChartPart;

public class PointCloudActualChartPart extends ChartPart
{

	private List<ASDFile> file_list;
	private Map<String, ASDFile> file_map;
	private DefaultXYDataset dataset_points;
	private DefaultXYDataset dataset_lines;

	public PointCloudActualChartPart()
	{
		super("Point cloud (actual)");
		
		file_list = new ArrayList<>();
		file_map = new HashMap<>();
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
		
		dataset_points.addSeries(event.getASDFile().getName() + " (Points)", event.getASDFile().getCurrentVoltage());
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
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset_points.addSeries(event.getASDFile().getName() + " (Points)", event.getASDFile().getCurrentVoltage());
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
	}
	
}
