package com.hyperkit.analysis.parts.canvas;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.hyperkit.analysis.events.values.ExponentChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.OffsetChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class CloudCanvasPart extends CanvasPart
{
	
	private int frame = 0;
	private int offset = 10;
	private int exponent = 3;
	private int window = 10000;
	private boolean animate = false;
	
	public CloudCanvasPart(String title, String domain, String range)
	{
		super(title, domain, range);
		
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
		
		// Window
		
		JSpinner windowSpinner = new JSpinner(new SpinnerNumberModel(window, 1000, 100000, 1000));
		windowSpinner.addChangeListener(
			event ->
			{
				window = (int) windowSpinner.getValue();
				
				getPanel().repaint();
			}
		);
		
		// Animate
		
		JCheckBox animateCheck = new JCheckBox("", animate);
		animateCheck.addChangeListener(
			event ->
			{
				animate = animateCheck.isSelected();
				
				getPanel().repaint();
			}
		);
		
		getToolBar().add(new JLabel("Offset:"));
		getToolBar().add(offsetSpinner);
		getToolBar().add(new JLabel("Exponent:"));
		getToolBar().add(exponentSpinner);
		getToolBar().add(new JLabel("Point count:"));
		getToolBar().add(windowSpinner);
		getToolBar().add(new JLabel("Animation:"));
		getToolBar().add(animateCheck);
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
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		frame = event.getValue();
		
		if (animate)
		{
			getPanel().repaint();
		}
		
		return true;
	}
	
	@Override
	protected void paintComponent(Graphics graphics)
	{
		
		int width = getPanel().getWidth();
		int height = getPanel().getHeight();

		int[][][] count = new int[getFiles().size()][width][height];
		int[] max = new int[getFiles().size()];
		
		for (int number = 0; number < getFiles().size(); number++)
		{
			ASDFile file = getFiles().get(number);
			
			int start = animate ? Math.max(frame - window, 0) : 0;
			int end = animate ? Math.min(frame, getDataLength(file) - 1) : getDataLength(file) - 1;
			
			for (int index = start; index <= end; index++)
			{	
				double x = projectX(getDomainValue(file, index));
				double y = projectY(getRangeValue(file, index));

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
				
				if (r < 255 || b < 255 || g < 255)
				{
					graphics.setColor(new Color((int) r, (int) g, (int) b));
					graphics.fillRect(x, y, 1, 1);
				}
				
				// TODO graphics.fillRect(x - spread, y - spread, spread * 2 + 1, spread * 2 + 1);
			}
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
		return file.getLengthDisplayed();
	}

}
