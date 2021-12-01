package com.hyperkit.analysis.parts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
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
			
			// Spinners (1)
			
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
			
			// Parameters
			
			addRow("Parameters", "Value");
			addRow("Color", colorButton);
			
			// Originals
			
			addRow("Measurements", "Minimum", "Maximum");
			addRow("Timestamp", minTimestampField, maxTimestampField);
			addRow("Voltage", minVoltageField, maxVoltageField);
			addRow("Current", minCurrentField, maxCurrentField);
			
			// Filters
			
			addRow("Filters", "Minimum", "Maximum");
			addRow("Timestamp", minTimestampSpinner, maxTimestampSpinner);
			addRow("Voltage", minVoltageSpinner, maxVoltageSpinner);
			addRow("Current", minCurrentSpinner, maxCurrentSpinner);
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
	
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		updateSpinner(minTimestampSpinner, file.getMinTimestampMeasured(), file.getMaxTimestampDisplayed());
		updateSpinner(maxTimestampSpinner, file.getMinTimestampDisplayed(), file.getMaxTimestampMeasured());
		updateSpinner(minVoltageSpinner, file.getMinVoltageMeasured(), file.getMaxVoltageDisplayed());
		updateSpinner(maxVoltageSpinner, file.getMinVoltageDisplayed(), file.getMaxVoltageMeasured());
		updateSpinner(minCurrentSpinner, file.getMinCurrentMeasured(), file.getMaxCurrentDisplayed());
		updateSpinner(maxCurrentSpinner, file.getMinCurrentDisplayed(), file.getMaxCurrentMeasured());
		
		return true;
	}
	
	private void addRow(String first, String second)
	{	
		addRow(createBoldLabel(first), createBoldLabel(second));
	}
	
	private void addRow(String first, JComponent second)
	{
		addRow(new JLabel(first), second);
	}
	
	private void addRow(JComponent first, JComponent second)
	{
		addRow(first, second, new JPanel());
	}
	
	private void addRow(String first, String second, String third)
	{
		addRow(createBoldLabel(first), createBoldLabel(second), createBoldLabel(third));
	}
	
	private void addRow(String first, JComponent second, JComponent third)
	{
		addRow(new JLabel(first), second, third);
	}
	
	private void addRow(JComponent first, JComponent second, JComponent third)
	{
		style(first);
		style(second);
		style(third);
		
		panel.add(first);
		panel.add(second);
		panel.add(third);
		
		panel.setLayout(new GridLayout(panel.getComponentCount() / 3, 3, 1, 1));
	}
	
	private JLabel createBoldLabel(String value)
	{
		JLabel result = new JLabel(value);
		
		Font font = result.getFont();
		
		result.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		result.setBackground(Color.WHITE);
		
		return result;
	}
	
	private JTextField createTextField(double value)
	{
		JTextField field = new JTextField(format.format(value));
		
		field.setColumns(4);
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
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, minimumValue, maximumValue, (maximumValue - minimumValue) / 100));
		
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(4);
		
		return spinner;
	}
	
	private void style(JComponent component)
	{
		component.setBorder(new EmptyBorder(2, 2, 2, 2));
		component.setBackground(Color.WHITE);
		component.setOpaque(true);
	}
	
	private void updateSpinner(JSpinner spinner, double minimumValue, double maximumValue)
	{
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		
		double value = model.getNumber().doubleValue();
		/*
		double min = (double) model.getMinimum();
		double max = (double) model.getMaximum();
		*/
		if (value < minimumValue)
		{
			value = minimumValue;
		}
		if (value > maximumValue)
		{
			value = maximumValue;
		}
		
		//if (value != model.getNumber().doubleValue() || minimumValue != min || maximumValue != max)
		{
			spinner.setModel(new SpinnerNumberModel(value, minimumValue, maximumValue, model.getStepSize()));
		}
	}

}
