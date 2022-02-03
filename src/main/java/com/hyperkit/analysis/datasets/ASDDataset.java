package com.hyperkit.analysis.datasets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.values.ProgressChangeEvent;

public class ASDDataset extends Dataset
{

	public ASDDataset(File file) throws IOException
	{
		super(file);
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(0));
		
		// Parse data
		
		long size = getFile().length();
		
		long position = 0;
		
		int progress = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(getFile()));
		
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			position += line.length() + 1;
			
			try
			{
				String[] parts = line.split("\t");
				
				if (parts.length == 3)
				{	
					double first = parseDouble(parts[0]);
					double second = parseDouble(parts[1]);
					double third = parseDouble(parts[2]);
					
					data.add(new double[] {first, second, third, second / third, second * third});
				}
				else if (parts.length == 4)
				{
					double first = parseDouble(parts[0]);
					double second = parseDouble(parts[3]);
					double third = parseDouble(parts[2]);
					
					data.add(new double[] {first, second, third, second / third, second * third});
				}
				else
				{
					// silent
				}
			}
			catch (NumberFormatException | ParseException e)
			{
				// silent
			}
			
			int temp = (int) Math.floor(1.0 * position / size * 100);
			
			if (temp != progress)
			{
				progress = temp;
				
				Bus.getInstance().broadcastEvent(new ProgressChangeEvent(progress));
			}
		}
		
		reader.close();
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(100));
		
		// Clean data
		
		cleanData();
		
		// Update active data
		
		updateActiveData();
	}
	
	private double parseDouble(String string) throws NumberFormatException, ParseException
	{
		NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
		
		if (!string.contains("e"))
		{
			return format.parse(string).doubleValue();
		}
		else
		{
			String[] parts = string.split("e");
			
			if (parts.length == 2)
			{
				double number = format.parse(parts[0]).doubleValue();
				double exponent = format.parse(parts[1]).intValue();
				
				return number * Math.pow(10, exponent);
			}
			else
			{
				throw new NumberFormatException("Number format should be <x>e<y>.");
			}
		}
	}

}
