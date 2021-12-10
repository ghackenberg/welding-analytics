package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class ResistanceDerivativeCanvasPart extends DerivativeCanvasPart
{

	public ResistanceDerivativeCanvasPart(int frame, int window, int average)
	{
		super("Resistance derivative", "Resistance", "V/A", frame, window, average);
	}

	@Override
	protected double getIntegratedRawRangeValue(ASDFile file, int index)
	{
		return file.getAverageVoltageDisplayed(index, getAverage()) / file.getAverageCurrentDisplayed(index, getAverage());
	}

}
