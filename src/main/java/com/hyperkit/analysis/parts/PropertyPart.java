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
import com.hyperkit.analysis.events.parts.ZoomChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.canvas.histograms.CurrentHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.PowerHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.ResistanceHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.VoltageHistogramCanvasPart;

public class PropertyPart extends Part
{
	
	private JPanel panel;
	
	private JButton colorButton;

	private JSpinner minTimestampSpinner;
	private JSpinner maxTimestampSpinner;
	private JSpinner minVoltageSpinner;
	private JSpinner maxVoltageSpinner;
	private JSpinner minCurrentSpinner;
	private JSpinner maxCurrentSpinner;
	
	private JTextField minTimestampField;
	private JTextField maxTimestampField;
	private JTextField minVoltageField;
	private JTextField maxVoltageField;
	private JTextField minCurrentField;
	private JTextField maxCurrentField;
	
	private JTextField minVoltagePercentageField;
	private JTextField maxVoltagePercentageField;
	private JTextField minCurrentPercentageField;
	private JTextField maxCurrentPercentageField;
	private JTextField minResistancePercentageField;
	private JTextField maxResistancePercentageField;
	private JTextField minPowerPercentageField;
	private JTextField maxPowerPercentageField;
	
	private JTextField voltagePercentageField;
	private JTextField currentPercentageField;
	private JTextField resistancePercentageField;
	private JTextField powerPercentageField;
	
	private JTextField medianVoltageField;
	private JTextField medianCurrentField;
	private JTextField medianResistanceField;
	private JTextField medianPowerField;
	
	private JTextField meanVoltageField;
	private JTextField meanCurrentField;
	private JTextField meanResistanceField;
	private JTextField meanPowerField;
	
	private JTextField stdevVoltageField;
	private JTextField stdevCurrentField;
	private JTextField stdevResistanceField;
	private JTextField stdevPowerField;
	
	private JTextField rootMeanSquareVoltageField;
	private JTextField rootMeanSquareCurrentField;
	private JTextField rootMeanSquarePowerField;
	private JTextField rootMeanSquareResistanceField;
	
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
			
			// Text fields (2)
			
			minVoltagePercentageField = createTextField(file.getMinVoltagePercentage());
			maxVoltagePercentageField = createTextField(file.getMaxVoltagePercentage());
			minCurrentPercentageField = createTextField(file.getMinCurrentPercentage());
			maxCurrentPercentageField = createTextField(file.getMaxCurrentPercentage());
			minResistancePercentageField = createTextField(file.getMinResistancePercentage());
			maxResistancePercentageField = createTextField(file.getMaxResistancePercentage());
			minPowerPercentageField = createTextField(file.getMinPowerPercentage());
			maxPowerPercentageField = createTextField(file.getMaxPowerPercentage());
			
			// Text fields (3)
			
			voltagePercentageField = createTextField(file.getVoltagePercentage());
			currentPercentageField = createTextField(file.getCurrentPercentage());
			resistancePercentageField = createTextField(file.getResistancePercentage());
			powerPercentageField = createTextField(file.getPowerPercentage());
			
			medianVoltageField = createTextField(file.getMedianVoltage());
			medianCurrentField = createTextField(file.getMedianCurrent());
			medianResistanceField = createTextField(file.getMedianResistance());
			medianPowerField = createTextField(file.getMedianPower());
			
			meanVoltageField = createTextField(file.getMeanVoltage());
			meanCurrentField = createTextField(file.getMeanCurrent());
			meanResistanceField = createTextField(file.getMeanResistance());
			meanPowerField = createTextField(file.getMeanPower());
			
			stdevVoltageField = createTextField(file.getStdevVoltage());
			stdevCurrentField = createTextField(file.getStdevCurrent());
			stdevResistanceField = createTextField(file.getStdevResistance());
			stdevPowerField = createTextField(file.getStdevPower());
			
