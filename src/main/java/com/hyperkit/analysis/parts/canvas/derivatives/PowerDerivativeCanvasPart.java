package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class PowerDerivativeCanvasPart extends DerivativeCanvasPart
{

	public PowerDerivativeCanvasPart(int frame, int window, int average)
	{
		super("Power derivative", "Power (in V*A)", frame, window, average);
	}

	@Override
	protected double getIntegratedRawRangeValue(ASDFile file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage()) * file.getAverageCurrentDisplayed(index, getAverage());
	}

}
