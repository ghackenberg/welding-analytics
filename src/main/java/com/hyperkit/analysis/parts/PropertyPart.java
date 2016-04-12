package com.hyperkit.analysis.parts;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.FilePartSelectEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;

public class PropertyPart extends Part
{
	
	private JPanel panel;
	
	private JButton colorButton;
	
	private JTextField minTimestampField;
	private JTextField maxTimestampField;
	private JTextField minVoltageField;
	private JTextField maxVoltageField;
	private JTextField minCurrentField;
	private JTextField maxCurrentField;
	
	private JTextField meanVoltageField;
	private JTextField meanCurrentField;
	private JTextField meanPowerField;
	private JTextField rootMeanSquareVoltageField;
	private JTextField rootMeanSquareCurrentField;
	private JTextField rootMeanSquarePowerField;

	private JSpinner minTimestampSpinner;
	private JSpinner maxTimestampSpinner;
	private JSpinner minVoltageSpinner;
	private JSpinner maxVoltageSpinner;
	private JSpinner minCurrentSpinner;
	private JSpinner maxCurrentSpinner;
	
	private ASDFile file;
	
	private NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);

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
	
	public boolean handleEvent(FilePartSelectEvent event)
	{
		panel.removeAll();
		
		if (event.getASDFile() != null)
		{
			file = event.getASDFile();
			
			PropertyPart self = this;
			
			ASDFile file = event.getASDFile();
			
			// Buttons
			
			colorButton = new JButton(file.getIcon());

			colorButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						Color newColor = JColorChooser.showDialog(colorButton, "Choose color", file.getColor());
						
						if (newColor != null)
						{
							file.setColor(newColor);
							
							colorButton.setIcon(file.getIcon());
							
							Bus.getInstance().broadcastEvent(new PropertyPartChangeEvent(self, file));
						}
					}
				}
			);
			
			// Text fields (1)
			
			minTimestampField = createTextField(file.getMinTimestampMeasured());
			maxTimestampField = createTextField(file.getMaxTimestampMeasured());
			minVoltageField = createTextField(file.getMinVoltageMeasured());
			maxVoltageField = createTextField(file.getMaxVoltageMeasured());
			minCurrentField = createTextField(file.getMinCurrentMeasured());
			maxCurrentField = createTextField(file.getMaxCurrentMeasured());
			
			// Text fields (2)
			
			meanVoltageField = createTextField(file.getMeanVoltage());
			meanCurrentField = createTextField(file.getMeanCurrent());
			meanPowerField = createTextField(file.getMeanPower());
			rootMeanSquareVoltageField = createTextField(file.getRootMeanSquareVoltage());
			rootMeanSquareCurrentField = createTextField(file.getRootMeanSquareCurrent());
			rootMeanSquarePowerField = createTextField(file.getRootMeanSquarePower());
			
			// Spinners
			
			minTimestampSpinner = createMinSpinner(file.getMinTimestampDisplayed(), file.getMaxTimestampDisplayed());
			maxTimestampSpinner = createMaxSpinner(file.getMinTimestampDisplayed(), file.getMaxTimestampDisplayed());
			minVoltageSpinner = createMinSpinner(file.getMinVoltageDisplayed(), file.getMaxVoltageDisplayed());
			maxVoltageSpinner = createMaxSpinner(file.getMinVoltageDisplayed(), file.getMaxVoltageDisplayed());
			minCurrentSpinner = createMinSpinner(file.getMinCurrentDisplayed(), file.getMaxCurrentDisplayed());
			maxCurrentSpinner = createMaxSpinner(file.getMinCurrentDisplayed(), file.getMaxCurrentDisplayed());
			
			minTimestampSpinner.addChangeListener(
				new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						file.setMinTimestampDisplayed((double) minTimestampSpinner.getValue());
						
						Bus.getInstance().broadcastEvent(new PropertyPartChangeEvent(self, file));
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
						
						Bus.getInstance().broadcastEvent(new PropertyPartChangeEvent(self, file));
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
						
						Bus.getInstance().broadcastEvent(new PropertyPartChangeEvent(self, file));
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
						
						Bus.getInstance().broadcastEvent(new PropertyPartChangeEvent(self, file));
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
						
						Bus.getInstance().broadcastEvent(new PropertyPartChangeEvent(self, file));
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
						
						Bus.getInstance().broadcastEvent(new PropertyPartChangeEvent(self, file));
					}
				}
			);
			
			// Layout
			
			panel.setLayout(new GridLayout(14, 3, 10, 10));
			
			// Single parameters
			
			panel.add(new JLabel("Parameters"));
			panel.add(new JLabel("Value"));
			panel.add(new JLabel());
			
			panel.add(new JLabel("Color"));
			panel.add(colorButton);
			panel.add(new JLabel());
			
			// Min/max parameters
			
			panel.add(new JLabel("Parameters"));
			panel.add(new JLabel("Minimum"));
			panel.add(new JLabel("Maximum"));

			panel.add(new JLabel("Timestamp"));
			panel.add(minTimestampSpinner);
			panel.add(maxTimestampSpinner);
			panel.add(new JLabel("Voltage"));
			panel.add(minVoltageSpinner);
			panel.add(maxVoltageSpinner);
			panel.add(new JLabel("Current"));
			panel.add(minCurrentSpinner);
			panel.add(maxCurrentSpinner);
			
			// Min/max measurements
			
			panel.add(new JLabel("Measurements"));
			panel.add(new JLabel("Minimum"));
			panel.add(new JLabel("Maximum"));

			panel.add(new JLabel("Timestamp"));
			panel.add(minTimestampField);
			panel.add(maxTimestampField);
			panel.add(new JLabel("Voltage"));
			panel.add(minVoltageField);
			panel.add(maxVoltageField);
			panel.add(new JLabel("Current"));
			panel.add(minCurrentField);
			panel.add(maxCurrentField); 
			
			// Mean/root mean square measurements
			
			panel.add(new JLabel("Measurements"));
			panel.add(new JLabel("Mean"));
			panel.add(new JLabel("Root mean square"));
			
			panel.add(new JLabel("Voltage"));
			panel.add(meanVoltageField);
			panel.add(rootMeanSquareVoltageField);
			panel.add(new JLabel("Current"));
			panel.add(meanCurrentField);
			panel.add(rootMeanSquareCurrentField);
			panel.add(new JLabel("Power"));
			panel.add(meanPowerField);
			panel.add(rootMeanSquarePowerField);
		}
		
		panel.revalidate();
		
		return true;
	}
	
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		if (file != null && file == event.getASDFile())
		{
			file = null;
			
			panel.removeAll();
			panel.repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(PropertyPartChangeEvent event) {
		meanVoltageField.setText(format.format(file.getMeanVoltage()));
		meanCurrentField.setText(format.format(file.getMeanCurrent()));
		meanPowerField.setText(format.format(file.getMeanPower()));
		rootMeanSquareVoltageField.setText(format.format(file.getRootMeanSquareVoltage()));
		rootMeanSquareCurrentField.setText(format.format(file.getRootMeanSquareCurrent()));
		rootMeanSquarePowerField.setText(format.format(file.getRootMeanSquarePower()));
		
		((SpinnerNumberModel) minTimestampSpinner.getModel()).setMaximum(file.getMaxTimestampDisplayed());
		((SpinnerNumberModel) maxTimestampSpinner.getModel()).setMinimum(file.getMinTimestampDisplayed());
		((SpinnerNumberModel) minVoltageSpinner.getModel()).setMaximum(file.getMaxVoltageDisplayed());
		((SpinnerNumberModel) maxVoltageSpinner.getModel()).setMinimum(file.getMinVoltageDisplayed());
		((SpinnerNumberModel) minCurrentSpinner.getModel()).setMaximum(file.getMaxCurrentDisplayed());
		((SpinnerNumberModel) maxCurrentSpinner.getModel()).setMinimum(file.getMinCurrentDisplayed());
		
		/*
		((SpinnerNumberModel) minTimestampSpinner.getModel()).setStepSize((file.getMaxTimestampDisplayed() - file.getMinTimestampDisplayed()) / 100);
		((SpinnerNumberModel) maxTimestampSpinner.getModel()).setStepSize((file.getMaxTimestampDisplayed() - file.getMinTimestampDisplayed()) / 100);
		((SpinnerNumberModel) minVoltageSpinner.getModel()).setStepSize((file.getMaxVoltageDisplayed() - file.getMinVoltageDisplayed()) / 100);
		((SpinnerNumberModel) maxVoltageSpinner.getModel()).setStepSize((file.getMaxVoltageDisplayed() - file.getMinVoltageDisplayed()) / 100);
		((SpinnerNumberModel) minCurrentSpinner.getModel()).setStepSize((file.getMaxCurrentDisplayed() - file.getMinCurrentDisplayed()) / 100);
		((SpinnerNumberModel) maxCurrentSpinner.getModel()).setStepSize((file.getMaxCurrentDisplayed() - file.getMinCurrentDisplayed()) / 100);
		*/
		
		return true;
	}
	
	private JTextField createTextField(double value)
	{
		JTextField field = new JTextField(format.format(value));
		
		field.setEnabled(false);
		field.setHorizontalAlignment(JTextField.RIGHT);
		
		return field;
	}
	
	private JSpinner createMinSpinner(double minimumValue, double maximumValue)
	{
		return createSpinner(minimumValue, maximumValue, minimumValue);
	}
	
	private JSpinner createMaxSpinner(double minimumValue, double maximumValue)
	{
		return createSpinner(minimumValue, maximumValue, maximumValue);
	}
	
	private JSpinner createSpinner(double minimumValue, double maximumValue, double value)
	{
		return new JSpinner(new SpinnerNumberModel(value, minimumValue, maximumValue, (maximumValue - minimumValue) / 100));
	}

}
