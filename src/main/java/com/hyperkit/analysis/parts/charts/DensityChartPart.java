package com.hyperkit.analysis.parts.charts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

import com.hyperkit.analysis.events.StepChangeEvent;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.ChartPart;

public abstract class DensityChartPart extends ChartPart
{
	
	protected static final int STEP_INIT = 100;
	protected static final int STEP_MIN = 100;
	protected static final int STEP_MAX = 10000;
	protected static final int STEP_SIZE = 100;
	
	private String axis;
	private int step;
	private Map<String, ASDFile> files = new HashMap<>();
	private DefaultXYDataset dataset;
	
	public DensityChartPart(String title, String axis)
	{
		super(title);
		
		this.axis = axis;
		this.step = STEP_INIT;
		
		// Steps
		
		JSpinner stepSpinner = new JSpinner(new SpinnerNumberModel(STEP_INIT, STEP_MIN, STEP_MAX, STEP_SIZE));
		stepSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new StepChangeEvent((int) stepSpinner.getValue()));
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
		
		files.put(event.getASDFile().getName(), event.getASDFile());
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		dataset.removeSeries(event.getASDFile().getName());
		
		files.remove(event.getASDFile().getName());
		
		update();
		
		return true;
	}
	public boolean handleEvent(StepChangeEvent event)
	{
		step = event.getStep();
		
		for (Entry<String, ASDFile> file : files.entrySet())
		{
			dataset.addSeries(file.getKey(), getData(file.getValue(), step));
		}
		
		update();
		
		return true;
	}
	
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		dataset.addSeries(event.getASDFile().getName(), getData(event.getASDFile(), step));
		
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
		}
	}
	
	protected abstract double[][] getData(ASDFile file, int step);
	
}
