package com.hyperkit.analysis.parts;

import java.awt.Component;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.parts.FilePartSelectEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;

public class PropertyPart extends Part
{
	
	private JPanel panel;

	public PropertyPart()
	{
		super("Properties", PropertyPart.class.getClassLoader().getResource("icons/parts/property.png"));
	}

	@Override
	protected Component createComponent()
	{
		panel = new JPanel();
		
		return panel;
	}
	
	public boolean handleEvent(FilePartSelectEvent e)
	{
		panel.removeAll();
		
		if (e.getASDFile() != null)
		{			
			NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
			
			PropertyPart self = this;
			
			ASDFile file = e.getASDFile();
			
			double minTimestampMeasured = file.getMinTimestampMeasured();
			double maxTimestampMeasured = file.getMaxTimestampMeasured();
			double minVoltageMeasured = file.getMinVoltageMeasured();
			double maxVoltageMeasured = file.getMaxVoltageMeasured();
			double minCurrentMeasured = file.getMinCurrentMeasured();
			double maxCurrentMeasured = file.getMaxCurrentMeasured();
			
			double minTimestampDisplayed = file.getMinTimestampDisplayed();
			double maxTimestampDisplayed = file.getMaxTimestampDisplayed();
			double minVoltageDisplayed = file.getMinVoltageDisplayed();
			double maxVoltageDisplayed = file.getMaxVoltageDisplayed();
			double minCurrentDisplayed = file.getMinCurrentDisplayed();
			double maxCurrentDisplayed = file.getMaxCurrentDisplayed();
			
			JTextField minTimestampField = new JTextField(format.format(minTimestampMeasured));
			JTextField maxTimestampField = new JTextField(format.format(maxTimestampMeasured));
			JTextField minVoltageField = new JTextField(format.format(minVoltageMeasured));
			JTextField maxVoltageField = new JTextField(format.format(maxVoltageMeasured));
			JTextField minCurrentField = new JTextField(format.format(minCurrentMeasured));
			JTextField maxCurrentField = new JTextField(format.format(maxCurrentMeasured));
			
			minTimestampField.setEnabled(false);
			maxTimestampField.setEnabled(false);
			minVoltageField.setEnabled(false);
			maxVoltageField.setEnabled(false);
			minCurrentField.setEnabled(false);
			maxCurrentField.setEnabled(false);
			
			minTimestampField.setHorizontalAlignment(JTextField.RIGHT);			
			maxTimestampField.setHorizontalAlignment(JTextField.RIGHT);
			minVoltageField.setHorizontalAlignment(JTextField.RIGHT);
			maxVoltageField.setHorizontalAlignment(JTextField.RIGHT);
			minCurrentField.setHorizontalAlignment(JTextField.RIGHT);
			maxCurrentField.setHorizontalAlignment(JTextField.RIGHT);
			
			JSpinner minTimestampSpinner = new JSpinner(new SpinnerNumberModel(minTimestampDisplayed, minTimestampMeasured, maxTimestampMeasured, (maxTimestampMeasured - minTimestampMeasured) / 100));
			JSpinner maxTimestampSpinner = new JSpinner(new SpinnerNumberModel(maxTimestampDisplayed, minTimestampMeasured, maxTimestampMeasured, (maxTimestampMeasured - minTimestampMeasured) / 100));
			JSpinner minVoltageSpinner = new JSpinner(new SpinnerNumberModel(minVoltageDisplayed, minVoltageMeasured, maxVoltageMeasured, (maxVoltageMeasured - minVoltageMeasured) / 100));
			JSpinner maxVoltageSpinner = new JSpinner(new SpinnerNumberModel(maxVoltageDisplayed, minVoltageMeasured, maxVoltageMeasured, (maxVoltageMeasured - minVoltageMeasured) / 100));
			JSpinner minCurrentSpinner = new JSpinner(new SpinnerNumberModel(minCurrentDisplayed, minCurrentMeasured, maxCurrentMeasured, (maxCurrentMeasured - minCurrentMeasured) / 100));
			JSpinner maxCurrentSpinner = new JSpinner(new SpinnerNumberModel(maxCurrentDisplayed, minCurrentMeasured, maxCurrentMeasured, (maxCurrentMeasured - minCurrentMeasured) / 100));
			
			minTimestampSpinner.addChangeListener(
				new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						file.setMinTimestampDisplayed((double) minTimestampSpinner.getValue());
						
						triggerEvent(new PropertyPartChangeEvent(self, file));
					}
				}
			);
			maxTimestampSpinner.addChangeListener(
				new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						file.setMaxTimestampDisplayed((double) maxTimestampSpinner.getValue());
						
						triggerEvent(new PropertyPartChangeEvent(self, file));
					}
				}
			);
			minVoltageSpinner.addChangeListener(
				new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						file.setMinVoltageDisplayed((double) minVoltageSpinner.getValue());
						
						triggerEvent(new PropertyPartChangeEvent(self, file));
					}
				}
			);
			maxVoltageSpinner.addChangeListener(
				new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						file.setMaxVoltageDisplayed((double) maxVoltageSpinner.getValue());
						
						triggerEvent(new PropertyPartChangeEvent(self, file));
					}
				}
			);
			minCurrentSpinner.addChangeListener(
				new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						file.setMinCurrentDisplayed((double) minCurrentSpinner.getValue());
						
						triggerEvent(new PropertyPartChangeEvent(self, file));
					}
				}
			);
			maxCurrentSpinner.addChangeListener(
				new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						file.setMaxCurrentDisplayed((double) maxCurrentSpinner.getValue());
						
						triggerEvent(new PropertyPartChangeEvent(self, file));
					}
				}
			);
			
			panel.setLayout(new GridLayout(7, 3));
			
			panel.add(new JLabel("Property"));
			panel.add(new JLabel("Minimum"));
			panel.add(new JLabel("Maximum"));

			panel.add(new JLabel("Timestamp measured"));
			panel.add(minTimestampField);
			panel.add(maxTimestampField);
			panel.add(new JLabel("Voltage measured"));
			panel.add(minVoltageField);
			panel.add(maxVoltageField);
			panel.add(new JLabel("Current measured"));
			panel.add(minCurrentField);
			panel.add(maxCurrentField); 

			panel.add(new JLabel("Timestamp displayed"));
			panel.add(minTimestampSpinner);
			panel.add(maxTimestampSpinner);
			panel.add(new JLabel("Voltage displayed"));
			panel.add(minVoltageSpinner);
			panel.add(maxVoltageSpinner);
			panel.add(new JLabel("Current displayed"));
			panel.add(minCurrentSpinner);
			panel.add(maxCurrentSpinner);
		}
		
		panel.revalidate();
		
		return true;
	}

}
