package com.hyperkit.analysis.datasets;

import java.io.File;
import java.util.Map.Entry;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.values.ProgressChangeEvent;

import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Group;
import io.jhdf.api.Node;

public class HDFDataset extends Dataset {

	public HDFDataset(File file)
	{
		super(file);
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(0));
		
		// Parse data
		
		try (HdfFile hdfFile = new HdfFile(file))
		{
			recursivePrintGroup(hdfFile, 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(100));
		
		// Clean data
		
		cleanData();
		
		// Update active data
		
		updateActiveData();
	}

	private static void recursivePrintGroup(Group group, int level)
	{
		for (Node node : group)
		{
			for (int index = 0; index < level; index++)
			{
				System.out.print(" ");
			}
			
			System.out.print(node.getName() + " / ");
			
			System.out.print(node.getType() + ": ");
			
			for (Entry<String, Attribute> entry : node.getAttributes().entrySet())
			{
				System.out.print(entry.getKey() + " = " + entry.getValue().getData() + ", ");
			}
			
			if (node instanceof io.jhdf.api.Dataset)
			{
				io.jhdf.api.Dataset dataset = (io.jhdf.api.Dataset) node;
				
				System.out.print(dataset.getSize() + ", " + dataset.getDataType() + ", " + dataset.getDataLayout());
			}
			
			System.out.println();
			
			if (node instanceof Group)
			{
				recursivePrintGroup((Group) node, level + 1);
			}
		}
	}

}
