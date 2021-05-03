package com.hyperkit.analysis.parts.canvas.pointclouds;

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

public class PointCloudAnimationCanvasPart extends CanvasPart
{

	private int frame = 0;
	private int window_backward = 1000;
	private Map<ASDFile, double[][]> series = new HashMap<>();
	
	public PointCloudAnimationCanvasPart()
	{
		super("Point cloud animation");
		
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
	protected void prepareData()
	{
		series.clear();
		
		for (ASDFile file : getFiles())
		{
			series.put(file, file.getCurrentVoltage(frame, window_backward));	
		}
	}

	@Override
	protected double getDomainMinimum(ASDFile file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getDomainMaximum(ASDFile file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return file.getMaxVoltageDisplayed();
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
				
				drawLine(graphics, calculateColor(file, 1, progress), x1, y1, x2, y2);
			}
			
			for (int index = Math.max(getDataLength(file) - 1, 0); index < getDataLength(file); index++)
			{	
				double x = getDomainValue(file, index);
				double y = getRangeValue(file, index);

				double progress = 1 - (index + 1.0) / getDataLength(file);
				
				drawPoint(graphics, calculateColor(file, 0.5, progress), x, y);
			}
		}
	}

}
