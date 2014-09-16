package com.hyperkit.analysis.events.parts;

import com.hyperkit.analysis.events.PartEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.FilePart;

public class FilePartSelectEvent extends PartEvent<FilePart>
{
	
	private ASDFile file;

	public FilePartSelectEvent(FilePart part, ASDFile file)
	{
		super(part);
		
		this.file = file;
	}
	
	public ASDFile getASDFile()
	{
		return file;
	}

}
