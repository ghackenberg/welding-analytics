package com.hyperkit.analysis.parts.canvas;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.WindowChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class TimeseriesCanvasPart extends CanvasPart
{

	private int frame = 0;
	private int window_backward = 10000;
	
	public TimeseriesCanvasPart(String title)
	{
		super(title);
		
		// Point
		
		JSpinner pointSpinner = new JSpinner(new SpinnerNumberModel(window_backward, 100, 100000, 100));
		pointSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new WindowChangeEvent((int) pointSpinner.getValue()));
			}
		);

		getToolBar().add(new JLabel("Point count:"));
		getToolBar().add(pointSpinner);
	}
	
	public boolean handleEvent(WindowChangeEvent event)
	{
		window_backward = event.getValue();
		
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
			double[][] data = getData(file, frame, window_backward);
			
			assert data.length == 2;
			assert data[0].length == data[1].length;
			
			series.put(file.getName(), data);
			
			/*
			for (int index = 0; index < data[0].length; index++)
			{
				domain_lower = Math.min(domain_lower, data[0][index]);
				domain_upper = Math.max(domain_upper, data[0][index]);
				
				range_lower = Math.min(range_lower, data[1][index]);
				range_upper = Math.max(range_upper, data[1][index]);	
			}
			*/
			
			domain_lower = Math.min(domain_lower, getDomainMinimum(file, frame, window_backward));
			domain_upper = Math.max(domain_upper, getDomainMaximum(file, frame, window_backward));
			
			range_lower = Math.min(range_lower, getRangeMinimum(file, frame, window_backward));
			range_upper = Math.max(range_upper, getRangeMaximum(file, frame, window_backward));	
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
			
			for (int index = 1; index < data[0].length; index++)
			{	
				double x1 = width * (data[0][index - 1] - domain_lower) / domain_delta;
				double y1 = height - height * (data[1][index - 1] - range_lower) / range_delta;

				double x2 = width * (data[0][index] - domain_lower) / domain_delta;
				double y2 = height - height * (data[1][index] - range_lower) / range_delta;

				double progress = 1 - (index + 1.0) / data[0].length;
				
				double r = red + (255 - red) * progress;
				double g = green + (255 - green) * progress;
				double b = blue + (255 - blue) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			}
			
			for (int index = data[0].length - 1; index < data[0].length; index++)
			{	
				double x = width * (data[0][index] - domain_lower) / domain_delta;
				double y = height - height * (data[1][index] - range_lower) / range_delta;

				double progress = 1 - (index + 1.0) / data[0].length;
				
				double r = red * 0.5 + (255 - red * 0.5) * progress;
				double g = green * 0.5 + (255 - green * 0.5) * progress;
				double b = blue * 0.5 + (255 - blue * 0.5) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.fillOval((int) x - 2, (int) y - 2, 4, 4);
			}
		}
	}
	
	protected abstract double getDomainMinimum(ASDFile file, int frame, int window);
	protected abstract double getRangeMinimum(ASDFile file, int frame, int window);
	
	protected abstract double getDomainMaximum(ASDFile file, int frame, int window);
	protected abstract double getRangeMaximum(ASDFile file, int frame, int window);
	
	protected abstract double[][] getData(ASDFile file, int frame, int window);

}
