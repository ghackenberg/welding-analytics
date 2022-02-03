package com.hyperkit.analysis.events.parts;

import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.PartEvent;
import com.hyperkit.analysis.parts.FilePart;

public class FilePartSelectEvent extends PartEvent<FilePart>
{
	
	private Dataset file;

	public FilePartSelectEvent(FilePart part, Dataset file)
	{
		super(part);
		
		this.file = file;
	}
	
	public Dataset getASDFile()
	{
		return file;
	}

}
