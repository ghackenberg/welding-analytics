package com.hyperkit.analysis.files;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;

import com.hyperkit.analysis.File;

public class ASDFile extends File
{
	
	private String name;
	private Color color;
	private List<double[]> data;

	public ASDFile(java.io.File file)
	{
		super(file);
		
		name = file.getAbsolutePath();
        color = new Color(Color.HSBtoRGB((float) Math.random(), 1f, 0.75f));
	}
	
	public String getName()
	{
		return name;
	}
	public Color getColor()
	{
		return color;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public List<double[]> getData()
	{
		if (data == null)
		{
			try
			{
				data = new ArrayList<>();
				
				CSVReader reader = new CSVReader(new FileReader(getFile()), '\t');
				
				NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
				
				String[] line;
				
				while ((line = reader.readNext()) != null)
				{
					try
					{
						double first = format.parse(line[0]).doubleValue();
						double second = format.parse(line[1]).doubleValue();
						double third = format.parse(line[2]).doubleValue();
						
						data.add(new double[] {first, second, third});
					}
					catch (NumberFormatException | ParseException e)
					{
						
					}
				}
				
				reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return data;
	}
	
	public double[][] getVoltageTimeseries(int steps)
	{
		List<double[]> data = getData();
		
		double min = data.get(0)[0];
		double max = data.get(data.size() - 1)[0];
		
		double[][] timeseries = new double[2][steps];
		
		for (int i = 0; i < steps; i++)
		{
			timeseries[0][i] = min + (max - min) / steps * (i + 0.5);
		}

		double[] count = new double[steps];
		
		for (double[] line : data)
		{
			double time = line[0];
			
			int bin = (int) Math.floor((time - min) / (max - min) * (steps - 1));
			
			timeseries[1][bin] += line[2];
			
			count[bin]++;
		}
		
		for (int i = 0; i < steps; i++)
		{
			timeseries[1][i] /= count[i];
		}
		
		return timeseries;
	}
	
	public double[][] getCurrentTimeseries(int steps)
	{
		List<double[]> data = getData();
		
		double min = data.get(0)[0];
		double max = data.get(data.size() - 1)[0];
		
		double[][] timeseries = new double[2][steps];
		
		for (int i = 0; i < steps; i++)
		{
			timeseries[0][i] = min + (max - min) / steps * (i + 0.5);
		}

		double[] count = new double[steps];
		
		for (double[] line : data)
		{
			double time = line[0];
			
			int bin = (int) Math.floor((time - min) / (max - min) * (steps - 1));
			
			timeseries[1][bin] += line[1];
			
			count[bin]++;
		}
		
		for (int i = 0; i < steps; i++)
		{
			timeseries[1][i] /= count[i];
		}
		
		return timeseries;
	}
	
	public double[][] getVoltageDensity(int steps)
	{
		List<double[]> data = getData();
		
		// Find limits
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (double[] line : data)
		{
			min = Math.min(min, line[1]);
			max = Math.max(max, line[1]);
		}
		
		// Create density
		
		double[][] density = new double[2][steps];
		
		// Calculate x
		
		for (int i = 0; i < steps; i++)
		{
			density[0][i] = min + (max - min) / steps * (i + 0.5);
		}
		
		// Calculate y
		
		for (double[] line : data)
		{
			double voltage = line[1];
			
			int bin = (int) Math.floor((voltage - min) / (max - min) * (steps - 1));
			
			density[1][bin]++;
		}
		
		// Normalize y
		
		for (int i = 0; i < steps; i++)
		{
			density[1][i] /= data.size();
		}
		
		return density;
	}
	
	public double[][] getCurrentDensity(int steps)
	{
		List<double[]> data = getData();
		
		// Find limits
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (double[] line : data)
		{
			min = Math.min(min, line[2]);
			max = Math.max(max, line[2]);
		}
		
		// Create density
		
		double[][] density = new double[2][steps];
		
		// Calculate x
		
		for (int i = 0; i < steps; i++)
		{
			density[0][i] = min + (max - min) / steps * (i + 0.5);
		}
		
		// Calculate y
		
		for (double[] line : data)
		{
			double current = line[2];
			
			int bin = (int) Math.floor((current - min) / (max - min) * (steps - 1));
			
			density[1][bin]++;
		}
		
		// Normalize y
		
		for (int i = 0; i < steps; i++)
		{
			density[1][i] /= data.size();
		}
		
		return density;
	}

}
