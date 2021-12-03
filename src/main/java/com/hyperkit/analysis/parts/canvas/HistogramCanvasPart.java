package com.hyperkit.analysis.parts.canvas;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.FilePartSelectEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.events.parts.ZoomChangeEvent;
import com.hyperkit.analysis.events.values.AverageChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.HistogramChangeEvent;
import com.hyperkit.analysis.events.values.MarkerChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class HistogramCanvasPart extends CanvasPart
{
	
	private String domainName;
	private String domainUnit;

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
	
	private enum Statistics
	{
		MEAN_STDEV("Mean/Stdev"), MEDIAN("Median"), RMS("Root Mean Square");
		
		private final String name;
		
		private Statistics(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	private JComboBox<Statistics> combo;
	private JTextField percentage;
	
	private ASDFile selected;
	
	public HistogramCanvasPart(String title, String domainName, String domainUnit, int frame, int average, int histogram)
	{
		super(title, domainName + " (in " + domainUnit + ")", "Probability (in %)", HistogramCanvasPart.class.getClassLoader().getResource("icons/parts/histogram.png"), true, false);
		
		this.domainName = domainName;
		this.domainUnit = domainUnit;
		
		this.frame = frame;
		this.average = average;
		this.histogram = histogram;
		
		combo = new JComboBox<>(Statistics.values());
		combo.setEnabled(false);
		combo.addActionListener(action ->
		{
			updateStatistics();
			
			getPanel().repaint();
		});
		
		percentage = new JTextField("", 7);
		percentage.setEditable(false);
		percentage.setEnabled(false);
		
		getToolBar().add(new JLabel("Stats:"));
		getToolBar().add(combo);
		getToolBar().add(new JLabel("Area:"));
		getToolBar().add(percentage);
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
	
	public boolean handleEvent(FilePartSelectEvent event)
	{
		selected = event.getASDFile();
		
		if (selected != null)
		{
			combo.setEnabled(true);
			
			percentage.setEnabled(true);
			
			updateStatistics();
		}
		else
		{
			combo.setEnabled(false);
			
			percentage.setEnabled(false);
			percentage.setText("");
			
			setDomain(domainName + " (in " + domainUnit + ")");
		}
		
		getPanel().repaint();
		
		return true;
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
		
		if (selected != null)
		{
			updateStatistics();
		}
		
		return super.handleEvent(event);
	}
	
	public boolean handleEvent(ZoomChangeEvent event)
	{
		if (event.getPart() == this)
		{
			double min = getDomainLowerCustom();
			double max = getDomainUpperCustom();
			
			for (ASDFile file : getFiles())
			{
				updateZoom(file, min, max);
			}
			
			updateStatistics();
			
			getPanel().repaint();
		}
		return true;
	}
	
	private void updateStatistics()
	{
		if (selected != null)
		{
			percentage.setText(String.format("%.5f", getPercentage(selected)) + "%");
			
			switch ((Statistics) combo.getSelectedItem())
			{
			case MEAN_STDEV:
				double mean = getMean(selected);
				double stdev = getStdev(selected);
				
				setDomain(domainName + " (in " + domainUnit + " | Mean=" + String.format("%.5f", mean) + domainUnit + "±" + String.format("%.5f", stdev) + domainUnit + ")");
				
				break;
			case MEDIAN:
				double median = getMedian(selected);
				
				setDomain(domainName + " (in " + domainUnit + " | Median=" + String.format("%.5f", median) + domainUnit + ")");
				
				break;
			case RMS:
				double rms = getRootMeanSquare(selected);
				
				setDomain(domainName + " (in " + domainUnit + " | RMS=" + String.format("%.5f", rms) + domainUnit + ")");
				
				break;
			}
		}
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
		if (getDomainLowerCustom() == -Double.MAX_VALUE)
		{
			return maxRange.get(file);
		}
		else
		{
			double delta = deltas.get(file);
			
			double max = -Double.MAX_VALUE;
			
			for (int i = 0; i < histogram; i++)
			{
				double x = getDomainValue(file, i);
				double y = getRangeValue(file, i);
				
				if (x - delta >= getDomainLowerCustom() && x - delta <= getDomainUpperCustom())
				{
					max = Math.max(max, y);
				}
				else if (x + delta >= getDomainLowerCustom() && x + delta <= getDomainUpperCustom())
				{
					max = Math.max(max, y);
				}
				else if (x - delta <= getDomainLowerCustom() && x + delta >= getDomainLowerCustom())
				{
					max = Math.max(max, y);
				}
				else if (x - delta <= getDomainUpperCustom() && x + delta >= getDomainUpperCustom())
				{
					max = Math.max(max, y);
				}
			}
			
			return max;
		}
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
		else
		{
			marker = null;
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
		
		if (selected != null)
		{
			double yLower = getRangeLower();
			double yUpper = getRangeUpper();
			
			switch ((Statistics) combo.getSelectedItem())
			{
			case MEAN_STDEV:
				double mean = getMean(selected);
				double stdev = getStdev(selected);
				
				BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 5, 2 }, 0);
				
				drawLine(graphics, calculateColor(selected, 0.75, Math.pow(0, 10)), new BasicStroke(2), mean, yLower, mean, yUpper);
				drawLine(graphics, calculateColor(selected, 0.75, Math.pow(0, 10)), dashed, mean - stdev, yLower, mean - stdev, yUpper);
				drawLine(graphics, calculateColor(selected, 0.75, Math.pow(0, 10)), dashed, mean + stdev, yLower, mean + stdev, yUpper);
				
				break;
			case MEDIAN:
				double median = getMedian(selected);
				
				drawLine(graphics, calculateColor(selected, 0.75, Math.pow(0, 10)), new BasicStroke(2), median, yLower, median, yUpper);
				
				break;
			case RMS:
				double rms = getRootMeanSquare(selected);
				
				drawLine(graphics, calculateColor(selected, 0.75, Math.pow(0, 10)), new BasicStroke(2), rms, yLower, rms, yUpper);
				
				break;
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

	protected abstract double getPercentage(ASDFile file);
	protected abstract double getMean(ASDFile file);
	protected abstract double getStdev(ASDFile file);
	protected abstract double getMedian(ASDFile file);
	protected abstract double getRootMeanSquare(ASDFile file);
	
	protected abstract void updateZoom(ASDFile file, double min, double max);

}
