package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class ResistanceDerivativeCanvasPart extends DerivativeCanvasPart
{

	public ResistanceDerivativeCanvasPart()
	{
		super("Resistance derivative", "Resistance (in V/A)");
	}

	@Override
	protected double getIntegratedRawRangeValue(ASDFile file, int index)
	{
		return file.getVoltageDisplayed(index) / file.getCurrentDisplayed(index);
	}

}
