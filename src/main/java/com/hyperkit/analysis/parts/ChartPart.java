package com.hyperkit.analysis.parts;

import java.awt.Component;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.hyperkit.analysis.Part;

public abstract class ChartPart extends Part
{
	
	private JFreeChart chart;
	
	public ChartPart(String title)
	{
		super(title, ChartPart.class.getClassLoader().getResource("icons/parts/chart.png"));
	}
	
	@Override
	protected final Component createComponent()
	{
		return new ChartPanel(getChart());
	}
	
	public final JFreeChart getChart()
	{
		if (chart == null)
		{
			chart = createChart();
		}
		
		return chart;
	}
	
	protected abstract JFreeChart createChart();

}
