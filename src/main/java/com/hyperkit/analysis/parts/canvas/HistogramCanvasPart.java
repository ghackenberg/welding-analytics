package com.hyperkit.analysis.parts.canvas;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.HistogramChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class HistogramCanvasPart extends CanvasPart
{

	private int frame = 0;
	private int histogram = 100;
	private Map<ASDFile, Double> mins = new HashMap<>();
	private Map<ASDFile, Double> maxs = new HashMap<>();
	private Map<ASDFile, Double> deltas = new HashMap<>();
	private Map<ASDFile, double[][]> series = new HashMap<>();
	
	public HistogramCanvasPart(String title)
	{
		super(title);
		
		// Point
		
		JSpinner histogramSpinner = new JSpinner(new SpinnerNumberModel(histogram, 100, 1000, 100));
		histogramSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new HistogramChangeEvent((int) histogramSpinner.getValue()));
			}
		);

		getToolBar().add(new JLabel("Histogram bins:"));
		getToolBar().add(histogramSpinner);
	}
	
	public boolean handleEvent(HistogramChangeEvent event)
	{
		histogram = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		frame = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	protected int getFrame()
	{
		return frame;
	}
	
	protected int getHistogram()
	{
		return histogram;
	}
	
	@Override
	protected void prepareData()
	{
		mins.clear();
		maxs.clear();
		deltas.clear();
		series.clear();
		
		for (ASDFile file : getFiles())
		{
			// Find limits
			
			double min = getRawMinimum(file);
			double max = getRawMaximum(file);
			
			// Create density
			
			double[][] density = new double[2][histogram];
			
			// Calculate x
			
			for (int i = 0; i < histogram; i++)
			{
				density[0][i] = min + (max - min) / histogram * (i + 0.5);
			}
			
			// Calculate y
			
			int count = 0;
			
			for (int i = 0; i < getRawDataLength(file); i++)
			{
				double voltage = getRawValue(file, i);
				
				int bin = Math.min((int) Math.floor((voltage - min) / (max - min) * histogram), histogram - 1);
				
				density[1][bin]++;
				
				count++;
			}
			
			// Normalize y
			
			for (int i = 0; i < histogram; i++)
			{
				density[1][i] /= count;
			}
			
			// Remember
			
			mins.put(file, min);
			maxs.put(file, max);
			deltas.put(file, (density[0][1] - density[0][0]) / 2);
			series.put(file, density);
		}
	}
	
	@Override
	protected double getDomainMinimum(ASDFile file)
	{
		double result = Double.MAX_VALUE;
		
		double delta = deltas.get(file);
		
		for (int index = 0; index < getDataLength(file); index++)
		{
			result = Math.min(result, getDomainValue(file, index) - delta);
		}
		
		return result;
	}
	
	@Override
	protected double getDomainMaximum(ASDFile file)
	{
		double result = -Double.MAX_VALUE;
		
		double delta = deltas.get(file);
		
		for (int index = 0; index < getDataLength(file); index++)
		{
			result = Math.max(result, getDomainValue(file, index) + delta);
		}
		
		return result;
	}
	
	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		double result = Double.MAX_VALUE;
		
		for (int index = 0; index < getDataLength(file); index++)
		{
			result = Math.min(result, getRangeValue(file, index));
		}
		
		return result;
	}
	
	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		double result = -Double.MAX_VALUE;
		
		for (int index = 0; index < getDataLength(file); index++)
		{
			result = Math.max(result, getRangeValue(file, index));
		}
		
		return result;
	}

	@Override
	protected int getDataLength(ASDFile file)
	{
		return series.get(file)[0].length;
	}

	@Override
	protected double getDomainValue(ASDFile file, int index)
	{
		return series.get(file)[0][index];
	}

	@Override
	protected double getRangeValue(ASDFile file, int index)
	{
		return series.get(file)[1][index];
	}
	
	@Override
	protected void paintComponent(Graphics graphics)
	{		
		for (ASDFile file : getFiles())
		{
			double delta = deltas.get(file);
			
			for (int index = 0; index < getDataLength(file); index++)
			{	
				double x1 = getDomainValue(file, index) - delta;
				double y1 = getRangeValue(file, index);

				double x2 = getDomainValue(file, index) + delta;
				double y2 = getRangeValue(file, index);
				
				double progress = 0;
				
				drawLine(graphics, calculateColor(file, 1, progress), x1, y1, x2, y2);
			}
			
			for (int index = 1; index < getDataLength(file); index++)
			{	
				double x1 = getDomainValue(file, index) - delta;
				double y1 = getRangeValue(file, index - 1);

				double x2 = getDomainValue(file, index) - delta;
				double y2 = getRangeValue(file, index);
				
				double progress = 0;
				
				drawLine(graphics, calculateColor(file, 1, progress), x1, y1, x2, y2);
			}
			
			if (frame < getRawDataLength(file))
			{
				double x = getDomainMarkerValue(file);
				double y = getRangeMarkerValue(file);
				
				double progress = 0;
				
				drawPoint(graphics, calculateColor(file, 0.5, progress), x, y);
			}
		}
	}
	
	protected double getDomainMarkerValue(ASDFile file)
	{
		return getRawValue(file, frame);
	}
	
	protected double getRangeMarkerValue(ASDFile file)
	{
		double value = getDomainMarkerValue(file);
		
		double min = mins.get(file);
		double max = maxs.get(file);
		
		int bin = Math.min((int) Math.floor((value - min) / (max - min) * histogram), histogram - 1);
		
		return series.get(file)[1][bin];
	}
	
	protected double getRawDataLength(ASDFile file)
	{
		return file.getLengthDisplayed();
	}
	
	protected abstract double getRawMinimum(ASDFile file);
	protected abstract double getRawMaximum(ASDFile file);
	
	protected abstract double getRawValue(ASDFile file, int index);

}
