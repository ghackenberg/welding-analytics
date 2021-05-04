package com.hyperkit.analysis.parts.canvas;

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
	private int window = 10000;
	private Map<ASDFile, Integer> starts = new HashMap<>();
	private Map<ASDFile, Integer> ends = new HashMap<>();
	private Map<ASDFile, Integer> counts = new HashMap<>();
	
	public TimeseriesCanvasPart(String title, String range)
	{
		super(title, "Time (in s)", range);
		
		// Point
		
		JSpinner pointSpinner = new JSpinner(new SpinnerNumberModel(window, 100, 100000, 100));
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
		window = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		frame = event.getValue();
		
		getPanel().repaint();
		
		return true;
	}
	
	protected int getFrame()
	{
		return frame;
	}
	
	protected int getWindow()
	{
		return window;
	}
	
	@Override
	protected void prepareData()
	{
		starts.clear();
		ends.clear();
		counts.clear();
		
		for (ASDFile file : getFiles())
		{
			int end = Math.min(frame + 1, file.getLengthDisplayed());
			int start = Math.max(frame + 1 - window, 0);
			int count = Math.max(0, end - start);
			
			starts.put(file, start);
			ends.put(file, end);
			counts.put(file, count);
		}
	}
	
	@Override
	protected double getDomainMinimum(ASDFile file)
	{
		return getDataLength(file) > 0 ? getDomainValue(file, 0) : Double.MAX_VALUE;
	}
	
	@Override
	protected double getDomainMaximum(ASDFile file)
	{	
		return getDataLength(file) > 0 ? getDomainValue(file, getDataLength(file) - 1) : -Double.MAX_VALUE;
	}

	@Override
	protected int getDataLength(ASDFile file)
	{
		return counts.get(file);
	}

	@Override
	protected double getDomainValue(ASDFile file, int index)
	{
		return getRawDomainValue(file, starts.get(file) + index);
	}

	@Override
	protected double getRangeValue(ASDFile file, int index)
	{
		return getRawRangeValue(file, starts.get(file) + index);
	}
	
	@Override
	protected void paintComponent(Graphics graphics)
	{		
		for (ASDFile file : getFiles())
		{
			for (int index = 1; index < getDataLength(file); index++)
			{	
				double x1 = getDomainValue(file, index - 1);
				double y1 = getRangeValue(file, index - 1);

				double x2 = getDomainValue(file, index);
				double y2 = getRangeValue(file, index);

				double progress = 1 - (index + 1.0) / getDataLength(file);
				
				drawLine(graphics, calculateColor(file, 1, Math.pow(progress, 2)), x1, y1, x2, y2);
			}
			
			for (int index = Math.max(getDataLength(file) - 1, 0); index < getDataLength(file); index++)
			{	
				double x = getDomainValue(file, index);
				double y = getRangeValue(file, index);

				double progress = 1 - (index + 1.0) / getDataLength(file);
				
				drawPoint(graphics, calculateColor(file, 0.5, Math.pow(progress, 2)), x, y);
			}
		}
	}
	
	protected double getRawDomainValue(ASDFile file, int index)
	{
		return file.getTimestampDisplayed(index);
	}
	
	protected abstract double getRawRangeValue(ASDFile file, int index);
}
