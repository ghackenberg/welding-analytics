package com.hyperkit.analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class Dataset
{
	
	private java.io.File file;
	
	private String name;
	private Color color;
	private Icon icon;
	
	public Dataset(java.io.File file)
	{
		this.file = file;
		
		name = file.getAbsolutePath();
		color = new Color(Color.HSBtoRGB((float) Math.random(), 1f, 0.9f));
		updateIcon();
	}
	
	public java.io.File getFile()
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

	public abstract int getLengthMeasured();
	public abstract int getLengthDisplayed();
	
	public abstract double getTimestampDisplayed(int index);
	public abstract double getVoltageDisplayed(int index);
	public abstract double getCurrentDisplayed(int index);
	public abstract double getResistanceDisplayed(int index);
	public abstract double getPowerDisplayed(int index);
	
	public abstract double getMinTimestampMeasured();
	public abstract double getMaxTimestampMeasured();
	public abstract double getMinVoltageMeasured();
	public abstract double getMaxVoltageMeasured();
	public abstract double getMinCurrentMeasured();
	public abstract double getMaxCurrentMeasured();
	public abstract double getMinResistanceMeasured();
	public abstract double getMaxResistanceMeasured();
	public abstract double getMinPowerMeasured();
	public abstract double getMaxPowerMeasured();
	
	public abstract double getMinTimestampDisplayed();
	public abstract double getMaxTimestampDisplayed();
	public abstract double getMinVoltageDisplayed();
	public abstract double getMaxVoltageDisplayed();
	public abstract double getMinCurrentDisplayed();
	public abstract double getMaxCurrentDisplayed();
	public abstract double getMinResistanceDisplayed();
	public abstract double getMaxResistanceDisplayed();
	public abstract double getMinPowerDisplayed();
	public abstract double getMaxPowerDisplayed();
	
	public abstract void setMinTimestampDisplayed(double value);
	public abstract void setMaxTimestampDisplayed(double value);
	public abstract void setMinVoltageDisplayed(double value);
	public abstract void setMaxVoltageDisplayed(double value);
	public abstract void setMinCurrentDisplayed(double value);
	public abstract void setMaxCurrentDisplayed(double value);
	public abstract void setMinResistanceDisplayed(double value);
	public abstract void setMaxResistanceDisplayed(double value);
	public abstract void setMinPowerDisplayed(double value);
	public abstract void setMaxPowerDisplayed(double value);
	
	public abstract double getAverageCurrentDisplayed(int index, int window);
	public abstract double getAverageVoltageDisplayed(int index, int window);
	public abstract double getAverageResistanceDisplayed(int index, int window);
	public abstract double getAveragePowerDisplayed(int index, int window);

	public abstract void setMinVoltagePercentage(double value);
	public abstract void setMinCurrentPercentage(double value);
	public abstract void setMinResistancePercentage(double value);
	public abstract void setMinPowerPercentage(double value);

	public abstract void setMaxVoltagePercentage(double value);
	public abstract void setMaxCurrentPercentage(double value);
	public abstract void setMaxResistancePercentage(double value);
	public abstract void setMaxPowerPercentage(double value);

	public abstract double getMeanVoltage();
	public abstract double getMeanCurrent();
	public abstract double getMeanResistance();
	public abstract double getMeanPower();

	public abstract double getStdevVoltage();
	public abstract double getStdevCurrent();
	public abstract double getStdevResistance();
	public abstract double getStdevPower();

	public abstract double getMedianVoltage();
	public abstract double getMedianCurrent();
	public abstract double getMedianResistance();
	public abstract double getMedianPower();

	public abstract double getModeVoltage();
	public abstract double getModeCurrent();
	public abstract double getModeResistance();
	public abstract double getModePower();

	public abstract double getRootMeanSquareVoltage();
	public abstract double getRootMeanSquareCurrent();
	public abstract double getRootMeanSquareResistance();
	public abstract double getRootMeanSquarePower();

	public abstract double getVoltagePercentage();
	public abstract double getCurrentPercentage();
	public abstract double getResistancePercentage();
	public abstract double getPowerPercentage();

}
