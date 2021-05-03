package com.hyperkit.analysis.files;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.File;
import com.hyperkit.analysis.events.values.ProgressChangeEvent;

public class ASDFile extends File
{
	
	private static final int TIMESTAMP_INDEX = 0;
	private static final int VOLTAGE_INDEX = 1;
	private static final int CURRENT_INDEX = 2;
	
	private String name;
	private Color color;
	private Icon icon;
	
	private List<double[]> data;
	private List<double[]> activeData;

	private double minTimestampMeasured = Double.MAX_VALUE;
	private double maxTimestampMeasured = -Double.MAX_VALUE;
	
	private double minVoltageMeasured = Double.MAX_VALUE;
	private double maxVoltageMeasured = -Double.MAX_VALUE;
	
	private double minCurrentMeasured = Double.MAX_VALUE;
	private double maxCurrentMeasured = -Double.MAX_VALUE;
	
	private double minResistanceMeasured = Double.MAX_VALUE;
	private double maxResistanceMeasured = -Double.MAX_VALUE;

	private double minTimestampDisplayed = Double.MAX_VALUE;
	private double maxTimestampDisplayed = -Double.MAX_VALUE;
	
	private double minVoltageDisplayed = Double.MAX_VALUE;
	private double maxVoltageDisplayed = -Double.MAX_VALUE;
	
	private double minCurrentDisplayed = Double.MAX_VALUE;
	private double maxCurrentDisplayed = -Double.MAX_VALUE;
	
	private double minResistanceDisplayed = Double.MAX_VALUE;
	private double maxResistanceDisplayed = -Double.MAX_VALUE;
	
	private double minTimestamp = Double.MAX_VALUE;
	private double maxTimestamp = -Double.MAX_VALUE;
	
	private double minVoltagePercentage = Double.MAX_VALUE;
	private double maxVoltagePercentage = -Double.MAX_VALUE;
	
	private double minCurrentPercentage = Double.MAX_VALUE;
	private double maxCurrentPercentage = -Double.MAX_VALUE;

	public ASDFile(java.io.File file) throws IOException
	{
		super(file);
		
		name = file.getAbsolutePath();
		color = new Color(Color.HSBtoRGB((float) Math.random(), 1f, 0.9f));
		updateIcon();
		
		data = new ArrayList<>();
		activeData = new ArrayList<>();
		
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
				else if (parts.length == 4)
				{
					double first = parseDouble(parts[0]);
					double second = parseDouble(parts[3]);
					double third = parseDouble(parts[2]);
					
					data.add(new double[] {first, second, third});
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
		
		// Update timestamps
		
		double minTimestamp = Double.MAX_VALUE;
		
		for (double[] dataLine : data)
		{
			minTimestamp = Math.min(minTimestamp, dataLine[TIMESTAMP_INDEX]);
		}
		
		// System.out.println(minTimestamp);
		
		for (double[] dataLine : data)
		{
			dataLine[TIMESTAMP_INDEX] -= minTimestamp;
		}
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(100));
		
		// Update active data
		
		updateActiveData();
	}
	
