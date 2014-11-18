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
	
	private static final int TIMESTAMP_INDEX = 0;
	private static final int CURRENT_INDEX = 1;
	private static final int VOLTAGE_INDEX = 2;
	
	private String name;
	
	private Color color;
	
	private List<double[]> data;

	private double minTimestampMeasured = Double.MAX_VALUE;
	private double maxTimestampMeasured = Double.MIN_VALUE;
	
	private double minVoltageMeasured = Double.MAX_VALUE;
	private double maxVoltageMeasured = Double.MIN_VALUE;
	
	private double minCurrentMeasured = Double.MAX_VALUE;
	private double maxCurrentMeasured = Double.MIN_VALUE;

	private double minTimestampDisplayed = Double.MAX_VALUE;
	private double maxTimestampDisplayed = Double.MIN_VALUE;
	
	private double minVoltageDisplayed = Double.MAX_VALUE;
	private double maxVoltageDisplayed = Double.MIN_VALUE;
	
	private double minCurrentDisplayed = Double.MAX_VALUE;
	private double maxCurrentDisplayed = Double.MIN_VALUE;

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
	
	public int getLength()
	{
		return getData().size();
	}
	
	public double getTimestampMeasured(int index)
	{
		return getData().get(index)[TIMESTAMP_INDEX];
	}
	
	public double getVoltageMeasured(int index)
	{
		return getData().get(index)[VOLTAGE_INDEX];
	}
	
	public double getCurrentMeasured(int index)
	{
		return getData().get(index)[CURRENT_INDEX];
	}
	
	public double getMinTimestampMeasured()
	{
		if (minTimestampMeasured == Double.MAX_VALUE)
		{
			for (double[] item : getData())
			{
				minTimestampMeasured = Math.min(minTimestampMeasured, item[TIMESTAMP_INDEX]);
			}
		}
		
		return minTimestampMeasured;
	}
	
	public double getMaxTimestampMeasured()
	{
		if (maxTimestampMeasured == Double.MIN_VALUE)
		{
			for (double[] item : getData())
			{
				maxTimestampMeasured = Math.max(maxTimestampMeasured, item[TIMESTAMP_INDEX]);
			}
		}
		
		return maxTimestampMeasured;
	}
	
	public double getMinVoltageMeasured()
	{
		if (minVoltageMeasured == Double.MAX_VALUE)
		{
			for (double[] item : getData())
			{
				minVoltageMeasured = Math.min(minVoltageMeasured, item[VOLTAGE_INDEX]);
			}
		}
		
		return minVoltageMeasured;
	}
	
	public double getMaxVoltageMeasured()
	{
		if (maxVoltageMeasured == Double.MIN_VALUE)
		{
			for (double[] item : getData())
			{
				maxVoltageMeasured = Math.max(maxVoltageMeasured, item[VOLTAGE_INDEX]);
			}
		}
		
		return maxVoltageMeasured;
	}
	
	public double getMinCurrentMeasured()
	{
		if (minCurrentMeasured == Double.MAX_VALUE)
		{
			for (double[] item : getData())
			{
				minCurrentMeasured = Math.min(minCurrentMeasured, item[CURRENT_INDEX]);
			}
		}
		
		return minCurrentMeasured;
	}
	
	public double getMaxCurrentMeasured()
	{
		if (maxCurrentMeasured == Double.MIN_VALUE)
		{
			for (double[] item : getData())
			{
				maxCurrentMeasured = Math.max(maxCurrentMeasured, item[CURRENT_INDEX]);
			}
		}
		
		return maxCurrentMeasured;
	}
	
	public double getMinTimestampDisplayed()
	{
		if (minTimestampDisplayed == Double.MAX_VALUE)
		{
			return getMinTimestampMeasured();
		}
		else
		{
			return minTimestampDisplayed;
		}
	}
	
	public double getMaxTimestampDisplayed()
	{
		if (maxTimestampDisplayed == Double.MIN_VALUE)
		{
			return getMaxTimestampMeasured();
		}
		else
		{
			return maxTimestampDisplayed;
		}
	}
	
	public double getMinVoltageDisplayed()
	{
		if (minVoltageDisplayed == Double.MAX_VALUE)
		{
			return getMinVoltageMeasured();
		}
		else
		{
			return minVoltageDisplayed;
		}
	}
	
	public double getMaxVoltageDisplayed()
	{
		if (maxVoltageDisplayed == Double.MIN_VALUE)
		{
			return getMaxVoltageMeasured();
		}
		else
		{
			return maxVoltageDisplayed;
		}
	}
	
	public double getMinCurrentDisplayed()
	{
		if (minCurrentDisplayed == Double.MAX_VALUE)
		{
			return getMinCurrentMeasured();
		}
		else
		{
			return minCurrentDisplayed;
		}
	}
	
	public double getMaxCurrentDisplayed()
	{
		if (maxCurrentDisplayed == Double.MIN_VALUE)
		{
			return getMaxCurrentMeasured();
		}
		else
		{
			return maxCurrentDisplayed;
		}
	}
	
	public void setMinTimestampDisplayed(double value)
	{
		minTimestampDisplayed = value;
	}
	
	public void setMaxTimestampDisplayed(double value)
	{
		maxTimestampDisplayed = value;
	}
	
	public void setMinVoltageDisplayed(double value)
	{
		minVoltageDisplayed = value;
	}
	
	public void setMaxVoltageDisplayed(double value)
	{
		maxVoltageDisplayed = value;
	}
	
	public void setMinCurrentDisplayed(double value)
	{
		minCurrentDisplayed = value;
	}
	
	public void setMaxCurrentDisplayed(double value)
	{
		maxCurrentDisplayed = value;
	}
	
	public double[][] getVoltageTimeseries(int steps)
	{
		List<double[]> data = getData();
		
		double min = getMinTimestampDisplayed();
		double max = getMaxTimestampDisplayed();
		
		double[][] timeseries = new double[2][steps];
		
		for (int i = 0; i < steps; i++)
		{
			timeseries[0][i] = min + (max - min) / steps * (i + 0.5);
		}

		double[] count = new double[steps];
		
		for (double[] line : data)
		{
			double time = line[TIMESTAMP_INDEX];
			
			if (time >= min && time <= max)
			{
				int bin = (int) Math.floor((time - min) / (max - min) * (steps - 1));
				
				timeseries[1][bin] += line[VOLTAGE_INDEX];
				
				count[bin]++;
			}
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
		
		double min = getMinTimestampDisplayed();
		double max = getMaxTimestampDisplayed();
		
		double[][] timeseries = new double[2][steps];
		
		for (int i = 0; i < steps; i++)
		{
			timeseries[0][i] = min + (max - min) / steps * (i + 0.5);
		}

		double[] count = new double[steps];
		
		for (double[] line : data)
		{
			double time = line[TIMESTAMP_INDEX];
			
			if (time >= min && time <= max)
			{
				int bin = (int) Math.floor((time - min) / (max - min) * (steps - 1));
				
				timeseries[1][bin] += line[CURRENT_INDEX];
				
				count[bin]++;
			}
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
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minVoltage = getMinVoltageDisplayed();
		double maxVoltage = getMaxVoltageDisplayed();
		
		// Create density
		
		double[][] density = new double[2][steps];
		
		// Calculate x
		
		for (int i = 0; i < steps; i++)
		{
			density[0][i] = minVoltage + (maxVoltage - minVoltage) / steps * (i + 0.5);
		}
		
		// Calculate y
		
		int count = 0;
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && voltage >= minVoltage && voltage <= maxVoltage)
			{
				int bin = (int) Math.floor((voltage - minVoltage) / (maxVoltage - minVoltage) * (steps - 1));
				
				density[1][bin]++;
				
				count++;
			}
		}
		
		// Normalize y
		
		for (int i = 0; i < steps; i++)
		{
			density[1][i] /= count;
		}
		
		return density;
	}
	
	public double[][] getCurrentDensity(int steps)
	{
		List<double[]> data = getData();
		
		// Find limits
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		// Create density
		
		double[][] density = new double[2][steps];
		
		// Calculate x
		
		for (int i = 0; i < steps; i++)
		{
			density[0][i] = minCurrent + (maxCurrent - minCurrent) / steps * (i + 0.5);
		}
		
		// Calculate y
		
		int count = 0;
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			
			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && current >= minCurrent && current <= maxCurrent)
			{
				int bin = (int) Math.floor((current - minCurrent) / (maxCurrent - minCurrent) * (steps - 1));
				
				density[1][bin]++;
				
				count++;
			}
		}
		
		// Normalize y
		
		for (int i = 0; i < steps; i++)
		{
			density[1][i] /= count;
		}
		
		return density;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	private List<double[]> getData()
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

}
