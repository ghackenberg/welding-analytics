package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class VoltageDerivativeCanvasPart extends DerivativeCanvasPart
{

	public VoltageDerivativeCanvasPart()
	{
		super("Voltage derivative", "Voltage (in V)");
	}

	@Override
	protected double getIntegratedRawRangeValue(ASDFile file, int index)
	{
		return file.getVoltageDisplayed(index);
	}

}
