package com.hyperkit.analysis.parts.canvas;

import java.util.HashMap;
import java.util.Map;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.events.values.AverageChangeEvent;

public abstract class DerivativeCanvasPart extends TraceCanvasPart
{
	
	private Map<Dataset, Double> rangeMin = new HashMap<>();
	private Map<Dataset, Double> rangeMax = new HashMap<>();
	
	public DerivativeCanvasPart(String title, String range, String rangeUnit, int frame, int window, int average)
	{
		super(title, "Time", "s", range, rangeUnit, "icons/parts/derivative.png", false, true, frame, window, average, 1);
	}
	
	@Override
	public boolean handleEvent(FilePartAddEvent event)
	{
		rangeMin.put(event.getASDFile(), calculateRangeMinimum(event.getASDFile()));
		rangeMax.put(event.getASDFile(), calculateRangeMaximum(event.getASDFile()));
		
		return super.handleEvent(event);
	}
	
	@Override
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		rangeMin.remove(event.getASDFile());
		rangeMax.remove(event.getASDFile());
		
		return super.handleEvent(event);
	}
	
	@Override
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		rangeMin.put(event.getASDFile(), calculateRangeMinimum(event.getASDFile()));
		rangeMax.put(event.getASDFile(), calculateRangeMaximum(event.getASDFile()));
		
		return super.handleEvent(event);
	}
	
	@Override
	public boolean handleEvent(AverageChangeEvent event)
	{
		for (Dataset file : getFiles())
		{
			rangeMin.put(file, calculateRangeMinimum(file));
			rangeMax.put(file, calculateRangeMaximum(file));
		}
		
		return super.handleEvent(event);
	}
	
	@Override
	protected double getDomainMinimum(Dataset file)
	{
		return getDataLength(file) > 0 ? getDomainValue(file, 0) : Double.MAX_VALUE;
	}
	
	@Override
	protected double getDomainMaximum(Dataset file)
	{	
		return getDataLength(file) > 0 ? getDomainValue(file, getDataLength(file) - 1) : -Double.MAX_VALUE;
	}
	
	@Override
	protected double getRawDomainValue(Dataset file, int index)
	{
		return file.getTimestampDisplayed(index);
	}
	
	@Override
	protected double getRangeMinimum(Dataset file)
	{
		return rangeMin.get(file);
	}
	
	@Override
	protected double getRangeMaximum(Dataset file)
	{	
		return rangeMax.get(file);
	}
	
	private double calculateRangeMinimum(Dataset file)
	{
		double min = Double.MAX_VALUE;
		
		for (int index = 1; index < file.getLengthDisplayed() - 1; index++)
		{
			min = Math.min(min, getRawRangeValue(file, index));
		}
		
		return min;
	}
	
	private double calculateRangeMaximum(Dataset file)
	{
		double max = -Double.MAX_VALUE;
		
		for (int index = 1; index < file.getLengthDisplayed() - 1; index++)
		{
			max = Math.max(max, getRawRangeValue(file, index));
		}
		
		return max;
	}
	
	@Override
	protected double getRawRangeValue(Dataset file, int index)
	{
		double y1 = getIntegratedRawRangeValue(file, index - 1);
		double y2 = getIntegratedRawRangeValue(file, index);
		double y3 = getIntegratedRawRangeValue(file, index + 1);
		
		double dy1 = y2 - y1;
		double dy2 = y3 - y2;
		
		return (dy1 + dy2) / 2;
	}
	
	protected abstract double getIntegratedRawRangeValue(Dataset file, int index);
	
}