			rootMeanSquareVoltageField = createTextField(file.getRootMeanSquareVoltage());
			rootMeanSquareCurrentField = createTextField(file.getRootMeanSquareCurrent());
			rootMeanSquareResistanceField = createTextField(file.getRootMeanSquareResistance());
			rootMeanSquarePowerField = createTextField(file.getRootMeanSquarePower());
			
			// Single parameters
			
			addRow("Parameter", "Value");
			addRow("Color", colorButton);
			
			// Min/max parameters
			
			addRow("Parameter", "Minimum", "Maximum");
			addRow("Timestamp", minTimestampSpinner, maxTimestampSpinner);
			addRow("Voltage", minVoltageSpinner, maxVoltageSpinner);
			addRow("Current", minCurrentSpinner, maxCurrentSpinner);
			
			// Min/max measurements
			
			addRow("Measurement", "Minimum", "Maximum");
			addRow("Timestamp", minTimestampField, maxTimestampField);
			addRow("Voltage", minVoltageField, maxVoltageField);
			addRow("Current", minCurrentField, maxCurrentField);
			
			// Percentage measurements
			
			addRow("Measurement", "Minimum", "Maximum", "Percentage");
			addRow("Voltage", minVoltagePercentageField, maxVoltagePercentageField, voltagePercentageField);
			addRow("Current", minCurrentPercentageField, maxCurrentPercentageField, currentPercentageField);
			addRow("Resistance", minResistancePercentageField, maxResistancePercentageField, resistancePercentageField);
			addRow("Power", minPowerPercentageField, maxPowerPercentageField, powerPercentageField);
			
			addRow("Measurement", "Mean", "Stdev");
			addRow("Voltage", meanVoltageField, stdevVoltageField);
			addRow("Current", meanCurrentField, stdevCurrentField);
			addRow("Resistance", meanResistanceField, stdevResistanceField);
			addRow("Power", meanPowerField, stdevPowerField);
			
			addRow("Measurement", "Median", "Root mean square");
			addRow("Voltage", medianVoltageField, rootMeanSquareVoltageField);
			addRow("Current", medianCurrentField, rootMeanSquareCurrentField);
			addRow("Resistance", medianResistanceField, rootMeanSquareResistanceField);
			addRow("Power", medianPowerField, rootMeanSquarePowerField);
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
		
		updateTextField(minVoltagePercentageField, file.getMinVoltageDisplayed());
		updateTextField(maxVoltagePercentageField, file.getMaxVoltageDisplayed());
		updateTextField(minCurrentPercentageField, file.getMinCurrentDisplayed());
		updateTextField(maxCurrentPercentageField, file.getMaxCurrentDisplayed());
		updateTextField(minResistancePercentageField, file.getMinResistanceDisplayed());
		updateTextField(maxResistancePercentageField, file.getMaxResistanceDisplayed());
		updateTextField(minPowerPercentageField, file.getMinPowerDisplayed());
		updateTextField(maxPowerPercentageField, file.getMaxPowerDisplayed());
		
		updateTextField(voltagePercentageField, file.getVoltagePercentage());
		updateTextField(currentPercentageField, file.getCurrentPercentage());
		updateTextField(resistancePercentageField, file.getResistancePercentage());
		updateTextField(powerPercentageField, file.getPowerPercentage());
		
		updateTextField(medianVoltageField, file.getMedianVoltage());
		updateTextField(medianCurrentField, file.getMedianCurrent());
		updateTextField(medianResistanceField, file.getMedianResistance());
		updateTextField(medianPowerField, file.getMedianPower());
		
		updateTextField(meanVoltageField, file.getMeanVoltage());
		updateTextField(meanCurrentField, file.getMeanCurrent());
		updateTextField(meanResistanceField, file.getMeanResistance());
		updateTextField(meanPowerField, file.getMeanPower());
		
		updateTextField(stdevVoltageField, file.getStdevVoltage());
		updateTextField(stdevCurrentField, file.getStdevCurrent());
		updateTextField(stdevResistanceField, file.getStdevResistance());
		updateTextField(stdevPowerField, file.getStdevPower());
		
