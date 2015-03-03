package com.hyperkit.analysis.parts.charts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
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

public class PointCloudStatisticalChartPart extends ChartPart
{
	
	private int step;
	private List<ASDFile> files;
	private DefaultXYDataset dataset;

	public PointCloudStatisticalChartPart(int step)
	{
		super("Point cloud");
		
		this.step = step;
		this.files = new ArrayList<>();
	}

	@Override
	protected JFreeChart createChart()
	{
		dataset = new DefaultXYDataset();
		
		return ChartFactory.createXYLineChart("Point cloud (statistical)", "Current (in A)", "Voltage (in V)", dataset, PlotOrientation.VERTICAL, true, true, true);
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		dataset.addSeries(event.getASDFile().getName() + " (Minimum)", event.getASDFile().getCurrentVoltageMin(step));
		dataset.addSeries(event.getASDFile().getName() + " (Maximum)", event.getASDFile().getCurrentVoltageMax(step));
		dataset.addSeries(event.getASDFile().getName() + " (Average)", event.getASDFile().getCurrentVoltageAvg(step));
		dataset.addSeries(event.getASDFile().getName() + " (Regression)", getRegressionData(event.getASDFile()));
		
		files.add(event.getASDFile());
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName() + " (Minimum)");
		dataset.removeSeries(event.getASDFile().getName() + " (Maximum)");
		dataset.removeSeries(event.getASDFile().getName() + " (Average)");
		dataset.removeSeries(event.getASDFile().getName() + " (Regression)");
		
		files.remove(event.getASDFile());
		
		return true;
	}
	public boolean handleEvent(StepChangeEvent event)
	{
		step = event.getStep();
		
		for (ASDFile file : files)
		{
			dataset.addSeries(file.getName() + " (Minimum)", file.getCurrentVoltageMin(step));
			dataset.addSeries(file.getName() + " (Maximum)", file.getCurrentVoltageMax(step));
			dataset.addSeries(file.getName() + " (Average)", file.getCurrentVoltageAvg(step));
			dataset.addSeries(file.getName() + " (Regression)", getRegressionData(file));
		}
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.addSeries(event.getASDFile().getName() + " (Minimum)", event.getASDFile().getCurrentVoltageMin(step));
		dataset.addSeries(event.getASDFile().getName() + " (Maximum)", event.getASDFile().getCurrentVoltageMax(step));
		dataset.addSeries(event.getASDFile().getName() + " (Average)", event.getASDFile().getCurrentVoltageAvg(step));
		dataset.addSeries(event.getASDFile().getName() + " (Regression)", getRegressionData(event.getASDFile()));
		
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

}
