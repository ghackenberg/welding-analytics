package com.hyperkit.analysis.parts.canvas.derivatives;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.DerivativeCanvasPart;

public class CurrentDerivativeCanvasPart extends DerivativeCanvasPart
{

	public CurrentDerivativeCanvasPart()
	{
		super("Current derivative", "Current (in A)");
	}

	@Override
	protected double getIntegratedRawRangeValue(ASDFile file, int index)
	{
		return file.getCurrentDisplayed(index);
	}

}
