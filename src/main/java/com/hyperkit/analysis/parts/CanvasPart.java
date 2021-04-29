package com.hyperkit.analysis.parts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.AnimationChangeEvent;
import com.hyperkit.analysis.events.PointChangeEvent;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;

public class CanvasPart extends Part {
	
	private List<ASDFile> files = new ArrayList<>();
	
	private int point;
	private int progress;

	private JPanel panel;
	
	public CanvasPart(int _point, int _progress) {
		super("Canvas", ChartPart.class.getClassLoader().getResource("icons/parts/canvas.png"));
		
		this.point = _point;
		this.progress = _progress;
		
		panel = new JPanel()
		{
			private static final long serialVersionUID = -1222107750580828320L;
			
			@Override
			protected void paintComponent(Graphics graphics)
			{
				super.paintComponent(graphics);
				
				double range_lower = +Double.MAX_VALUE;
				double range_upper = -Double.MAX_VALUE;
				
				double domain_lower = +Double.MAX_VALUE;
				double domain_upper = -Double.MAX_VALUE;
				
				for (ASDFile file : files)
				{
					range_lower = Math.min(range_lower, file.getMinVoltageDisplayed());
					range_upper = Math.max(range_upper, file.getMaxVoltageDisplayed());
					
					domain_lower = Math.min(domain_lower, file.getMinCurrentDisplayed());
					domain_upper = Math.max(domain_upper, file.getMaxCurrentDisplayed());
				}
				
				double range_delta = range_upper - range_lower;
				double domain_delta = domain_upper - domain_lower;
				
				range_lower -= range_delta * 0.1;
				range_upper += range_delta * 0.1;
				
				domain_lower -= domain_delta * 0.1;
				domain_upper += domain_delta * 0.1;
				
				range_delta *= 1.2;
				domain_delta *= 1.2;
				
				double width = getWidth();
				double height = getHeight();
				
				for (ASDFile file : files)
				{
					Color color = file.getColor();
					
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();
					
					double[][] data = file.getCurrentVoltage(point, progress);
					
					assert data.length == 2;
					assert data[0].length == data[1].length;
					
					for (int index = 0; index < data[0].length; index++)
					{	
						double y = height - height * (data[1][index] - range_lower) / range_delta;
						double x = width * (data[0][index] - domain_lower) / domain_delta;

						double progress = 1 - (1.0) / data[0].length;
						
						double r = red + (255 - red) * progress;
						double g = green + (255 - green) * progress;
						double b = blue + (255 - blue) * progress;
						
						graphics.setColor(new Color((int) r, (int) g, (int) b));
						graphics.fillOval((int) x - 2, (int) y - 2, 4, 4);
					}
					
					for (int index = 1; index < data[0].length; index++)
					{	
						double y1 = height - height * (data[1][index - 1] - range_lower) / range_delta;
						double x1 = width * (data[0][index - 1] - domain_lower) / domain_delta;
						
						double y2 = height - height * (data[1][index] - range_lower) / range_delta;
						double x2 = width * (data[0][index] - domain_lower) / domain_delta;

						double progress = 1 - (index + 1.0) / data[0].length;
						
						double r = red * 0.5 + (255 - red * 0.5) * progress;
						double g = green * 0.5 + (255 - green * 0.5) * progress;
						double b = blue * 0.5 + (255 - blue * 0.5) * progress;
						
						graphics.setColor(new Color((int) r, (int) g, (int) b));
						graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
					}
					
					for (int index = 0; index < data[0].length; index++)
					{	
						double y = height - height * (data[1][index] - range_lower) / range_delta;
						double x = width * (data[0][index] - domain_lower) / domain_delta;

						double progress = 1 - (index + 1.0) / data[0].length;
						
						double r = red + (255 - red) * progress;
						double g = green + (255 - green) * progress;
						double b = blue + (255 - blue) * progress;
						
						graphics.setColor(new Color((int) r, (int) g, (int) b));
						graphics.fillOval((int) x - 2, (int) y - 2, 4, 4);
					}
				}
			}
		};
		panel.setBackground(Color.white);
	}
	
	@Override
	protected Component createComponent()
	{
		return panel;
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{		
		files.add(event.getASDFile());
		
		panel.repaint();
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		files.remove(event.getASDFile());
		
		panel.repaint();
		
		return true;
	}
	public boolean handleEvent(PointChangeEvent event)
	{
		point = event.getPoint();
		
		panel.repaint();
		
		return true;
	}
	public boolean handleEvent(AnimationChangeEvent event)
	{
		progress = event.getProgress();
		
		panel.repaint();
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		panel.repaint();
		
		return true;
	}

}
