package com.hyperkit.analysis.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.File;
import com.hyperkit.analysis.events.ProgressChangeEvent;

public class ASDFile extends File
{
	
	private static final int TIMESTAMP_INDEX = 0;
	private static final int VOLTAGE_INDEX = 1;
	private static final int CURRENT_INDEX = 2;
	
	private String name;
	
	private List<double[]> data;

	private double minTimestampMeasured = Double.MAX_VALUE;
	private double maxTimestampMeasured = -Double.MAX_VALUE;
	
	private double minVoltageMeasured = Double.MAX_VALUE;
	private double maxVoltageMeasured = -Double.MAX_VALUE;
	
	private double minCurrentMeasured = Double.MAX_VALUE;
	private double maxCurrentMeasured = -Double.MAX_VALUE;

	private double minTimestampDisplayed = Double.MAX_VALUE;
	private double maxTimestampDisplayed = -Double.MAX_VALUE;
	
	private double minVoltageDisplayed = Double.MAX_VALUE;
	private double maxVoltageDisplayed = -Double.MAX_VALUE;
	
	private double minCurrentDisplayed = Double.MAX_VALUE;
	private double maxCurrentDisplayed = -Double.MAX_VALUE;

	public ASDFile(java.io.File file) throws IOException
	{
		super(file);
		
		name = file.getAbsolutePath();
		data = new ArrayList<>();
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(0));
		
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
					
