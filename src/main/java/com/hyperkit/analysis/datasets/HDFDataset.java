package com.hyperkit.analysis.datasets;

import java.io.File;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.values.ProgressChangeEvent;

public class HDFDataset extends Dataset
{

	public HDFDataset(File file)
	{
		super(file);
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(0));
		
		// Parse data
		
		// TODO
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(100));
		
		// Clean data
		
		cleanData();
		
		// Update active data
		
		updateActiveData();
	}

}
