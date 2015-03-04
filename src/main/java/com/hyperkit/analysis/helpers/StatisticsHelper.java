package com.hyperkit.analysis.helpers;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.hyperkit.analysis.files.ASDFile;

public class StatisticsHelper
{

	public static double[][] getRegressionData(ASDFile file)
	{
		SimpleRegression regression = file.getRegression();
		
		double[][] data = new double[2][2];
		
		data[0][0] = file.getMinCurrentDisplayed();
		data[1][0] = regression.predict(file.getMinCurrentDisplayed());
		
		data[0][1] = file.getMaxCurrentDisplayed();
		data[1][1] = regression.predict(file.getMaxCurrentDisplayed());
		
		return data;
	}

}
