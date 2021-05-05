package com.hyperkit.analysis.parts.canvas;

import com.hyperkit.analysis.files.ASDFile;

public abstract class DerivativeCanvasPart extends TraceCanvasPart
{
	
	public DerivativeCanvasPart(String title, String range)
	{
		super(title, "Time (in s)", range, 10000, 1);
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
	protected double getRawDomainValue(ASDFile file, int index)
	{
		return file.getTimestampDisplayed(index);
	}
	
	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		double min = Double.MAX_VALUE;
		
		for (int index = 1; index < file.getLengthDisplayed() - 1; index++)
		{
			min = Math.min(min, getRawRangeValue(file, index));
		}
		
		System.out.println("Min = " + min);
		
		return min;
	}
	
	@Override
	protected double getRangeMaximum(ASDFile file)
	{	
		double max = -Double.MAX_VALUE;
		
		for (int index = 1; index < file.getLengthDisplayed() - 1; index++)
		{
			max = Math.max(max, getRawRangeValue(file, index));
		}
		
		System.out.println("Max = " + max);
		
		return max;
	}
	
	@Override
	protected double getRawRangeValue(ASDFile file, int index)
	{
		double y1 = getIntegratedRawRangeValue(file, index - 1);
		double y2 = getIntegratedRawRangeValue(file, index);
		double y3 = getIntegratedRawRangeValue(file, index + 1);
		
		double dy1 = y2 - y1;
		double dy2 = y3 - y2;
		
		return (dy1 + dy2) / 2;
	}
	
	protected abstract double getIntegratedRawRangeValue(ASDFile file, int index);
	
}
