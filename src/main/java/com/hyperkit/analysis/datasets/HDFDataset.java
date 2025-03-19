package com.hyperkit.analysis.datasets;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.events.values.ProgressChangeEvent;

import io.jhdf.HdfFile;
import io.jhdf.api.Group;
import io.jhdf.api.Node;

public class HDFDataset extends Dataset {
	
	private static class Option
	{
		private io.jhdf.api.Dataset dataset;
		
		private Group block;
		private Group channel;
		private Group measurement;
		
		public Option(io.jhdf.api.Dataset dataset)
		{
			this.dataset = dataset;
			
			block = dataset.getParent();
			channel = block.getParent().getParent();
			measurement = channel.getParent().getParent();
		}
		
		public String getMeasurementName()
		{
			return measurement.getAttribute("name").getData().toString();
		}
		
		public String getChannelName()
		{
			return channel.getAttribute("name").getData().toString();
		}
		public String getChannelPhysicalUnit()
		{
			return channel.getAttribute("physicalUnit").getData().toString();
		}
		public double getChannelRangeMin()
		{
			return (double) channel.getAttribute("rangeMin").getData();
		}
		public double getChannelRangeMax()
		{
			return (double) channel.getAttribute("rangeMax").getData();
		}
		
		public double getBlockSampleRateHertz()
		{
			return (double) block.getAttribute("sampleRateHertz").getData();
		}
		
		public long getDatasetSize()
		{
			return dataset.getSize();
		}
		public float[] getDatasetData()
		{
			Object data = dataset.getData();
			if (data instanceof int[]) {
				int[] ints = (int[]) data;
				float[] floats = new float[ints.length];
				for (int i = 0; i < ints.length; i++) {
					floats[i] = ints[i];
				}
				return floats;
			} else if (data instanceof float[]) {
				return (float[]) dataset.getData();
			} else if (data instanceof double[]) {
				double[] doubles = (double[]) data;
				float[] floats = new float[doubles.length];
				for (int i = 0; i < doubles.length; i++) {
					floats[i] = (float)doubles[i];
				}
				return floats;
			} else {
				throw new IllegalStateException();
			}
		}
		
		@Override
		public String toString()
		{
			return getMeasurementName() + " - " + getChannelName() + " (" + getChannelRangeMin() + " to " + getChannelRangeMax() + " " + getChannelPhysicalUnit() + ")";
		}
	}
	
	public static List<Dataset> load(File file, Component parent) {
		List<Dataset> result = new ArrayList<>();
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(0));
		
		// Parse data
		
		try (HdfFile hdfFile = new HdfFile(file))
		{
			Vector<io.jhdf.api.Dataset> datasets = new Vector<>();
			
			findDatasets(hdfFile, datasets);
			
			Vector<Option> options = new Vector<>();
			
			for (io.jhdf.api.Dataset dataset: datasets)
			{
				if (dataset.getPath().contains("measurements") && dataset.getPath().contains("channels") && dataset.getPath().contains("blocks") && !dataset.getPath().contains("@"))
				{
					options.add(new Option(dataset));
				}
			}
			
			JComboBox<Option> voltageCombo = new JComboBox<>(options);
			JComboBox<Option> currentCombo = new JComboBox<>(options);
			
			int padding = new JLabel().getFont().getSize() / 2;
			
			GridBagConstraints constraint = new GridBagConstraints();
			constraint.insets = new Insets(padding, padding, padding, padding);
			
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			
			constraint.gridx = 0;
			constraint.gridy = 0;
			panel.add(new JLabel("Voltage:"), constraint);
			
			constraint.gridx = 1;
			constraint.gridy = 0;
			panel.add(voltageCombo, constraint);

			constraint.gridx = 0;
			constraint.gridy = 1;
			panel.add(new JLabel("Current:"), constraint);

			constraint.gridx = 1;
			constraint.gridy = 1;
			panel.add(currentCombo, constraint);
			
			int option = JOptionPane.showConfirmDialog(parent, panel, "Configure dataset", JOptionPane.OK_CANCEL_OPTION);
			
			if (option == JOptionPane.OK_OPTION)
			{
				if (voltageCombo.getSelectedItem() == null || currentCombo.getSelectedItem() == null)
				{
					JOptionPane.showMessageDialog(parent, "Please select two datasets!");
				}
				else
				{
					Option voltageOption = (Option) voltageCombo.getSelectedItem();
					Option currentOption = (Option) currentCombo.getSelectedItem();
					
					double voltageRate = voltageOption.getBlockSampleRateHertz();
					double currentRate = currentOption.getBlockSampleRateHertz();
					
					if (voltageOption.getDatasetSize() != currentOption.getDatasetSize())
					{
						JOptionPane.showMessageDialog(parent, "Datasets do not have same length!");
					}
					else if (voltageRate != currentRate)
					{
						JOptionPane.showMessageDialog(parent, "Datasets do not have same rate!");
					}
					else
					{
						float[] voltages = voltageOption.getDatasetData();
						float[] currents = currentOption.getDatasetData();
						
						List<double[]> data = new ArrayList<>();
						
						int number = 0;
						
						for (int index = 0; index < voltages.length; index++)
						{
							double timestamp = index / voltageRate;
							double voltage = voltages[index];
							double current = currents[index];
							double resistance = voltage / current;
							double power = voltage * current;
							
							data.add(new double[] {timestamp, voltage, current, resistance, power});
							
							if (data.size() > 100000) {
								result.add(new HDFDataset(file, number++, data));
								
								data = new ArrayList<>();
							}
						}
						
						if (data.size() > 0) {
							result.add(new HDFDataset(file, number, data));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Broadcast event
		
		Bus.getInstance().broadcastEvent(new ProgressChangeEvent(100));
		
		return result;
	}

	private static void findDatasets(Group group, Vector<io.jhdf.api.Dataset> result)
	{
		for (Node node : group)
		{	
			if (node instanceof io.jhdf.api.Dataset)
			{
				result.add((io.jhdf.api.Dataset) node);
			}
			else if (node instanceof Group)
			{
				findDatasets((Group) node, result);
			}
		}
	}

	private HDFDataset(File file, int index, List<double[]> data)
	{
		super(file);
		
		// Set name

		this.name = file.getAbsolutePath() + " - " + index;
		
		// Set data
		
		this.data = data;
		
		// Clean data
		
		cleanData();
		
		// Update active data
		
		updateActiveData();
	}

}
