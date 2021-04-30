package com.hyperkit.analysis.parts.canvases;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.hyperkit.analysis.events.values.ExponentChangeEvent;
import com.hyperkit.analysis.events.values.OffsetChangeEvent;
import com.hyperkit.analysis.events.values.SpreadChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public class PointCloudVisualizationCanvasPart extends CanvasPart
{
	
	private int offset = 10;
	private int exponent = 3;
	private int spread = 0;
	
	public PointCloudVisualizationCanvasPart()
	{
		super("Point cloud visualization");
		
		// Offset
		
		JSpinner offsetSpinner = new JSpinner(new SpinnerNumberModel(offset, 0, 100, 1));
		offsetSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new OffsetChangeEvent((int) offsetSpinner.getValue()));
			}
		);
		
		// Exponent
		
		JSpinner exponentSpinner = new JSpinner(new SpinnerNumberModel(exponent, 1, 100, 1));
		exponentSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new ExponentChangeEvent((int) exponentSpinner.getValue()));
			}
		);
		
		// Spread
		
		JSpinner spreadSpinner = new JSpinner(new SpinnerNumberModel(spread, 0, 100, 1));
		spreadSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new SpreadChangeEvent((int) spreadSpinner.getValue()));
			}
		);
		
		getToolBar().add(new JLabel("Offset:"));
		getToolBar().add(offsetSpinner);
		getToolBar().add(new JLabel("Exponent:"));
		getToolBar().add(exponentSpinner);
		getToolBar().add(new JLabel("Spread:"));
		getToolBar().add(spreadSpinner);
	}
	
	public boolean handleEvent(OffsetChangeEvent event)
	{
		offset = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(ExponentChangeEvent event)
	{
		exponent = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(SpreadChangeEvent event)
	{
		spread = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	@Override
	protected void paintComponent(Graphics graphics)
	{	
		double range_lower = +Double.MAX_VALUE;
		double range_upper = -Double.MAX_VALUE;
		
		double domain_lower = +Double.MAX_VALUE;
		double domain_upper = -Double.MAX_VALUE;
		
		for (ASDFile file : getFiles())
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
		
		int width = getPanel().getWidth();
		int height = getPanel().getHeight();

		int[][][] count = new int[getFiles().size()][width][height];
		int[] max = new int[getFiles().size()];
		
		for (int number = 0; number < getFiles().size(); number++)
		{
			ASDFile file = getFiles().get(number);
			
			for (int index = 0; index < file.getLengthDisplayed(); index++)
			{	
				double x = width * (file.getCurrentDisplayed(index) - domain_lower) / domain_delta;
				double y = height - height * (file.getVoltageDisplayed(index) - range_lower) / range_delta;

				max[number] = Math.max(max[number], ++count[number][(int) x][(int) y]);
			}
		}
		
		for (int x = 0; x < width; x++)
		{	
			for (int y = 0; y < height; y++)
			{
				double r = 255;
				double g = 255;
				double b = 255;
				
				for (int number = 0; number < getFiles().size(); number++)
				{
					ASDFile file = getFiles().get(number);
					
					Color color = file.getColor();
					
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();
					
					double progress = Math.pow(count[number][x][y] / (double) max[number], 1.0 / exponent);
					
					progress = progress > 0 ? (offset / 100. + (1 - offset / 100.) * progress) : 0;
					
					r -= (255 - red) * progress;
					g -= (255 - green) * progress;
					b -= (255 - blue) * progress;
				}
				
				r = Math.max(r, 0);
				g = Math.max(g, 0);
				b = Math.max(b, 0);
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.fillRect(x, y, 1, 1);
				
				// TODO graphics.fillRect(x - spread, y - spread, spread * 2 + 1, spread * 2 + 1);
			}
		}
	}

}
