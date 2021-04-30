package com.hyperkit.analysis.parts.canvases;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.events.ExponentChangeEvent;
import com.hyperkit.analysis.events.OffsetChangeEvent;
import com.hyperkit.analysis.events.SpreadChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public class PointCloudVisualizationCanvasPart extends CanvasPart
{
	
	private int offset;
	private int exponent;
	private int spread;
	
	public PointCloudVisualizationCanvasPart(int offset, int exponent, int spread)
	{
		super("Point cloud visualization");
		
		this.offset = offset;
		this.exponent = exponent;
		this.spread = spread;
		
		// Offset
		
		JSpinner offsetSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
		offsetSpinner.addChangeListener(
			new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					Bus.getInstance().broadcastEvent(new OffsetChangeEvent((int) offsetSpinner.getValue()));
				}
			}
		);
		
		// Exponent
		
		JSpinner exponentSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 100, 1));
		exponentSpinner.addChangeListener(
			new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					Bus.getInstance().broadcastEvent(new ExponentChangeEvent((int) exponentSpinner.getValue()));
				}
			}
		);
		
		// Spread
		
		JSpinner spreadSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		spreadSpinner.addChangeListener(
			new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					Bus.getInstance().broadcastEvent(new SpreadChangeEvent((int) spreadSpinner.getValue()));
				}
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
		offset = event.getOffset();
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(ExponentChangeEvent event)
	{
		exponent = event.getExponent();
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(SpreadChangeEvent event)
	{
		spread = event.getSpread();
		
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
				double r = 0;
				double g = 0;
				double b = 0;
				
				for (int number = 0; number < getFiles().size(); number++)
				{
					ASDFile file = getFiles().get(number);
					
					Color color = file.getColor();
					
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();
					
					double progress = Math.pow(count[number][x][y] / (double) max[number], 1.0 / exponent);
					
					progress = progress > 0 ? (offset / 100. + (1 - offset / 100.) * progress) : 0;
					
					r += red * progress;
					g += green * progress;
					b += blue * progress;
				}
				
				r = Math.min(r, 255);
				g = Math.min(g, 255);
				b = Math.min(b, 255);
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.fillRect(x, y, 1, 1);
				
				// TODO graphics.fillRect(x - spread, y - spread, spread * 2 + 1, spread * 2 + 1);
			}
		}
	}

}
