package com.hyperkit.analysis.events.parts;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.PartEvent;
import com.hyperkit.analysis.parts.FilePart;

public class FilePartRemoveEvent extends PartEvent<FilePart>
{
	
	private Dataset file;

	public FilePartRemoveEvent(FilePart part, Dataset file)
	{
		super(part);
		
		this.file = file;
	}
	
	public Dataset getASDFile()
	{
		return file;
	}

}
