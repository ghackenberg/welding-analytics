package com.hyperkit.analysis.parts.canvas;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.hyperkit.analysis.events.values.AnimateChangeEvent;
import com.hyperkit.analysis.events.values.AverageChangeEvent;
import com.hyperkit.analysis.events.values.ExponentChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.OffsetChangeEvent;
import com.hyperkit.analysis.events.values.WindowChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class CloudCanvasPart extends CanvasPart
{
	
	private int frame;
	private int window;
	private int average;
	
	private int offset = 10;
	private int exponent = 3;
	private boolean animate = false;
	
	public CloudCanvasPart(String title, String domain, String domainUnit, String range, String rangeUnit, int frame, int window, int average)
	{
		super(title, domain, domainUnit, range, rangeUnit, "icons/parts/scatter.png", true, true);
		
		this.frame = frame;
		this.window = window;
		this.average = average;
		
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
		
		// Animate
		
		JCheckBox animateCheck = new JCheckBox("", animate);
		animateCheck.addChangeListener(
			event ->
			{
				this.handleEvent(new AnimateChangeEvent(animateCheck.isSelected()));
			}
		);
		
		getToolBar().add(new JLabel("Offset:"));
		getToolBar().add(offsetSpinner);
		getToolBar().add(new JLabel("Exponent:"));
		getToolBar().add(exponentSpinner);
		getToolBar().add(new JLabel("Animate:"));
		getToolBar().add(animateCheck);
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		if (frame != event.getValue())
		{
			frame = event.getValue();
			
			if (animate)
			{
				getPanel().repaint();
			}
		}
		
		return true;
	}
	
	public boolean handleEvent(WindowChangeEvent event)
	{
		if (window != event.getValue())
		{
			window = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(AverageChangeEvent event)
	{
		if (average != event.getValue())
		{
			average = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(OffsetChangeEvent event)
	{
		if (offset != event.getValue())
		{
			offset = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(ExponentChangeEvent event)
	{
		if (exponent != event.getValue())
		{
			exponent = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(AnimateChangeEvent event)
	{
		if (animate != event.getValue())
		{
			animate = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public int getAverage()
	{
		return average;
	}

	@Override
	protected void prepareData()
	{
		// empty
	}

	@Override
	protected int getDataLength(ASDFile file)
	{
		return file.getLengthDisplayed();
	}
	
	@Override
	protected void paintComponent(Graphics2D graphics, int width, int height)
	{
		int[][][] count = new int[getFiles().size()][width][height];
		int[] max = new int[getFiles().size()];
		
		for (int number = 0; number < getFiles().size(); number++)
		{
			ASDFile file = getFiles().get(number);
			
			int start = animate ? Math.max(frame - window, 0) : 0;
			int end = animate ? Math.min(frame, getDataLength(file) - 1) : getDataLength(file) - 1;
			
			for (int index = start; index <= end; index++)
			{	
				double x = projectDomain(width, getDomainValue(file, index));
				double y = projectRange(height, getRangeValue(file, index));

				if (x >= getPaddingLeft() && x <= getPanel().getWidth() - getPaddingRight())
				{
					if (y >= getPaddingTop() && y <= getPanel().getHeight() - getPaddingBottom())
					{
						max[number] = Math.max(max[number], ++count[number][(int) x][(int) y]);
					}
				}
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
				
				if (r < 255 || b < 255 || g < 255)
				{
					graphics.setColor(new Color((int) r, (int) g, (int) b));
					graphics.fillRect(x, y, 1, 1);
				}
				
				// TODO graphics.fillRect(x - spread, y - spread, spread * 2 + 1, spread * 2 + 1);
			}
		}
	}

}
