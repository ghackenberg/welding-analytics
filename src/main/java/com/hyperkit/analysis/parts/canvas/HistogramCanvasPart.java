package com.hyperkit.analysis.parts.canvas;

import java.awt.Color;
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
	
	@Override
	protected void paintComponent(Graphics graphics)
	{	
		double domain_lower = +Double.MAX_VALUE;
		double domain_upper = -Double.MAX_VALUE;
		
		double range_lower = +Double.MAX_VALUE;
		double range_upper = -Double.MAX_VALUE;
		
		Map<String, double[][]> series = new HashMap<>();
		
		for (ASDFile file : getFiles())
		{
			double[][] data = getData(file, histogram);
			
			assert data.length == 2;
			assert data[0].length == data[1].length;
			
			double delta = (data[0][1] - data[0][0]) / 2;
			
			series.put(file.getName(), data);
			
			for (int index = 0; index < data[0].length; index++)
			{
				domain_lower = Math.min(domain_lower, data[0][index] - delta);
				domain_upper = Math.max(domain_upper, data[0][index] + delta);
				
				range_lower = Math.min(range_lower, data[1][index]);
				range_upper = Math.max(range_upper, data[1][index]);	
			}	
		}

		double domain_delta = domain_upper - domain_lower;
		double range_delta = range_upper - range_lower;
		
		domain_lower -= domain_delta * 0.1;
		domain_upper += domain_delta * 0.1;
		
		range_lower -= range_delta * 0.1;
		range_upper += range_delta * 0.1;

		domain_delta *= 1.2;
		range_delta *= 1.2;
		
		double width = getPanel().getWidth();
		double height = getPanel().getHeight();
		
		for (ASDFile file : getFiles())
		{
			Color color = file.getColor();
			
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			
			double[][] data = series.get(file.getName());
			
			double delta = (data[0][1] - data[0][0]) / 2;
			
			for (int index = 0; index < data[0].length; index++)
			{	
				double x1 = width * (data[0][index] - delta - domain_lower) / domain_delta;
				double y1 = height - height * (data[1][index] - range_lower) / range_delta;

				double x2 = width * (data[0][index] + delta - domain_lower) / domain_delta;
				double y2 = height - height * (data[1][index] - range_lower) / range_delta;

				double progress = 0;
				
				double r = red + (255 - red) * progress;
				double g = green + (255 - green) * progress;
				double b = blue + (255 - blue) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			}
			
			for (int index = 1; index < data[0].length; index++)
			{	
				double x1 = width * (data[0][index] - delta - domain_lower) / domain_delta;
				double y1 = height - height * (data[1][index - 1] - range_lower) / range_delta;

				double x2 = width * (data[0][index] - delta - domain_lower) / domain_delta;
				double y2 = height - height * (data[1][index] - range_lower) / range_delta;

				double progress = 0;
				
				double r = red + (255 - red) * progress;
				double g = green + (255 - green) * progress;
				double b = blue + (255 - blue) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			}
			
			if (hasDomainMarkerValue(file, frame) && hasRangeMarkerValue(file, frame))
			{
				double x = width * (getDomainMarkerValue(file, frame) - domain_lower) / domain_delta;
				double y = height - height * (getRangeMarkerValue(file, histogram, frame) - range_lower) / range_delta;

				double progress = 0;
				
				double r = red * 0.5 + (255 - red * 0.5) * progress;
				double g = green * 0.5 + (255 - green * 0.5) * progress;
				double b = blue * 0.5 + (255 - blue * 0.5) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.fillOval((int) x - 2, (int) y - 2, 4, 4);
			}
		}
	}
	
	protected abstract double[][] getData(ASDFile file, int step);
	
	protected abstract boolean hasDomainMarkerValue(ASDFile file, int frame);
	protected abstract boolean hasRangeMarkerValue(ASDFile file, int frame);
	
	protected abstract double getDomainMarkerValue(ASDFile file, int frame);
	protected abstract double getRangeMarkerValue(ASDFile file, int step, int frame);

}
