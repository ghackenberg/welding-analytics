package com.hyperkit.analysis.parts.canvas;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.events.values.AverageChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.HistogramChangeEvent;
import com.hyperkit.analysis.events.values.MarkerChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class HistogramCanvasPart extends CanvasPart
{

	private int frame;
	private int average;
	private int histogram;
	
	private Map<ASDFile, Integer> marker;
	
	private Map<ASDFile, Double> minDomain = new HashMap<>();
	private Map<ASDFile, Double> maxDomain = new HashMap<>();
	private Map<ASDFile, Double> minRange = new HashMap<>();
	private Map<ASDFile, Double> maxRange = new HashMap<>();
	private Map<ASDFile, Double> deltas = new HashMap<>();
	private Map<ASDFile, double[][]> series = new HashMap<>();
	
	public HistogramCanvasPart(String title, String domain, int frame, int average, int histogram)
	{
		super(title, domain, "Probability (in %)", HistogramCanvasPart.class.getClassLoader().getResource("icons/parts/histogram.png"), true, true);
		
		this.frame = frame;
		this.average = average;
		this.histogram = histogram;
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		if (frame != event.getValue())
		{
			frame = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(AverageChangeEvent event)
	{
		if (average != event.getValue())
		{
			average = event.getValue();
			
			for (ASDFile file : getFiles())
			{
				updateHistogram(file);	
			}
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(HistogramChangeEvent event)
	{
		if (histogram != event.getValue())
		{
			histogram = event.getValue();
			
			for (ASDFile file : getFiles())
			{
				updateHistogram(file);	
			}
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(MarkerChangeEvent event)
	{
		if (marker != event.getValue())
		{
			marker = event.getValue();
			
			getPanel().repaint();
		}
		
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
	
	protected int getAverage()
	{
		return average;
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
	protected void paintComponent(Graphics2D graphics)
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
			
			/*
			if (frame >= 0 && frame < getRawDataLength(file))
			{
				double x = getDomainMarkerValue(file);
				double y = getRangeMarkerValue(file);
				
				double progress = 0;
				
				drawPoint(graphics, calculateColor(file, 0.5, progress), x, y);
			}
			*/
		}
		
		// Update marker
		
		boolean hover = getMouseCurrentX() != Integer.MAX_VALUE && getMouseCurrentY() != Integer.MAX_VALUE;
		
		if (hover)
		{
			marker = new HashMap<>();
			
			for (ASDFile file : getFiles())
			{
				int marker_index = Integer.MAX_VALUE;
				double marker_distance = Double.MAX_VALUE;
				
				for (int index = 0; index < getDataLength(file); index++)
				{
					double x = getDomainValue(file, index);
					double y = getRangeValue(file, index);
					
					if (check(x, y))
					{
						double domain_delta = getMouseCurrentX() - projectDomain(x);
						double range_delta = getMouseCurrentY() - projectRange(y);
						
						double temp = Math.sqrt(domain_delta * domain_delta + range_delta * range_delta);
						
						if (temp < marker_distance)
						{
							marker_index = index;
							marker_distance = temp;
						}
					}
				}
				
				if (marker_index != Integer.MAX_VALUE)
				{
					marker.put(file, marker_index);
				}
			}
		}
		
		if (marker != null)
		{
			for (Entry<ASDFile, Integer> entry : marker.entrySet())
			{
				ASDFile file = entry.getKey();
				
				int index = entry.getValue();
			
				if (hover)
				{
					if (series.containsKey(file))
					{
						double domain = getDomainValue(file, index);
						double range = getRangeValue(file, index);
						
						drawMarker(graphics, calculateColor(file, 0.5, Math.pow(0, 10)), domain, range);
					}
				}
				else
				{
					if (minDomain.containsKey(file) && maxDomain.containsKey(file) && series.containsKey(file))
					{
						double domain = getRawValue(file, index);	
						double range = calculateRangeValue(file, domain);
						
						drawMarker(graphics, calculateColor(file, 0.5, Math.pow(0, 10)), domain, range);
					}
				}
			}
		}
	}
	
	private double calculateRangeValue(ASDFile file, double value)
	{
		double min = minDomain.get(file);
		double max = maxDomain.get(file);
		
		int bin = Math.min((int) Math.floor((value - min) / (max - min) * histogram), histogram - 1);
		
		return series.get(file)[1][bin];
	}
	
	protected double getDomainMarkerValue(ASDFile file)
	{
		return getRawValue(file, frame);
	}
	
	protected double getRangeMarkerValue(ASDFile file)
	{
		return calculateRangeValue(file, getDomainMarkerValue(file));
	}
	
	protected double getRawDataLength(ASDFile file)
	{
		return file.getLengthDisplayed();
	}
	
	protected abstract double getRawMinimum(ASDFile file);
	protected abstract double getRawMaximum(ASDFile file);
	
	protected abstract double getRawValue(ASDFile file, int index);

}
