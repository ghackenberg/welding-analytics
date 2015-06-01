package com.hyperkit.analysis.adapters;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.data.xy.XYDataset;

import com.hyperkit.analysis.files.ASDFile;

public class FileXYSeriesLabelGenerator implements XYSeriesLabelGenerator
{
	
	private Map<String, ASDFile> file_map;
	
	public FileXYSeriesLabelGenerator(Map<String, ASDFile> file_map)
	{
		this.file_map = file_map;
	}
	
	@Override
	public String generateLabel(XYDataset dataset, int series)
	{
		String key = (String) dataset.getSeriesKey(series);
		
		ASDFile file = file_map.get(key);
		
		SimpleRegression regression = file.getRegression();
		
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		
		format.setMinimumFractionDigits(5);
		
		String text = "y=" + format.format(regression.getIntercept()) + "+" + format.format(regression.getSlope()) + "*x";
		
		return file.getName() + " (" + text + ")";
	}
	
}