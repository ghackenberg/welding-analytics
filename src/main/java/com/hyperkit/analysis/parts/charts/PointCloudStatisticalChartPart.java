package com.hyperkit.analysis.parts.charts;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.events.StepChangeEvent;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.ChartPart;

public class PointCloudStatisticalChartPart extends ChartPart
{
	
	private int step;
	private List<ASDFile> file_list;
	private Map<String, ASDFile> file_map;
	private DefaultXYDataset dataset;

	public PointCloudStatisticalChartPart(int step)
	{
		super("Point cloud (statistical)");
		
		this.step = step;
		this.file_list = new ArrayList<>();
		this.file_map = new HashMap<>();
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		return ChartFactory.createXYLineChart("Point cloud (statistical)", "Current (in A)", "Voltage (in V)", dataset, PlotOrientation.VERTICAL, true, true, true);
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		for (int series = dataset.getSeriesCount(); series < dataset.getSeriesCount() + 4; series++)
		{
			getChart().getXYPlot().getRenderer().setSeriesPaint(series, event.getASDFile().getColor());
		}
		
		dataset.addSeries(event.getASDFile().getName() + " (Minimum)", event.getASDFile().getCurrentVoltageMin(step));
		dataset.addSeries(event.getASDFile().getName() + " (Maximum)", event.getASDFile().getCurrentVoltageMax(step));
		dataset.addSeries(event.getASDFile().getName() + " (Average)", event.getASDFile().getCurrentVoltageAvg(step));
		dataset.addSeries(event.getASDFile().getName() + " (Regression)", getRegressionData(event.getASDFile()));
		
		file_list.add(event.getASDFile());
		
		file_map.put(event.getASDFile().getName() + " (Minimum)", event.getASDFile());
		file_map.put(event.getASDFile().getName() + " (Maximum)", event.getASDFile());
		file_map.put(event.getASDFile().getName() + " (Average)", event.getASDFile());
		file_map.put(event.getASDFile().getName() + " (Regression)", event.getASDFile());
		
		update();
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName() + " (Minimum)");
		dataset.removeSeries(event.getASDFile().getName() + " (Maximum)");
		dataset.removeSeries(event.getASDFile().getName() + " (Average)");
		dataset.removeSeries(event.getASDFile().getName() + " (Regression)");
		
		file_list.remove(event.getASDFile());
		
		file_map.remove(event.getASDFile() + " (Minimum)");
		file_map.remove(event.getASDFile() + " (Maximum)");
		file_map.remove(event.getASDFile() + " (Average)");
		file_map.remove(event.getASDFile() + " (Regression)");
		
		update();
		
		return true;
	}
	public boolean handleEvent(StepChangeEvent event)
	{
		step = event.getStep();
		
		for (ASDFile file : file_list)
		{
			dataset.addSeries(file.getName() + " (Minimum)", file.getCurrentVoltageMin(step));
			dataset.addSeries(file.getName() + " (Maximum)", file.getCurrentVoltageMax(step));
			dataset.addSeries(file.getName() + " (Average)", file.getCurrentVoltageAvg(step));
			dataset.addSeries(file.getName() + " (Regression)", getRegressionData(file));
		}
		
		update();
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.addSeries(event.getASDFile().getName() + " (Minimum)", event.getASDFile().getCurrentVoltageMin(step));
		dataset.addSeries(event.getASDFile().getName() + " (Maximum)", event.getASDFile().getCurrentVoltageMax(step));
		dataset.addSeries(event.getASDFile().getName() + " (Average)", event.getASDFile().getCurrentVoltageAvg(step));
		dataset.addSeries(event.getASDFile().getName() + " (Regression)", getRegressionData(event.getASDFile()));
		
		update();
		
		return true;
	}
	
	private double[][] getRegressionData(ASDFile file)
	{
		SimpleRegression regression = file.getRegression();
		
		double[][] data = new double[2][2];
		
		data[0][0] = file.getMinCurrentDisplayed();
		data[1][0] = regression.predict(file.getMinCurrentDisplayed());
		
		data[0][1] = file.getMaxCurrentDisplayed();
		data[1][1] = regression.predict(file.getMaxCurrentDisplayed());
		
		return data;
	}

	private void update()
	{
		for (int series = 0; series < dataset.getSeriesCount(); series++)
		{
			Comparable<?> key = dataset.getSeriesKey(series);
			
			getChart().getXYPlot().getRenderer().setSeriesPaint(series, file_map.get(key).getColor());
			
			if (((String) key).endsWith("(Regression)"))
			{
				getChart().getXYPlot().getRenderer().setSeriesStroke(series, new BasicStroke(3f));
			}
			else
			{
				getChart().getXYPlot().getRenderer().setSeriesStroke(series, new BasicStroke(1f));
			}
		}
		
		getChart().getXYPlot().clearAnnotations();
		
		for (ASDFile file : file_list)
		{
			SimpleRegression regression = file.getRegression();
			
			String text = "y = " + regression.getIntercept() + " + " + regression.getSlope() + " * x";
			double x = (file.getMaxCurrentDisplayed() + file.getMinCurrentDisplayed()) / 2;
			double y = regression.predict(x);
			
			XYTextAnnotation annotation = new XYTextAnnotation(text, x, y);
			annotation.setPaint(file.getColor());
			
			getChart().getXYPlot().addAnnotation(annotation);
		}
	}

}
