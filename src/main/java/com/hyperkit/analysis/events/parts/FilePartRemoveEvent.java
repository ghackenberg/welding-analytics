package com.hyperkit.analysis.events.parts;

import com.hyperkit.analysis.events.PartEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.FilePart;

public class FilePartRemoveEvent extends PartEvent<FilePart>
{
	
	private ASDFile file;

	public FilePartRemoveEvent(FilePart part, ASDFile file)
	{
		super(part);
		
		this.file = file;
	}
	
	public ASDFile getASDFile()
	{
		return file;
	}

}
