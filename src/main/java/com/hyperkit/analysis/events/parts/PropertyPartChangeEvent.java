package com.hyperkit.analysis.events.parts;

import com.hyperkit.analysis.events.PartEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.PropertyPart;

public class PropertyPartChangeEvent extends PartEvent<PropertyPart>
{
	
	private ASDFile file;

	public PropertyPartChangeEvent(PropertyPart part, ASDFile file)
	{
		super(part);
		
		this.file = file;
	}
	
	public ASDFile getASDFile()
	{
		return file;
	}

}
