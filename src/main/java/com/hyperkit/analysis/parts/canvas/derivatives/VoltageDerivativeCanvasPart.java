package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class VoltageDerivativeCanvasPart extends DerivativeCanvasPart
{

	public VoltageDerivativeCanvasPart(int frame, int window, int average)
	{
		super("Voltage derivative", "Voltage", "V", frame, window, average);
	}

	@Override
	protected double getIntegratedRawRangeValue(ASDFile file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage());
	}

}
