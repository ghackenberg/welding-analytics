package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class PowerDerivativeCanvasPart extends DerivativeCanvasPart
{

	public PowerDerivativeCanvasPart(int frame, int window, int average)
	{
		super("Power derivative", "Power", "V*A", frame, window, average);
	}

	@Override
	protected double getIntegratedRawRangeValue(Dataset file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage()) * file.getAverageCurrentDisplayed(index, getAverage());
	}

}
