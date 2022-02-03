package com.hyperkit.analysis.events.parts;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.PartEvent;
import com.hyperkit.analysis.parts.PropertyPart;

public class PropertyPartChangeEvent extends PartEvent<PropertyPart>
{
	
	private Dataset file;

	public PropertyPartChangeEvent(PropertyPart part, Dataset file)
	{
		super(part);
		
		this.file = file;
	}
	
	public Dataset getASDFile()
	{
		return file;
	}

}