	public String getName()
	{
		return name;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public Color getMarkerColor()
	{
		return new Color((int) (color.getRed() * 0.8), (int) (color.getGreen() * 0.8), (int) (color.getBlue() * 0.8));
	}
	
	public void setColor(Color color)
	{
		this.color = color;
		
		updateIcon();
	}
	
	public Icon getIcon()
	{
		return icon;
	}
	
	public int getLengthMeasured()
	{
		return data.size();
	}
	
	public int getLengthDisplayed()
	{
		return activeData.size();
	}
	
	public double getTimestampMeasured(int index)
	{
		return data.get(index)[TIMESTAMP_INDEX];
	}
	
	public double getVoltageMeasured(int index)
	{
		return data.get(index)[VOLTAGE_INDEX];
	}
	
	public double getCurrentMeasured(int index)
	{
		return data.get(index)[CURRENT_INDEX];
	}
	
	public double getResistanceMeasured(int index)
	{
		return getVoltageMeasured(index) / getCurrentMeasured(index);
	}
	
	public double getTimestampDisplayed(int index)
	{
		return activeData.get(index)[TIMESTAMP_INDEX] - minTimestamp;
	}
	
	public double getVoltageDisplayed(int index)
	{
		return activeData.get(index)[VOLTAGE_INDEX];
	}
	
	public double getCurrentDisplayed(int index)
	{
		return activeData.get(index)[CURRENT_INDEX];
	}
	
	public double getResistanceDisplayed(int index)
	{
		return getVoltageDisplayed(index) / getCurrentDisplayed(index);
	}
	
	public double getMinTimestampMeasured()
	{
		if (minTimestampMeasured == Double.MAX_VALUE)
		{
			for (double[] item : data)
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
			for (double[] item : data)
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
			for (double[] item : data)
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
			for (double[] item : data)
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
			for (double[] item : data)
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
			for (double[] item : data)
			{
				maxCurrentMeasured = Math.max(maxCurrentMeasured, item[CURRENT_INDEX]);
			}
		}
		
		return maxCurrentMeasured;
	}
	
	public double getMinResistanceMeasured()
	{
		if (minResistanceMeasured == Double.MAX_VALUE)
		{
			for (int index = 0; index < data.size(); index++)
			{
				minResistanceMeasured = Math.min(minResistanceMeasured, getResistanceMeasured(index));
			}
		}
		
		return minResistanceMeasured;
	}
	
	public double getMaxResistanceMeasured()
	{
		if (maxResistanceMeasured == -Double.MAX_VALUE)
		{
			for (int index = 0; index < data.size(); index++)
			{
				maxResistanceMeasured = Math.max(maxResistanceMeasured, getResistanceMeasured(index));
			}
		}
		
		return maxResistanceMeasured;
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
	
	public double getMinResistanceDisplayed()
	{
		if (minResistanceDisplayed == Double.MAX_VALUE)
		{
			return getMinResistanceMeasured();
		}
		else
		{
			return minResistanceDisplayed;
		}
	}
	
	public double getMaxResistanceDisplayed()
	{
		if (maxResistanceDisplayed == -Double.MAX_VALUE)
		{
			return getMaxResistanceMeasured();
		}
		else
		{
			return maxResistanceDisplayed;
		}
	}
	
	public double getMinVoltagePercentage()
	{
		if (minVoltagePercentage == Double.MAX_VALUE)
		{
			return getMinVoltageDisplayed();
		}
		else
		{
			return minVoltagePercentage;
		}
	}
	
	public double getMaxVoltagePercentage()
	{
		if (maxVoltagePercentage == -Double.MAX_VALUE)
		{
			return getMaxVoltageDisplayed();
		}
		else
		{
			return maxVoltagePercentage;
		}
	}
	
	public double getMinCurrentPercentage()
	{
		if (minCurrentPercentage == Double.MAX_VALUE)
		{
			return getMinCurrentDisplayed();
		}
		else
		{
			return minCurrentPercentage;
		}
	}
	
	public double getMaxCurrentPercentage()
	{
		if (maxCurrentPercentage == -Double.MAX_VALUE)
		{
			return getMaxCurrentDisplayed();
		}
		else
		{
			return maxCurrentPercentage;
		}
	}
	
	public void setMinTimestampDisplayed(double value)
	{
		minTimestampDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMaxTimestampDisplayed(double value)
	{
		maxTimestampDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMinVoltageDisplayed(double value)
	{
		minVoltageDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMaxVoltageDisplayed(double value)
	{
		maxVoltageDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMinCurrentDisplayed(double value)
	{
		minCurrentDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMaxCurrentDisplayed(double value)
	{
		maxCurrentDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMinVoltagePercentage(double value)
	{
		minVoltagePercentage = value;
	}
	
	public void setMaxVoltagePercentage(double value)
	{
		maxVoltagePercentage = value;
	}
	
	public void setMinCurrentPercentage(double value)
	{
		minCurrentPercentage = value;
	}
	
	public void setMaxCurrentPercentage(double value)
	{
		maxCurrentPercentage = value;
	}
	
	public double[][] getVoltageTimeseries(int frame, int window)
	{
		int end = Math.min(frame + 1, activeData.size());
		
		int start = Math.max(frame + 1 - window, 0);
		
		int count = Math.max(0, end - start);
		
		double[][] timeseries = new double[2][count];
		
		for (int index = start; index < end; index++)
		{
			double timestamp = activeData.get(index)[TIMESTAMP_INDEX] - minTimestamp;
			double voltage = activeData.get(index)[VOLTAGE_INDEX];
			
			timeseries[0][index - start] = timestamp;
			timeseries[1][index - start] = voltage;
		}
		
		return timeseries;
	}
	
	public double[][] getCurrentTimeseries(int frame, int window)
	{
		int end = Math.min(frame + 1, activeData.size());
		
		int start = Math.max(frame + 1 - window, 0);
		
		int count = Math.max(0, end - start);
		
		double[][] timeseries = new double[2][count];

		for (int index = start; index < end; index++)
		{
			double timestamp = activeData.get(index)[TIMESTAMP_INDEX] - minTimestamp;
			double current = activeData.get(index)[CURRENT_INDEX];
			
			timeseries[0][index - start] = timestamp;
			timeseries[1][index - start] = current;
		}
		
		return timeseries;
	}
	
	public double[][] getVoltageDensity(int steps)
	{
		// Find limits
		
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
		
		for (double[] line : activeData)
		{
			double voltage = line[VOLTAGE_INDEX];
			
			int bin = Math.min((int) Math.floor((voltage - minVoltage) / (maxVoltage - minVoltage) * steps), steps - 1);
			
			density[1][bin]++;
			
			count++;
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
		// Find limits
		
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
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			
			int bin = Math.min((int) Math.floor((current - minCurrent) / (maxCurrent - minCurrent) * steps), steps - 1);
			
			density[1][bin]++;
			
			count++;
		}
		
		// Normalize y
		
		for (int i = 0; i < steps; i++)
		{
			density[1][i] /= count;
		}
		
		return density;
	}
	
	public double getCurrentProbability(int steps, int frame)
	{
		double[][] density = getCurrentDensity(steps);
		
		double min = getMinCurrentDisplayed();
		double max = getMaxCurrentDisplayed();
		
		double value = activeData.get(frame)[CURRENT_INDEX];
		
		int bin = Math.min((int) Math.floor((value - min) / (max - min) * steps), steps - 1);
		
		return density[1][bin];
	}
	
	public double getVoltageProbability(int steps, int frame)
	{
		double[][] density = getVoltageDensity(steps);
		
		double min = getMinVoltageDisplayed();
		double max = getMaxVoltageDisplayed();
		
		double value = activeData.get(frame)[VOLTAGE_INDEX];
		
		int bin = Math.min((int) Math.floor((value - min) / (max - min) * steps), steps - 1);
		
		return density[1][bin];
	}
	
	public double[][] getCurrentVoltage(int frame, int window_backward)
	{
		// Find count
		
		int start = Math.max(frame + 1 - window_backward, 0);
		
		int end = Math.min(frame + 1, activeData.size());
		
		int count = Math.max(0, end - start);
		
		// Create point cloud
		
		double[][] result = new double[2][count];
		
		// Fill dataset
		
		for (int index = start; index < end; index++)
		{
			double current = activeData.get(index)[CURRENT_INDEX];
			double voltage = activeData.get(index)[VOLTAGE_INDEX];
			
			result[0][index - start] = current;
			result[1][index - start] = voltage;
		}
		
		// Return result
		
		return result;
	}
	
	public double[][] getCurrentVoltageMin(int steps)
	{
		// Find limits
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		// Create map
		
		Map<Integer, Double> intermediate = new HashMap<>();
		
		// Calculate map
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
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
		
		// Calculate result
		
		double[][] result = new double[2][intermediate.size()];
		
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
		// Find limits
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		// Create map
		
		Map<Integer, Double> intermediate = new HashMap<>();
		
		// Calculate map
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
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
		
		// Calculate result
		
		double[][] result = new double[2][intermediate.size()];
		
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
		// Find limits
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		// Create map
		
		Map<Integer, Double> intermediate = new HashMap<>();
		Map<Integer, Integer> counts = new HashMap<>();
		
		// Calculate map
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
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
		
		// Calculate result
		
		double[][] result = new double[2][intermediate.size()];
		
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
	
	public SimpleRegression getRegression()
	{
		// Build data
		
		double[][] intermediate = new double[activeData.size()][2];
		
		int index = 0;

		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			intermediate[index][0] = current;
			intermediate[index][1] = voltage;
			
			index++;
		}
		
		// Build regression
		
		SimpleRegression regression = new SimpleRegression(true);
		
		regression.addData(intermediate);
		
		return regression;
	}
	
	public double getMeanPower()
	{
		// Calculate result
		
		double result = 0;
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];
			
			result += current * voltage / activeData.size();
		}
		
		return result;
	}
	
	public double getMeanCurrent()
	{
		// Calculate result
		
		double result = 0;
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			
			result += current / activeData.size();
		}
		
		return result;
	}
	
	public double getMeanVoltage() {
		// Calculate result
		
		double result = 0;
		
		for (double[] line : activeData)
		{
			double voltage = line[VOLTAGE_INDEX];
			
			result += voltage / activeData.size();
		}
		
		return result;
	}
	
	public double getRootMeanSquareCurrent()
	{	
		// Calculate result
		
		double result = 0;
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			
			result += current * current / activeData.size();
		}
		
		return Math.sqrt(result);
	}
	
	public double getRootMeanSquareVoltage()
	{
		// Calculate result
		
		double result = 0;
		
		for (double[] line : activeData)
		{
			double voltage = line[VOLTAGE_INDEX];
			
			result += voltage * voltage / activeData.size();
		}
		
		return Math.sqrt(result);
	}
	
	public double getVoltagePercentage()
	{
		double min = getMinVoltagePercentage();
		double max = getMaxVoltagePercentage();
		
		int count = 0;
		
		for (double[] line : activeData)
		{
			double voltage = line[VOLTAGE_INDEX];
			
			if (voltage >= min && voltage <= max)
			{
				count++;
			}
		}
		
		return count * 100.0 / activeData.size();
	}
	
	public double getCurrentPercentage()
	{
		double min = getMinCurrentPercentage();
		double max = getMaxCurrentPercentage();
		
		int count = 0;
		
		for (double[] line : activeData)
		{
			double current = line[CURRENT_INDEX];
			
			if (current >= min && current <= max)
			{
				count++;
			}
		}
		
		return count * 100.0 / activeData.size();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	private void updateActiveData()
	{
		// Find limits
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		double minVoltage = getMinVoltageDisplayed();
		double maxVoltage = getMaxVoltageDisplayed();
		
		// Clear active data
		
		activeData.clear();
		
		this.minTimestamp = Double.MAX_VALUE;
		this.maxTimestamp = -Double.MAX_VALUE;
		
		// Add active data
		
		for (double[] line : data)
		{
			double timestamp = line[TIMESTAMP_INDEX];
			double current = line[CURRENT_INDEX];
			double voltage = line[VOLTAGE_INDEX];

			if (timestamp >= minTimestamp && timestamp <= maxTimestamp && current >= minCurrent && current <= maxCurrent && voltage >= minVoltage && voltage <= maxVoltage)
			{	
				activeData.add(line);
				
				this.minTimestamp = Math.min(this.minTimestamp, timestamp);
				this.maxTimestamp = Math.max(this.maxTimestamp, timestamp);
			}
		}
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
	
	private void updateIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = image.createGraphics();
		
		graphics.setColor(color);
		graphics.fillRect(0, 0, 16, 16);
		
		icon = new ImageIcon(image);
	}

}
