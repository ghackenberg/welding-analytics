package com.hyperkit.analysis.parts.canvas.timeseries;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TimeseriesCanvasPart;

public class CurrentTimeseriesCanvasPart extends TimeseriesCanvasPart {
	
	public CurrentTimeseriesCanvasPart()
	{
		super("Point cloud animation");
	}

	@Override
	protected double[][] getData(ASDFile file, int frame, int window)
	{
		return file.getCurrentTimeseries(frame, window);
	}

	@Override
	protected double getDomainMinimum(ASDFile file, int frame, int window)
	{
		return file.getTimestampDisplayed(Math.max(frame + 1 - window, 0));
	}

	@Override
	protected double getRangeMinimum(ASDFile file, int frame, int window)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getDomainMaximum(ASDFile file, int frame, int window)
	{
		return file.getTimestampDisplayed(Math.min(frame, file.getLengthDisplayed() - 1));
	}

	@Override
	protected double getRangeMaximum(ASDFile file, int frame, int window)
	{
		return file.getMaxCurrentDisplayed();
	}

}
