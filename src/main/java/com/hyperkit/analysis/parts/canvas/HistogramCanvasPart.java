package com.hyperkit.analysis.parts.canvas;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.HistogramChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class HistogramCanvasPart extends CanvasPart
{

	private int frame = 0;
	private int histogram = 100;
	private Map<ASDFile, Double> minDomain = new HashMap<>();
	private Map<ASDFile, Double> maxDomain = new HashMap<>();
	private Map<ASDFile, Double> minRange = new HashMap<>();
	private Map<ASDFile, Double> maxRange = new HashMap<>();
	private Map<ASDFile, Double> deltas = new HashMap<>();
	private Map<ASDFile, double[][]> series = new HashMap<>();
	
	public HistogramCanvasPart(String title, String domain)
	{
		super(title, domain, "Probability (in %)");
		
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
		
		for (ASDFile file : getFiles())
		{
			updateHistogram(file);	
		}
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		frame = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	@Override
	public boolean handleEvent(FilePartAddEvent event)
	{
		updateHistogram(event.getASDFile());
		
		return super.handleEvent(event);
	}
	
	@Override
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		minDomain.remove(event.getASDFile());
		maxDomain.remove(event.getASDFile());
		deltas.remove(event.getASDFile());
		series.remove(event.getASDFile());
		
		return super.handleEvent(event);
	}
	
	@Override
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		updateHistogram(event.getASDFile());
		
		return super.handleEvent(event);
	}
	
	protected int getFrame()
	{
		return frame;
	}
	
	protected int getHistogram()
	{
		return histogram;
	}
	
	private void updateHistogram(ASDFile file)
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
		
		double minProb = 100;
		double maxProb = 0;
		
		for (int i = 0; i < histogram; i++)
		{
			density[1][i] /= count;
			density[1][i] *= 100;
			
			minProb = Math.min(minProb, density[1][i]);
			maxProb = Math.max(maxProb, density[1][i]);
		}
		
		// Remember
		
		minDomain.put(file, min);
		maxDomain.put(file, max);
		
		minRange.put(file, minProb);
		maxRange.put(file, maxProb);
		
		deltas.put(file, (density[0][1] - density[0][0]) / 2);
		series.put(file, density);
	}
	
	@Override
	protected double getDomainMinimum(ASDFile file)
	{
		return minDomain.get(file);
	}
	
	@Override
	protected double getDomainMaximum(ASDFile file)
	{
		return maxDomain.get(file);
	}
	
	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return minRange.get(file);
	}
	
	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return maxRange.get(file);
	}
	
	@Override
	protected void prepareData()
	{
		// empty
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
		
		double min = minDomain.get(file);
		double max = maxDomain.get(file);
		
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
