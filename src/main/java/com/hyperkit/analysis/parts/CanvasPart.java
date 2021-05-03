package com.hyperkit.analysis.parts;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;

public abstract class CanvasPart extends Part {
	
	private List<ASDFile> files = new ArrayList<>();
	
	private JPanel panel;
	
	private int width;
	private int height;
	
	private double domain_lower;
	private double domain_upper;
	private double domain_delta;
	
	private double range_lower;
	private double range_upper;
	private double range_delta;
	
	public CanvasPart(String title)
	{
		super(title, ChartPart.class.getClassLoader().getResource("icons/parts/canvas.png"));
		
		CanvasPart self = this;
		
		panel = new JPanel()
		{
			private static final long serialVersionUID = -1222107750580828320L;
			
			@Override
			protected void paintComponent(Graphics graphics)
			{
				synchronized (files)
				{
					super.paintComponent(graphics);
					
					width = panel.getWidth();
					height = panel.getHeight();
					
					prepareData();
					
					domain_lower = +Double.MAX_VALUE;
					domain_upper = -Double.MAX_VALUE;
					
					range_lower = +Double.MAX_VALUE;
					range_upper = -Double.MAX_VALUE;
					
					for (ASDFile file : files)
					{
						domain_lower = Math.min(domain_lower, getDomainMinimum(file));
						domain_upper = Math.max(domain_upper, getDomainMaximum(file));
						
						range_lower = Math.min(range_lower, getRangeMinimum(file));
						range_upper = Math.max(range_upper, getRangeMaximum(file));
					}
					
					domain_delta = domain_upper - domain_lower;
					range_delta = range_upper - range_lower;
					
					double dl = domain_lower - domain_delta * 0.05;
					double du = domain_upper + domain_delta * 0.05;
					
					double rl = range_lower - range_delta * 0.05;
					double ru = range_upper + range_delta * 0.05;
					
					domain_lower -= domain_delta * 0.1;
					domain_upper += domain_delta * 0.1;
					
					range_lower -= range_delta * 0.1;
					range_upper += range_delta * 0.1;
					
					domain_delta *= 1.2;
					range_delta *= 1.2;
					
					self.paintComponent(graphics);
					
					drawLine(graphics, new Color(128,128,128), dl, rl, du, rl);
					drawLine(graphics, new Color(128,128,128), dl, ru, dl, rl);
					
					double dx = crop((du - dl) / 10);
					double dy = crop((ru - rl) / 10);
					
					FontMetrics metrics = graphics.getFontMetrics();
					
					for (double x = Math.ceil(dl / dx); x <= Math.floor(du / dx); x++)
					{
						String string = String.format("%." + digits(dx) + "f", x * dx);
						
						Rectangle2D bounds = metrics.getStringBounds(string, graphics);
						
						graphics.setColor(new Color(128,128,128));
						graphics.drawLine((int) projectX(x * dx), (int) projectY(rl) - 2, (int) projectX(x * dx), (int) projectY(rl) + 2);
						graphics.drawString(string, (int) (projectX(x * dx) - bounds.getWidth() / 2), (int) (projectY(rl) + bounds.getHeight() / 2));
					}
					for (double y = Math.ceil(rl / dy); y <= Math.floor(ru / dy); y++)
					{
						String string = String.format("%." + digits(dy) + "f", y * dy);
						
						Rectangle2D bounds = metrics.getStringBounds(string, graphics);
						
						graphics.setColor(new Color(128,128,128));
						graphics.drawLine((int) projectX(dl) - 2, (int) projectY(y * dy), (int) projectX(dl) + 2, (int) projectY(y * dy));
						graphics.drawString(string, (int) (projectX(dl) - bounds.getWidth() / 2), (int) (projectY(y * dy) + bounds.getHeight() / 2));
					}
				}
			}
		};
		panel.setBackground(Color.white);
	}
	
	protected double crop(double value)
	{
		if (value > 0)
		{
			if (value < 1)
			{
				int count = 0;
				
				while (value < 1)
				{
					value *= 10;
					count++;
				}
				
				value = Math.ceil(value);
				
				while (count > 0)
				{
					value /= 10;
					count--;
				}
			}
			else
			{
				value = Math.ceil(value);
			}		
		}
		
		return value;
	}
	
	protected int digits(double value)
	{
		if (value > 0 && value < 1)
		{
			int count = 0;
			
			while (value < 1)
			{
				value *= 10;
				count++;
			}
			
			return count;
		}
		
		return 0;
	}
	
	@Override
	protected Component createComponent()
	{
		return panel;
	}
	
	public List<ASDFile> getFiles()
	{
		return files;
	}
	
	public JPanel getPanel()
	{
		return panel; 
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{		
		synchronized (files)
		{
			files.add(event.getASDFile());
		}
		
		panel.repaint();
		
		return true;
	}
	
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		synchronized (files)
		{
			files.remove(event.getASDFile());
		}
		
		panel.repaint();
		
		return true;
	}
	
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		panel.repaint();
		
		return true;
	}
	
	protected double projectX(double x)
	{
		return width * (x - domain_lower) / domain_delta;
	}
	
	protected double projectY(double y)
	{
		return height - height * (y - range_lower) / range_delta;
	}
	
	protected double calculateColor(int value, double shade, double progress)
	{
		return value + (255 - value) * progress;
	}
	
	protected Color calculateColor(Color color, double shade, double progress)
	{
		double r = calculateColor(color.getRed(), shade, progress);
		double g = calculateColor(color.getGreen(), shade, progress);
		double b = calculateColor(color.getBlue(), shade, progress);
		
		return new Color((int) r, (int) g, (int) b);
	}
	
	protected Color calculateColor(ASDFile file, double shade, double progress)
	{
		return calculateColor(file.getColor(), shade, progress);
	}
	
	protected void drawPoint(Graphics graphics, Color color, double x, double y)
	{
		graphics.setColor(color);
		graphics.fillOval((int) projectX(x) - 2, (int) projectY(y) - 2, 4, 4);
	}
	
	protected void drawLine(Graphics graphics, Color color, double x1, double y1, double x2, double y2)
	{	
		graphics.setColor(color);
		graphics.drawLine((int) projectX(x1), (int) projectY(y1), (int) projectX(x2), (int) projectY(y2));
	}
	
	protected abstract void prepareData();
	
	protected abstract double getDomainMinimum(ASDFile file);
	protected abstract double getRangeMinimum(ASDFile file);
	
	protected abstract double getDomainMaximum(ASDFile file);
	protected abstract double getRangeMaximum(ASDFile file);
	
	protected abstract int getDataLength(ASDFile file);
	
	protected abstract double getDomainValue(ASDFile file, int index);	
	protected abstract double getRangeValue(ASDFile file, int index);
	
	protected abstract void paintComponent(Graphics graphics);

}