					data.add(new double[] {first, second, third});
				}
			}
			catch (NumberFormatException | ParseException e)
			{
				
			}
			
			int temp = (int) Math.floor(1.0 * position / size * 100);
			
			if (temp != progress)
			{
				progress = temp;
				
				Bus.getInstance().broadcastEvent(new ProgressChangeEvent(progress));
			}
		}
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(100));
		
		reader.close();
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<double[]> getData()
	{
		return data;
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
		if (maxTimestampMeasured == -Double.MAX_VALUE)
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
		if (maxVoltageMeasured == -Double.MAX_VALUE)
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
		if (maxCurrentMeasured == -Double.MAX_VALUE)
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
		if (maxTimestampDisplayed == -Double.MAX_VALUE)
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
		if (maxVoltageDisplayed == -Double.MAX_VALUE)
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
		if (maxCurrentDisplayed == -Double.MAX_VALUE)
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
	
	public double[][] getVoltageTimeseries()
	{
		List<double[]> data = getData();
		
		int length = 0;

		for (int i = 0; i < data.size(); i++)
		{
			double timestamp = data.get(i)[TIMESTAMP_INDEX];
			
			if (timestamp >= getMinTimestampDisplayed() && timestamp <= getMaxTimestampDisplayed())
			{
				length++;
			}
		}
		
		int index = 0;
		
		double[][] timeseries = new double[2][length];
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			if (timestamp >= getMinTimestampDisplayed() && timestamp <= getMaxTimestampDisplayed())
			{
				timeseries[0][index] = timestamp;
				timeseries[1][index] = voltage;
				
				index++;
			}
		}
		
		return timeseries;
	}
	
	public double[][] getCurrentTimeseries()
	{
		List<double[]> data = getData();
		
		int length = 0;

		for (int i = 0; i < data.size(); i++)
		{
			double timestamp = data.get(i)[TIMESTAMP_INDEX];
			
			if (timestamp >= getMinTimestampDisplayed() && timestamp <= getMaxTimestampDisplayed())
			{
				length++;
			}
		}
		
		int index = 0;
		
		double[][] timeseries = new double[2][length];
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			
			if (timestamp >= getMinTimestampDisplayed() && timestamp <= getMaxTimestampDisplayed())
			{
				timeseries[0][index] = timestamp;
				timeseries[1][index] = current;
				
				index++;
			}
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
	
	public double[][] getPointCloud()
	{
		List<double[]> data = getData();
		
		// Find limits
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		double minVoltage = getMinVoltageDisplayed();
		double maxVoltage = getMaxVoltageDisplayed();
		
		// Find count

		int count = 0;
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && current >= minCurrent && current <= maxCurrent && voltage >= minVoltage && voltage <= maxVoltage)
			{
				count++;
			}
		}
		
		count = Math.min(count, 20000);
		
		// Create point cloud
		
		double[][] result = new double[2][count];
		
		// Fill dataset
		
		int index = 0;
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && current >= minCurrent && current <= maxCurrent && voltage >= minVoltage && voltage <= maxVoltage)
			{
				result[0][index] = current;
				result[1][index] = voltage;
				
				index++;
			}
			
			if (index == count)
			{
				break;
			}
		}
		
		// Return result
		
		return result;
	}
	
	public double[][] getCurrentVoltageMin(int steps)
	{
		List<double[]> data = getData();
		
		// Find limits
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		double minVoltage = getMinVoltageDisplayed();
		double maxVoltage = getMaxVoltageDisplayed();
		
		// Create map
		
		Map<Integer, Double> intermediate = new HashMap<>();
		
		// Calculate map
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && current >= minCurrent && current <= maxCurrent && voltage >= minVoltage && voltage <= maxVoltage)
			{
				int bin = (int) Math.floor((current - minCurrent) / (maxCurrent - minCurrent) * (steps - 1));
				
				if (!intermediate.containsKey(bin))
				{
					intermediate.put(bin, voltage);
				}
				else
				{
					intermediate.put(bin, Math.min(intermediate.get(bin), voltage));
				}
			}
		}
		
		// Create array
		
		double[][] result = new double[2][intermediate.size()];
		
		// Calculate array
		
		int index = 0;
		
		for (int step = 0; step < steps; step++)
		{
			if (intermediate.containsKey(step))
			{
				result[0][index] = minCurrent + (maxCurrent - minCurrent) / steps * (step + 0.5);
				result[1][index] = intermediate.get(step);
				
				index++;
			}
		}
		
		// Return result
		
		return result;
	}
	
	public double[][] getCurrentVoltageMax(int steps)
	{
		List<double[]> data = getData();
		
		// Find limits
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		double minVoltage = getMinVoltageDisplayed();
		double maxVoltage = getMaxVoltageDisplayed();
		
		// Create map
		
		Map<Integer, Double> intermediate = new HashMap<>();
		
		// Calculate map
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && current >= minCurrent && current <= maxCurrent && voltage >= minVoltage && voltage <= maxVoltage)
			{
				int bin = (int) Math.floor((current - minCurrent) / (maxCurrent - minCurrent) * (steps - 1));
				
				if (!intermediate.containsKey(bin))
				{
					intermediate.put(bin, voltage);
				}
				else
				{
					intermediate.put(bin, Math.max(intermediate.get(bin), voltage));
				}
			}
		}
		
		// Create array
		
		double[][] result = new double[2][intermediate.size()];
		
		// Calculate array
		
		int index = 0;
		
		for (int step = 0; step < steps; step++)
		{
			if (intermediate.containsKey(step))
			{
				result[0][index] = minCurrent + (maxCurrent - minCurrent) / steps * (step + 0.5);
				result[1][index] = intermediate.get(step);
				
				index++;
			}
		}
		
		// Return result
		
		return result;
	}
	
	public double[][] getCurrentVoltageAvg(int steps)
	{
		List<double[]> data = getData();
		
		// Find limits
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		double minVoltage = getMinVoltageDisplayed();
		double maxVoltage = getMaxVoltageDisplayed();
		
		// Create map
		
		Map<Integer, Double> intermediate = new HashMap<>();
		Map<Integer, Integer> counts = new HashMap<>();
		
		// Calculate map
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && current >= minCurrent && current <= maxCurrent && voltage >= minVoltage && voltage <= maxVoltage)
			{
				int bin = (int) Math.floor((current - minCurrent) / (maxCurrent - minCurrent) * (steps - 1));
				
				if (!intermediate.containsKey(bin))
				{
					intermediate.put(bin, voltage);
					counts.put(bin, 1);
				}
				else
				{
					intermediate.put(bin, intermediate.get(bin) + voltage);
					counts.put(bin, counts.get(bin) + 1);
				}
			}
		}
		
		// Create array
		
		double[][] result = new double[2][intermediate.size()];
		
		// Calculate array
		
		int index = 0;
		
		for (int step = 0; step < steps; step++)
		{
			if (intermediate.containsKey(step))
			{
				result[0][index] = minCurrent + (maxCurrent - minCurrent) / steps * (step + 0.5);
				result[1][index] = intermediate.get(step) / counts.get(step);
				
				index++;
			}
		}
		
		// Return result
		
		return result;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	private double parseDouble(String string) throws NumberFormatException, ParseException
	{
		String[] parts = string.split("e");
		
		if (parts.length != 2)
		{
			throw new NumberFormatException("Number format should be <x>e<y>.");
		}
		
		NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
		
		double number = format.parse(parts[0]).doubleValue();
		double exponent = format.parse(parts[1]).intValue();
		
		return number * Math.pow(10, exponent);
	}

}
