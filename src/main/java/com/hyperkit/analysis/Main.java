package com.hyperkit.analysis;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;

import com.hyperkit.analysis.events.StepChangeEvent;
import com.hyperkit.analysis.parts.FilePart;
import com.hyperkit.analysis.parts.PropertyPart;
import com.hyperkit.analysis.parts.charts.CurrentDensityChartPart;
import com.hyperkit.analysis.parts.charts.CurrentTimeseriesChartPart;
import com.hyperkit.analysis.parts.charts.VoltageDensityChartPart;
import com.hyperkit.analysis.parts.charts.VoltageTimeseriesChartPart;

public class Main
{
	
	private static final int STEP_INIT = 100;
	private static final int STEP_MIN = 100;
	private static final int STEP_MAX = 10000;
	private static final int STEP_SIZE = 100;
	
	public static void main(String[] arguments)
	{
		// Look and feel
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		
		// Icon
		
		ImageIcon icon_analysis = new ImageIcon(Main.class.getClassLoader().getResource("icons/main.png"));
		
		// Part
		
		Part part_file = new FilePart();
		Part part_voltage_timeseries = new VoltageTimeseriesChartPart(STEP_INIT);
		Part part_current_timeseries = new CurrentTimeseriesChartPart(STEP_INIT);
		Part part_voltage_density = new VoltageDensityChartPart(STEP_INIT);
		Part part_current_density = new CurrentDensityChartPart(STEP_INIT);
		Part part_property = new PropertyPart();
		//Part part_help = new HelpPart();
		
		// Steps
		
		JSpinner step = new JSpinner(new SpinnerNumberModel(STEP_INIT, STEP_MIN, STEP_MAX, STEP_SIZE));
		step.addChangeListener(
			new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					Bus.getInstance().broadcastEvent(new StepChangeEvent((int) step.getValue()));
				}
			}
		);
		
		// Help
		
		ImageIcon icon_original = new ImageIcon(Main.class.getClassLoader().getResource("icons/parts/help.png"));
		ImageIcon icon_resized = new ImageIcon(icon_original.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		
		JButton button_help = new JButton(icon_resized);
		button_help.addActionListener(
			new ActionListener()
			{	
				@Override
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						Desktop.getDesktop().open(new java.io.File("User Documentation.pdf"));
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		);
		
		// Headbar
		
		JToolBar headbar = new JToolBar("Headbar");
		headbar.setFloatable(false);
		headbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		headbar.add(new JLabel("Step number:"));
		headbar.add(step);
		headbar.add(new JLabel("User documentation:"));
		headbar.add(button_help);
		
		// Footbar
		
		JToolBar footbar = new JToolBar("Footbar");
		footbar.setFloatable(false);
		footbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		footbar.add(new JLabel("Copyright 2014 Hyperkit Software, Georg Hackenberg, Auweg 20, 85748 Garching"));
		
		// Dock
		
		// Grid
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable(0, 0, 1, 1, part_file.getDockable());
		grid.addDockable(1, 0, 2, 1, part_voltage_timeseries.getDockable());
		grid.addDockable(3, 0, 2, 1, part_current_timeseries.getDockable());
		grid.addDockable(0, 1, 1, 1, part_property.getDockable());
		grid.addDockable(1, 1, 2, 1, part_voltage_density.getDockable());
		grid.addDockable(3, 1, 2, 1, part_current_density.getDockable());
		//grid.addDockable(5, 0, 1, 2, part_help.getDockable());

		// Station
		SplitDockStation station = new SplitDockStation();
		station.dropTree(grid.toTree());
		
		// Controller
		DockController controller = new DockController();
		controller.setTheme(new EclipseTheme());
		controller.add(station);
		
		// Frame
		
		JFrame frame = new JFrame("Hyperkit Analysis Solution");
		frame.setLayout(new BorderLayout());
		frame.add(headbar, BorderLayout.PAGE_START);
		frame.add(station.getComponent(), BorderLayout.CENTER);
		frame.add(footbar, BorderLayout.PAGE_END);
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(icon_analysis.getImage());
	}

}
