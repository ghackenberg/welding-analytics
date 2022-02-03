package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class VoltageDerivativeCanvasPart extends DerivativeCanvasPart
{

	public VoltageDerivativeCanvasPart(int frame, int window, int average)
	{
		super("Voltage derivative", "Voltage", "V", frame, window, average);
	}

	@Override
	protected double getIntegratedRawRangeValue(Dataset file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

}
