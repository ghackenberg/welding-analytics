package com.hyperkit.analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class Dataset
{
	
	protected static final int TIMESTAMP_INDEX = 0;
	protected static final int VOLTAGE_INDEX = 1;
	protected static final int CURRENT_INDEX = 2;
	protected static final int RESISTANCE_INDEX = 3;
	protected static final int POWER_INDEX = 4;
	
	private java.io.File file;
	
	protected String name;
	private Color color;
	private Icon icon;
	
	protected List<double[]> data = new ArrayList<>();
	private List<double[]> activeData = new ArrayList<>();
	private Map<Integer, List<double[]>> averageActiveData = new HashMap<>();
	
	private double minTimestamp = Double.MAX_VALUE;
	private double maxTimestamp = -Double.MAX_VALUE;
	
	// Measured

	private double minTimestampMeasured = Double.MAX_VALUE;
	private double maxTimestampMeasured = -Double.MAX_VALUE;
	
	private double minVoltageMeasured = Double.MAX_VALUE;
	private double maxVoltageMeasured = -Double.MAX_VALUE;
	
	private double minCurrentMeasured = Double.MAX_VALUE;
	private double maxCurrentMeasured = -Double.MAX_VALUE;
	
	private double minResistanceMeasured = Double.MAX_VALUE;
	private double maxResistanceMeasured = -Double.MAX_VALUE;
	
	private double minPowerMeasured = Double.MAX_VALUE;
	private double maxPowerMeasured = -Double.MAX_VALUE;
	
	// Displayed

	private double minTimestampDisplayed = Double.MAX_VALUE;
	private double maxTimestampDisplayed = -Double.MAX_VALUE;
	
	private double minVoltageDisplayed = Double.MAX_VALUE;
	private double maxVoltageDisplayed = -Double.MAX_VALUE;
	
	private double minCurrentDisplayed = Double.MAX_VALUE;
	private double maxCurrentDisplayed = -Double.MAX_VALUE;
	
	private double minResistanceDisplayed = Double.MAX_VALUE;
	private double maxResistanceDisplayed = -Double.MAX_VALUE;
	
	private double minPowerDisplayed = Double.MAX_VALUE;
	private double maxPowerDisplayed = -Double.MAX_VALUE;
	
	// Percentage
	
	private double minVoltagePercentage = Double.MAX_VALUE;
	private double maxVoltagePercentage = -Double.MAX_VALUE;
	
	private double minCurrentPercentage = Double.MAX_VALUE;
	private double maxCurrentPercentage = -Double.MAX_VALUE;
	
	private double minResistancePercentage = Double.MAX_VALUE;
	private double maxResistancePercentage = -Double.MAX_VALUE;
	
	private double minPowerPercentage = Double.MAX_VALUE;
	private double maxPowerPercentage = -Double.MAX_VALUE;
	
	public Dataset(File file)
	{
		this.file = file;
		
		name = file.getAbsolutePath();
		
		color = new Color(Color.HSBtoRGB((float) Math.random(), 1f, 0.9f));
		
		updateIcon();
	}
	
	public File getFile()
	{
		return file;
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
	
	private void updateIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = image.createGraphics();
		
		graphics.setColor(color);
		graphics.fillRect(0, 0, 16, 16);
		
		icon = new ImageIcon(image);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	// Measured
	
	public int getLengthMeasured()
	{
		return data.size();
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
		return data.get(index)[RESISTANCE_INDEX];
	}
	
	public double getPowerMeasured(int index)
	{
		return data.get(index)[POWER_INDEX];
	}
	
	// Displayed
	
	public int getLengthDisplayed()
	{
		return activeData.size();
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
		return activeData.get(index)[RESISTANCE_INDEX];
	}
	
	public double getPowerDisplayed(int index)
	{
		return activeData.get(index)[POWER_INDEX];
	}
	
	// Average displayed - calculate

	private double calculateAverageDisplayed(int column, int index, int window)
	{
		double average = 0;
		double count = 0;
		
		for (int i = Math.max(index - window, 0); i < Math.min(index + window + 1, getLengthDisplayed()); i++)
		{
			double delta = i - index;
			double factor = delta / (window + 1);
			double weight = 1 - factor * factor;
			average += activeData.get(i)[column] * weight;
			count += weight;
		}
		
		return average / count;
	}

	private double calculateAverageVoltageDisplayed(int index, int window)
	{
		return calculateAverageDisplayed(VOLTAGE_INDEX, index, window);
	}
	
	private double calculateAverageCurrentDisplayed(int index, int window)
	{
		return calculateAverageDisplayed(CURRENT_INDEX, index, window);
	}
	
	private double calculateAverageResistanceDisplayed(int index, int window)
	{
		return calculateAverageDisplayed(RESISTANCE_INDEX, index, window);
	}
	
	private double calculateAveragePowerDisplayed(int index, int window)
	{
		return calculateAverageDisplayed(POWER_INDEX, index, window);
	}
	
	// Average displayed - get
	
	private double getAverageDisplayed(int column, int index, int window)
	{
		if (!averageActiveData.containsKey(window))
		{
			updateAverageActiveData(window);
		}
		
		return averageActiveData.get(window).get(index)[column];
	}
	
	public double getAverageVoltageDisplayed(int index, int window)
	{
		return getAverageDisplayed(VOLTAGE_INDEX, index, window);
	}
	
	public double getAverageCurrentDisplayed(int index, int window)
	{
		return getAverageDisplayed(CURRENT_INDEX, index, window);
	}
	
	public double getAverageResistanceDisplayed(int index, int window)
	{
		return getAverageDisplayed(RESISTANCE_INDEX, index, window);
	}
	
	public double getAveragePowerDisplayed(int index, int window)
	{
		return getAverageDisplayed(POWER_INDEX, index, window);
	}
	
	// Min/max measured
	
	private double getMinMeasured(int column, double min)
	{
		if (min == +Double.MAX_VALUE)
		{
			for (double[] item : data)
			{
				min = Math.min(min,  item[column]);
			}
		}
		return min;
	}
	
	private double getMaxMeasured(int column, double max)
	{
		if (max == -Double.MAX_VALUE)
		{
			for (double[] item : data)
			{
				max = Math.max(max,  item[column]);
			}
		}
		return max;
	}
	
	public double getMinTimestampMeasured()
	{
		return minTimestampMeasured = getMinMeasured(TIMESTAMP_INDEX, minTimestampMeasured);
	}
	
	public double getMaxTimestampMeasured()
	{
		return maxTimestampMeasured = getMaxMeasured(TIMESTAMP_INDEX, maxTimestampMeasured);
	}
	
	public double getMinVoltageMeasured()
	{
		return minVoltageMeasured = getMinMeasured(VOLTAGE_INDEX, minVoltageMeasured);
	}
	
	public double getMaxVoltageMeasured()
	{
		return maxVoltageMeasured = getMaxMeasured(VOLTAGE_INDEX, maxVoltageMeasured);
	}
	
	public double getMinCurrentMeasured()
	{
		return minCurrentMeasured = getMinMeasured(CURRENT_INDEX, minCurrentMeasured);
	}
	
	public double getMaxCurrentMeasured()
	{
		return maxCurrentMeasured = getMaxMeasured(CURRENT_INDEX, maxCurrentMeasured);
	}
	
	public double getMinResistanceMeasured()
	{
		return minResistanceMeasured = getMinMeasured(RESISTANCE_INDEX, minResistanceMeasured);
	}
	
	public double getMaxResistanceMeasured()
	{
		return maxResistanceMeasured = getMaxMeasured(RESISTANCE_INDEX, maxResistanceMeasured);
	}
	
	public double getMinPowerMeasured()
	{
		return minPowerMeasured = getMinMeasured(POWER_INDEX, minPowerMeasured);
	}
	
	public double getMaxPowerMeasured()
	{
		return maxPowerMeasured = getMaxMeasured(POWER_INDEX, maxPowerMeasured);
	}
	
	// Min/max displayed
	
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
	
	public double getMinPowerDisplayed()
	{
		if (minPowerDisplayed == Double.MAX_VALUE)
		{
			return getMinPowerMeasured();
		}
		else
		{
			return minPowerDisplayed;
		}
	}
	
	public double getMaxPowerDisplayed()
	{
		if (maxPowerDisplayed == -Double.MAX_VALUE)
		{
			return getMaxPowerMeasured();
		}
		else
		{
			return maxPowerDisplayed;
		}
	}
	
	// Min/max percentage
	
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
	
	public double getMinResistancePercentage()
	{
		if (minResistancePercentage == Double.MAX_VALUE)
		{
			return getMinResistanceDisplayed();
		}
		else
		{
			return minResistancePercentage;
		}
	}
	
	public double getMaxResistancePercentage()
	{
		if (maxResistancePercentage == -Double.MAX_VALUE)
		{
			return getMaxResistanceDisplayed();
		}
		else
		{
			return maxResistancePercentage;
		}
	}
	
	public double getMinPowerPercentage()
	{
		if (minPowerPercentage == Double.MAX_VALUE)
		{
			return getMinPowerDisplayed();
		}
		else
		{
			return minPowerPercentage;
		}
	}
	
	public double getMaxPowerPercentage()
	{
		if (maxPowerPercentage == -Double.MAX_VALUE)
		{
			return getMaxPowerDisplayed();
		}
		else
		{
			return maxPowerPercentage;
		}
	}
	
	// Set min/max displayed
	
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
	
	public void setMinResistanceDisplayed(double value)
	{
		minResistanceDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMaxResistanceDisplayed(double value)
	{
		maxResistanceDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMinPowerDisplayed(double value)
	{
		minPowerDisplayed = value;
		
		updateActiveData();
	}
	
	public void setMaxPowerDisplayed(double value)
	{
		maxPowerDisplayed = value;
		
		updateActiveData();
	}
	
	// Set min/max percentage
	
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
	
	public void setMinResistancePercentage(double value)
	{
		minResistancePercentage = value;
	}
	
	public void setMaxResistancePercentage(double value)
	{
		maxResistancePercentage = value;
	}
	
	public void setMinPowerPercentage(double value)
	{
		minPowerPercentage = value;
	}
	
	public void setMaxPowerPercentage(double value)
	{
		maxPowerPercentage = value;
	}
	
	// Get timeseries
	
	private double[][] getTimeseries(int column, int frame, int window)
	{
		int end = Math.min(frame + 1, activeData.size());
		
		int start = Math.max(frame + 1 - window, 0);
		
		int count = Math.max(0, end - start);
		
		double[][] timeseries = new double[2][count];
		
		for (int index = start; index < end; index++)
		{
			double timestamp = activeData.get(index)[TIMESTAMP_INDEX] - minTimestamp;
			double value = activeData.get(index)[column];
			
			timeseries[0][index - start] = timestamp;
			timeseries[1][index - start] = value;
		}
		
		return timeseries;
	}
	
	public double[][] getVoltageTimeseries(int frame, int window)
	{
		return getTimeseries(VOLTAGE_INDEX, frame, window);
	}
	
	public double[][] getCurrentTimeseries(int frame, int window)
	{
		return getTimeseries(CURRENT_INDEX, frame, window);
	}
	
	public double[][] getResistanceTimeseries(int frame, int window)
	{
		return getTimeseries(RESISTANCE_INDEX, frame, window);
	}
	
	public double[][] getPowerTimeseries(int frame, int window)
	{
		return getTimeseries(POWER_INDEX, frame, window);
	}
	
	// Get density
	
	private double[][] getDensity(int column, double min, double max, int steps)
	{		
		// Create density
		
		double[][] density = new double[2][steps];
		
		// Calculate x
		
		for (int i = 0; i < steps; i++)
		{
			density[0][i] = min + (max - min) / steps * (i + 0.5);
		}
		
		// Calculate y
		
		int count = 0;
		
		for (double[] line : activeData)
		{
			double value = line[column];
			
			int bin = Math.min((int) Math.floor((value - min) / (max - min) * steps), steps - 1);
			
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
	
	public double[][] getVoltageDensity(int steps)
	{
		return getDensity(VOLTAGE_INDEX, getMinVoltageDisplayed(), getMaxVoltageDisplayed(), steps);
	}
	
	public double[][] getCurrentDensity(int steps)
	{
		return getDensity(CURRENT_INDEX, getMinCurrentDisplayed(), getMaxCurrentDisplayed(), steps);
	}
	
	public double[][] getResistanceDensity(int steps)
	{
		return getDensity(RESISTANCE_INDEX, getMinResistanceDisplayed(), getMaxResistanceDisplayed(), steps);
	}
	
	public double[][] getPowerDensity(int steps)
	{
		return getDensity(POWER_INDEX, getMinPowerDisplayed(), getMaxPowerDisplayed(), steps);
	}
	
	// Get probability
	
	private double getProbability(int column, double[][] density, double min, double max, int steps, int frame)
	{
		double value = activeData.get(frame)[column];
		
		int bin = Math.min((int) Math.floor((value - min) / (max - min) * steps), steps - 1);
		
		return density[1][bin];
	}
	
	public double getVoltageProbability(int steps, int frame)
	{
		return getProbability(VOLTAGE_INDEX, getVoltageDensity(steps), getMinVoltageDisplayed(), getMaxVoltageDisplayed(), steps, frame);
	}
	
	public double getCurrentProbability(int steps, int frame)
	{
		return getProbability(CURRENT_INDEX, getCurrentDensity(steps), getMinCurrentDisplayed(), getMaxCurrentDisplayed(), steps, frame);
	}
	
	public double getResistanceProbability(int steps, int frame)
	{
		return getProbability(RESISTANCE_INDEX, getResistanceDensity(steps), getMinResistanceDisplayed(), getMaxResistanceDisplayed(), steps, frame);
	}
	
	public double getPowerProbability(int steps, int frame)
	{
		return getProbability(POWER_INDEX, getPowerDensity(steps), getMinPowerDisplayed(), getMaxPowerDisplayed(), steps, frame);
	}
	
	// Get key
	
	private String getTimestampKey()
	{
		return minTimestampDisplayed + "/" + maxTimestampDisplayed;
	}
	
	private String getVoltageKey()
	{
		return minVoltageDisplayed + "/" + maxVoltageDisplayed;
	}
	
	private String getCurrentKey()
	{
		return minCurrentDisplayed + "/" + maxCurrentDisplayed;
	}
	
	private String getResistanceKey()
	{
		return minResistanceDisplayed + "/" + maxResistanceDisplayed;
	}
	
	private String getPowerKey()
	{
		return minPowerDisplayed + "/" + maxPowerDisplayed;
	}
	
	private String getKey()
	{
		return getTimestampKey() + "/" + getVoltageKey() + "/" + getCurrentKey() + "/" + getResistanceKey() + "/" + getPowerKey();
	}
	
	private String getKey(int column, double min, double max)
	{
		return getKey() + "/" + column + "/" + min + "/" + max;
	}
	
	// Get percentage
	
	private Map<String, Double> percentages = new HashMap<>();
	
	private double getPercentage(int column, double min, double max)
	{
		String key = getKey(column, min, max);
		
		if (!percentages.containsKey(key))
		{
			int count = 0;
			
			for (double[] line : activeData)
			{
				double value = line[column];
				
				if (value >= min && value <= max)
				{
					count++;
				}
			}
			
			percentages.put(key, count * 100.0 / data.size());
		}
		
		return percentages.get(key);
	}
	
	public double getVoltagePercentage()
	{
		return getPercentage(VOLTAGE_INDEX, getMinVoltagePercentage(), getMaxVoltagePercentage());
	}
	
	public double getCurrentPercentage()
	{
		return getPercentage(CURRENT_INDEX, getMinCurrentPercentage(), getMaxCurrentPercentage());
	}
	
	public double getResistancePercentage()
	{
		return getPercentage(RESISTANCE_INDEX, getMinResistancePercentage(), getMaxResistancePercentage());
	}
	
	public double getPowerPercentage()
	{
		return getPercentage(POWER_INDEX, getMinPowerPercentage(), getMaxPowerPercentage());
	}
	
	// Get mean
	
	private Map<String, Double> means = new HashMap<>();
	
	private double getMean(int column, double min, double max)
	{
		String key = getKey(column, min, max);
		
		if (!means.containsKey(key))
		{
			int count = getCount(column, min, max);
			
			double result = 0;
			
			for (double[] line : activeData)
			{
				double value = line[column];
	
				if (value >= min && value <= max)
				{
					result += value / count;
				}
			}
			
			means.put(key, result);
		}
		
		return means.get(key);
	}
	
	public double getMeanVoltage()
	{
		return getMean(VOLTAGE_INDEX, getMinVoltagePercentage(), getMaxVoltagePercentage());
	}
	
	public double getMeanCurrent()
	{
		return getMean(CURRENT_INDEX, getMinCurrentPercentage(), getMaxCurrentPercentage());
	}
	
	public double getMeanResistance()
	{
		return getMean(RESISTANCE_INDEX, getMinResistancePercentage(), getMaxResistancePercentage());
	}
	
	public double getMeanPower()
	{
		return getMean(POWER_INDEX, getMinPowerPercentage(), getMaxPowerPercentage());
	}
	
	// Get stdev
	
	private Map<String, Double> stdevs = new HashMap<>();
	
	private double getStdev(int column, double min, double max, double mean)
	{
		String key = getKey(column, min, max);
		
		if (!stdevs.containsKey(key))
		{
			int count = getCount(column, min, max);
			
			double stdev = 0;
			
			for (double[] line : activeData)
			{
				double value = line[column];
	
				if (value >= min && value <= max)
				{
					double delta = value - mean;
					
					stdev += Math.sqrt(delta * delta) / count;
				}
			}
			
			stdevs.put(key, stdev);
		}
		
		return stdevs.get(key);
	}
	
	public double getStdevVoltage()
	{
		return getStdev(VOLTAGE_INDEX, getMinVoltagePercentage(), getMaxVoltagePercentage(), getMeanVoltage());
	}
	
	public double getStdevCurrent()
	{
		return getStdev(CURRENT_INDEX, getMinCurrentPercentage(), getMaxCurrentPercentage(), getMeanCurrent());
	}

	public double getStdevResistance()
	{
		return getStdev(RESISTANCE_INDEX, getMinResistancePercentage(), getMaxResistancePercentage(), getMeanResistance());
	}

	public double getStdevPower()
	{
		return getStdev(POWER_INDEX, getMinPowerPercentage(), getMaxPowerPercentage(), getMeanPower());
	}
	
	// Get count
	
	private Map<String, Integer> counts = new HashMap<>();
	
	private int getCount(int column, double min, double max)
	{
		String key = getKey(column, min, max);
		
		if (!counts.containsKey(key))
		{
			int count = 0;
			
			for (double[] line : activeData)
			{
				double value = line[column];
				
				if (value >= min && value <= max)
				{
					count++;
				}
			}
			
			counts.put(key, count);
		}
		
		return counts.get(key);
	}
	
	// Get median
	
	private Map<String, Double> medians = new HashMap<>();
	
	private double getMedian(int column, double min, double max)
	{
		String key = getKey(column, min, max);
		
		if (!medians.containsKey(key))
		{
			List<Double> values = new ArrayList<>();
			
			for (double[] line : activeData)
			{
				double value = line[column];
	
				if (value >= min && value <= max)
				{
					values.add(value);
				}
			}
			
			values.sort((a, b) ->
			{
				double delta = a - b;
				
				if (delta < 0)
				{
					return -1;
				}
				else if (delta > 0)
				{
					return 1;
				}
				else
				{
					return 0;
				}
			});
			
			medians.put(key, values.get((int) Math.floor(values.size() / 2)));
		}
		
		return medians.get(key);
	}
	
	public double getMedianVoltage()
	{
		return getMedian(VOLTAGE_INDEX, getMinVoltagePercentage(), getMaxVoltagePercentage());
	}
	
	public double getMedianCurrent()
	{
		return getMedian(CURRENT_INDEX, getMinCurrentPercentage(), getMaxCurrentPercentage());
	}
	
	public double getMedianResistance()
	{
		return getMedian(RESISTANCE_INDEX, getMinResistancePercentage(), getMaxResistancePercentage());
	}
	
	public double getMedianPower()
	{
		return getMedian(POWER_INDEX, getMinPowerPercentage(), getMaxPowerPercentage());
	}
	
	// Get median
	
	private Map<String, Double> modes = new HashMap<>();
	
	private double getMode(int column, double min, double max)
	{
		String key = getKey(column, min, max);
		
		if (!modes.containsKey(key))
		{
			Map<Double, Integer> values = new HashMap<>();
			
			double mode = 0;
			double count = 0;
			
			for (double[] line : activeData)
			{
				double value = line[column];
	
				if (value >= min && value <= max)
				{
					// Update value
					if (values.containsKey(value)) {
						values.put(value, values.get(value) + 1);
					} else {
						values.put(value, 1);
					}
					// Update mode
					if (values.get(value) > count) {
						mode = value;
						count = values.get(value);
					}
				}
			}
			
			modes.put(key, mode);
		}
		
		return modes.get(key);
	}
	
	public double getModeVoltage()
	{
		return getMode(VOLTAGE_INDEX, getMinVoltagePercentage(), getMaxVoltagePercentage());
	}
	
	public double getModeCurrent()
	{
		return getMode(CURRENT_INDEX, getMinCurrentPercentage(), getMaxCurrentPercentage());
	}
	
	public double getModeResistance()
	{
		return getMode(RESISTANCE_INDEX, getMinResistancePercentage(), getMaxResistancePercentage());
	}
	
	public double getModePower()
	{
		return getMode(POWER_INDEX, getMinPowerPercentage(), getMaxPowerPercentage());
	}
	
	// Get root mean square
	
	private Map<String, Double> rootMeanSquares = new HashMap<>();
	
	private double getRootMeanSquare(int column, double min, double max)
	{
		String key = getKey(column, min, max);
		
		if (!rootMeanSquares.containsKey(key))
		{
			int count = getCount(column, min, max);
			
			double result = 0;
			
			for (double[] line : activeData)
			{
				double value = line[column];
	
				if (value >= min && value <= max)
				{
					result += value * value / count;
				}
			}
			
			rootMeanSquares.put(key, Math.sqrt(result));
		}
		
		return rootMeanSquares.get(key);
	}
	
	public double getRootMeanSquareVoltage()
	{
		return getRootMeanSquare(VOLTAGE_INDEX, getMinVoltagePercentage(), getMaxVoltagePercentage());
	}
	
	public double getRootMeanSquareCurrent()
	{
		return getRootMeanSquare(CURRENT_INDEX, getMinCurrentPercentage(), getMaxCurrentPercentage());
	}
	
	public double getRootMeanSquareResistance()
	{
		return getRootMeanSquare(RESISTANCE_INDEX, getMinResistancePercentage(), getMaxResistancePercentage());
	}
	
	public double getRootMeanSquarePower()
	{
		return getRootMeanSquare(POWER_INDEX, getMinPowerPercentage(), getMaxPowerPercentage());
	}
	
	// Get other
	
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
	
	protected void cleanData()
	{
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
	}
	
	protected void updateActiveData()
	{		
		// Find limits
		
		double minTimestamp = getMinTimestampDisplayed();
		double maxTimestamp = getMaxTimestampDisplayed();
		
		double minCurrent = getMinCurrentDisplayed();
		double maxCurrent = getMaxCurrentDisplayed();
		
		double minVoltage = getMinVoltageDisplayed();
		double maxVoltage = getMaxVoltageDisplayed();
		
		double minResistance = getMinResistanceDisplayed();
		double maxResistance = getMaxResistanceDisplayed();
		
		double minPower = getMinPowerDisplayed();
		double maxPower = getMaxPowerDisplayed();
		
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
			double resistance = line[RESISTANCE_INDEX];
			double power = line[POWER_INDEX];
			
			boolean timestampOk = timestamp >= minTimestamp && timestamp <= maxTimestamp;
			boolean currentOk = current >= minCurrent && current <= maxCurrent;
			boolean voltageOk = voltage >= minVoltage && voltage <= maxVoltage;
			boolean resistanceOk = resistance >= minResistance && resistance <= maxResistance;
			boolean powerOk = power >= minPower && power <= maxPower;

			if (timestampOk && currentOk && voltageOk && resistanceOk && powerOk)
			{	
				activeData.add(line);
				
				this.minTimestamp = Math.min(this.minTimestamp, timestamp);
				this.maxTimestamp = Math.max(this.maxTimestamp, timestamp);
			}
		}
		
		averageActiveData.clear();
	}
	
	private void updateAverageActiveData(int window)
	{
		List<double[]> data = new ArrayList<>();
		
		for (int index = 0; index < getLengthDisplayed(); index++)
		{
			double[] line = new double[5];
			
			line[TIMESTAMP_INDEX] = activeData.get(index)[TIMESTAMP_INDEX];
			line[CURRENT_INDEX] = calculateAverageCurrentDisplayed(index, window);
			line[VOLTAGE_INDEX] = calculateAverageVoltageDisplayed(index, window);
			line[RESISTANCE_INDEX] = calculateAverageResistanceDisplayed(index, window);
			line[POWER_INDEX] = calculateAveragePowerDisplayed(index, window);
			
			data.add(line);
		}
		
		averageActiveData.put(window, data);
	}

}
