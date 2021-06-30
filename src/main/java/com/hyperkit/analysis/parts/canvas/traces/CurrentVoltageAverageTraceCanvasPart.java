package com.hyperkit.analysis.parts.canvas.traces;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.TraceCanvasPart;

public class CurrentVoltageAverageTraceCanvasPart extends TraceCanvasPart
{
	
	private int average = 1;

	public CurrentVoltageAverageTraceCanvasPart()
	{
		super("Current-voltage average trace", "Current (in A)", "Voltage (in V)", CurrentVoltageAverageTraceCanvasPart.class.getClassLoader().getResource("icons/parts/scatter.png"), 1000, 0);
		
		// Point
		
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(average, 0, 100, 1));
		spinner.addChangeListener(
			event ->
			{
				average = (int) spinner.getValue();
				
				getPanel().repaint();
			}
		);

		getToolBar().add(new JLabel("Window size:"));
		getToolBar().add(spinner);
	}

	@Override
	protected double getDomainMinimum(ASDFile file)
	{
		return file.getMinCurrentDisplayed();
	}

	@Override
	protected double getRangeMinimum(ASDFile file)
	{
		return file.getMinVoltageDisplayed();
	}

	@Override
	protected double getDomainMaximum(ASDFile file)
	{
		return file.getMaxCurrentDisplayed();
	}

	@Override
	protected double getRangeMaximum(ASDFile file)
	{
		return file.getMaxVoltageDisplayed();
	}

	@Override
	protected double getRawDomainValue(ASDFile file, int index)
	{
		return file.getAverageCurrentDisplayed(index, average);
	}

	@Override
	protected double getRawRangeValue(ASDFile file, int index)
	{
		return file.getAverageVoltageDisplayed(index, average);
	}

}
