package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class CurrentDerivativeCanvasPart extends DerivativeCanvasPart
{

	public CurrentDerivativeCanvasPart(int frame, int window, int average)
	{
		super("Current derivative", "Current (in A)", frame, window, average);
	}

	@Override
	protected double getIntegratedRawRangeValue(ASDFile file, int index)
	{
		return file.getAverageCurrentDisplayed(index, getAverage());
	}

}