		updateTextField(rootMeanSquareVoltageField, file.getRootMeanSquareVoltage());
		updateTextField(rootMeanSquareCurrentField, file.getRootMeanSquareCurrent());
		updateTextField(rootMeanSquareResistanceField, file.getRootMeanSquareResistance());
		updateTextField(rootMeanSquarePowerField, file.getRootMeanSquarePower());
		
		return true;
	}
	
	public boolean handleEvent(ZoomChangeEvent event)
	{
		if (file != null)
		{
			if (event.getPart() instanceof CurrentHistogramCanvasPart)
			{	
				updateTextField(minCurrentPercentageField, file.getMinCurrentPercentage());
				updateTextField(maxCurrentPercentageField, file.getMaxCurrentPercentage());
				updateTextField(currentPercentageField, file.getCurrentPercentage());
				updateTextField(medianCurrentField, file.getMedianCurrent());
				updateTextField(meanCurrentField, file.getMeanCurrent());
				updateTextField(stdevCurrentField, file.getStdevCurrent());
				updateTextField(rootMeanSquareCurrentField, file.getRootMeanSquareCurrent());
			}
			else if (event.getPart() instanceof VoltageHistogramCanvasPart)
			{	
				updateTextField(minVoltagePercentageField, file.getMinVoltagePercentage());
				updateTextField(maxVoltagePercentageField, file.getMaxVoltagePercentage());
				updateTextField(voltagePercentageField, file.getVoltagePercentage());
				updateTextField(medianVoltageField, file.getMedianVoltage());
				updateTextField(meanVoltageField, file.getMeanVoltage());
				updateTextField(stdevVoltageField, file.getStdevVoltage());
				updateTextField(rootMeanSquareVoltageField, file.getRootMeanSquareVoltage());
			}
			else if (event.getPart() instanceof ResistanceHistogramCanvasPart)
			{
				updateTextField(minResistancePercentageField, file.getMinResistancePercentage());
				updateTextField(maxResistancePercentageField, file.getMaxResistancePercentage());
				updateTextField(resistancePercentageField, file.getResistancePercentage());
				updateTextField(medianResistanceField, file.getMedianResistance());
				updateTextField(meanResistanceField, file.getMeanResistance());
				updateTextField(stdevResistanceField, file.getStdevResistance());
				updateTextField(rootMeanSquareResistanceField, file.getRootMeanSquareResistance());
			}
			else if (event.getPart() instanceof PowerHistogramCanvasPart)
			{
				updateTextField(minPowerPercentageField, file.getMinPowerPercentage());
				updateTextField(maxPowerPercentageField, file.getMaxPowerPercentage());
				updateTextField(powerPercentageField, file.getPowerPercentage());
				updateTextField(medianPowerField, file.getMedianPower());
				updateTextField(meanPowerField, file.getMeanPower());
				updateTextField(stdevPowerField, file.getStdevPower());
				updateTextField(rootMeanSquarePowerField, file.getRootMeanSquarePower());
			}
		}	
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
		addRow(first, second, third, new JPanel());
	}
	
	private void addRow(String first, String second, String third, String fourth)
	{
		addRow(createBoldLabel(first), createBoldLabel(second), createBoldLabel(third), createBoldLabel(fourth));
	}
	
	private void addRow(String first, JComponent second, JComponent third, JComponent fourth)
	{
		addRow(new JLabel(first), second, third, fourth);
	}
	
	private void addRow(JComponent first, JComponent second, JComponent third, JComponent fourth)
	{
		style(first);
		style(second);
		style(third);
		style(fourth);
		
		panel.add(first);
		panel.add(second);
		panel.add(third);
		panel.add(fourth);
		
		panel.setLayout(new GridLayout(panel.getComponentCount() / 4, 4, 1, 1));
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
		
		return spinner;
	}
	
	private void style(JComponent component)
	{
		component.setBorder(new EmptyBorder(2, 2, 2, 2));
		component.setBackground(Color.WHITE);
		component.setOpaque(true);
	}
	
	private void updateTextField(JTextField field, double value)
	{
		String text = format.format(value);
		
		if (!field.getText().equals(text))
		{
			field.setText(text);
		}
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
