package com.hyperkit.analysis.events.parts;

import com.hyperkit.analysis.events.PartEvent;
import com.hyperkit.analysis.parts.CanvasPart;

public class ZoomChangeEvent extends PartEvent<CanvasPart>
{

	public ZoomChangeEvent(CanvasPart part)
	{
		super(part);
	}

}
