package com.hyperkit.analysis.parts.charts;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.adapters.FileXYSeriesLabelGenerator;
import com.hyperkit.analysis.events.StepChangeEvent;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.helpers.StatisticsHelper;
import com.hyperkit.analysis.parts.ChartPart;

public class PointCloudStatisticalChartPart extends ChartPart
{
	
	private List<ASDFile> file_list;
	private Map<String, ASDFile> file_map;
	private DefaultXYDataset dataset;
	private int step;

	public PointCloudStatisticalChartPart(int step)
	{
		super("Point cloud (statistical)");
		
		file_list = new ArrayList<>();
		file_map = new HashMap<>();
		
		this.step = step;
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		JFreeChart chart = ChartFactory.createXYLineChart("Point cloud (statistical)", "Current (in A)", "Voltage (in V)", dataset, PlotOrientation.VERTICAL, true, true, true);
		
		((NumberAxis) chart.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);
		((NumberAxis) chart.getXYPlot().getDomainAxis()).setAutoRangeIncludesZero(false);
		
		return chart;
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		// Initialize render options
		
		int count = getChart().getXYPlot().getSeriesCount();
		
		for (int series = 0; series < 4; series++)
		{
			getChart().getXYPlot().getRenderer().setSeriesPaint(count + series, event.getASDFile().getColor());
			
			if (series == 3)
			{
				getChart().getXYPlot().getRenderer().setSeriesVisibleInLegend(count + series, true);
			}
			else
			{
				getChart().getXYPlot().getRenderer().setSeriesVisibleInLegend(count + series, false);
			}
		}
		
		// Register file internally
		
		file_list.add(event.getASDFile());
		
		file_map.put(event.getASDFile().getName() + " (Minimum)", event.getASDFile());
		file_map.put(event.getASDFile().getName() + " (Maximum)", event.getASDFile());
		file_map.put(event.getASDFile().getName() + " (Average)", event.getASDFile());
		file_map.put(event.getASDFile().getName() + " (Regression)", event.getASDFile());
		
		// Add different series
		
		dataset.addSeries(event.getASDFile().getName() + " (Minimum)", event.getASDFile().getCurrentVoltageMin(step));
		dataset.addSeries(event.getASDFile().getName() + " (Maximum)", event.getASDFile().getCurrentVoltageMax(step));
		dataset.addSeries(event.getASDFile().getName() + " (Average)", event.getASDFile().getCurrentVoltageAvg(step));
		dataset.addSeries(event.getASDFile().getName() + " (Regression)", StatisticsHelper.getRegressionData(event.getASDFile()));
		
		// Update colors
		
		update();
		
		// Return true
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		// Remove series from chart
		
		dataset.removeSeries(event.getASDFile().getName() + " (Minimum)");
		dataset.removeSeries(event.getASDFile().getName() + " (Maximum)");
		dataset.removeSeries(event.getASDFile().getName() + " (Average)");
		dataset.removeSeries(event.getASDFile().getName() + " (Regression)");
		
		// Unregister series internally
		
		file_list.remove(event.getASDFile());
		
		file_map.remove(event.getASDFile() + " (Minimum)");
		file_map.remove(event.getASDFile() + " (Maximum)");
		file_map.remove(event.getASDFile() + " (Average)");
		file_map.remove(event.getASDFile() + " (Regression)");
		
		// Update colors
		
		update();
		
		// Return true
		
		return true;
	}
	public boolean handleEvent(StepChangeEvent event)
	{
		// Update diagram series
		
		step = event.getStep();
		
		for (ASDFile file : file_list)
		{
			dataset.addSeries(file.getName() + " (Minimum)", file.getCurrentVoltageMin(step));
			dataset.addSeries(file.getName() + " (Maximum)", file.getCurrentVoltageMax(step));
			dataset.addSeries(file.getName() + " (Average)", file.getCurrentVoltageAvg(step));
			dataset.addSeries(file.getName() + " (Regression)", StatisticsHelper.getRegressionData(file));
		}
		
		// Update colors
		
		update();
		
		// Return true
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		// Update diagram series
		
		dataset.addSeries(event.getASDFile().getName() + " (Minimum)", event.getASDFile().getCurrentVoltageMin(step));
		dataset.addSeries(event.getASDFile().getName() + " (Maximum)", event.getASDFile().getCurrentVoltageMax(step));
		dataset.addSeries(event.getASDFile().getName() + " (Average)", event.getASDFile().getCurrentVoltageAvg(step));
		dataset.addSeries(event.getASDFile().getName() + " (Regression)", StatisticsHelper.getRegressionData(event.getASDFile()));
		
		// Update colors
		
		update();
		
		// Return true
		
		return true;
	}

	protected void update()
	{
		// Update colors
		
		for (int series = 0; series < dataset.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset.getSeriesKey(series);
			
			getChart().getXYPlot().getRenderer().setSeriesPaint(series, file_map.get(key).getColor());
			
			if (((String) key).endsWith("(Regression)"))
			{
				getChart().getXYPlot().getRenderer().setSeriesStroke(series, new BasicStroke(3f));
				
				getChart().getXYPlot().getRenderer().setSeriesVisibleInLegend(series, true);
			}
			else
			{
				getChart().getXYPlot().getRenderer().setSeriesStroke(series, new BasicStroke(1f));
				
				getChart().getXYPlot().getRenderer().setSeriesVisibleInLegend(series, false);
			}
		}
		
		// Update label generator
		
		getChart().getXYPlot().getRenderer().setLegendItemLabelGenerator(new FileXYSeriesLabelGenerator(file_map));
	}

}
