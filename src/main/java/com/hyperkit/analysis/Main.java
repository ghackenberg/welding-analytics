package com.hyperkit.analysis;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.ProgressChangeEvent;
import com.hyperkit.analysis.parts.FilePart;
import com.hyperkit.analysis.parts.PropertyPart;
import com.hyperkit.analysis.parts.canvas.clouds.CurrentVoltageCloudCanvasPart;
import com.hyperkit.analysis.parts.canvas.derivatives.CurrentDerivativeCanvasPart;
import com.hyperkit.analysis.parts.canvas.derivatives.ResistanceDerivativeCanvasPart;
import com.hyperkit.analysis.parts.canvas.derivatives.VoltageDerivativeCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.CurrentHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.ResistanceHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.VoltageHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.timeseries.CurrentTimeseriesCanvasPart;
import com.hyperkit.analysis.parts.canvas.timeseries.ResistanceTimeseriesCanvasPart;
import com.hyperkit.analysis.parts.canvas.timeseries.VoltageTimeseriesCanvasPart;
import com.hyperkit.analysis.parts.canvas.traces.CurrentVoltageTraceCanvasPart;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;

public class Main
{
	
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
		
		// Progress
		
		JProgressBar progress = new JProgressBar(0, 100);
		progress.setStringPainted(true);
		
		Bus.getInstance().addHandler(new Handler()
			{
				@SuppressWarnings("unused")
				public boolean handleEvent(ProgressChangeEvent event)
				{
					progress.setValue(event.getValue());
					
					return true;
				}
			}
		);
		
		// Progress
		
		JSpinner progressSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		progressSpinner.addChangeListener(
			event ->
			{
				Bus.getInstance().broadcastEvent(new FrameChangeEvent((int) progressSpinner.getValue()));
			}
		);
		
		// Delta
		
		JSpinner delta = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
		
		// Timer
		
		Timer timer = new Timer(10,
			event ->
			{
				progressSpinner.setValue((int) progressSpinner.getValue() + (int) delta.getValue());
			}
		);
		
		// Delay
		
		JSpinner delay = new JSpinner(new SpinnerNumberModel(30, 1, 60, 1));
		delay.addChangeListener(
			event ->
			{
				timer.setDelay(1000 / (int) delay.getValue());
			}
		);
		
		// Play
		
		ImageIcon play_original = new ImageIcon(Main.class.getClassLoader().getResource("icons/parts/play.png"));
		ImageIcon play_resized = new ImageIcon(play_original.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		
		JToggleButton button_play = new JToggleButton(play_resized);
		button_play.addActionListener(
			event ->
			{
				if (button_play.isSelected())
				{
					timer.start();
				}
				else
				{
					timer.stop();
				}
			}
		);
		
		// Help
		
		ImageIcon help_original = new ImageIcon(Main.class.getClassLoader().getResource("icons/parts/help.png"));
		ImageIcon help_resized = new ImageIcon(help_original.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		
		JButton button_help = new JButton(help_resized);
		button_help.addActionListener(
			event ->
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
		);
		
		// Headbar
		
		JToolBar headbar = new JToolBar("Headbar");
		headbar.setFloatable(false);
		headbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		headbar.add(new JLabel("Load progress:"));
		headbar.add(progress);
		headbar.add(new JLabel("Frame:"));
		headbar.add(progressSpinner);
		headbar.add(new JLabel("Step:"));
		headbar.add(delta);
		headbar.add(new JLabel("FPS:"));
		headbar.add(delay);
		headbar.add(button_play);
		headbar.add(new JLabel("User documentation:"));
		headbar.add(button_help);
		
		// Footbar
		
		JToolBar footbar = new JToolBar("Footbar");
		footbar.setFloatable(false);
		footbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		footbar.add(new JLabel("(c) 2021 Dr. Georg Hackenberg, Raimundstraße 9, 4701 Bad Schallerbach, Österreich"));
		
		// Dock
		
		// Part
		Part part_file = new FilePart();
		Part part_property = new PropertyPart();
		
		Part part_voltage_density_canvas = new VoltageHistogramCanvasPart();
		Part part_current_density_canvas = new CurrentHistogramCanvasPart();
		Part part_resistance_density_canvas = new ResistanceHistogramCanvasPart();
		
		Part part_voltage_timeseries_canvas = new VoltageTimeseriesCanvasPart();
		Part part_current_timeseries_canvas = new CurrentTimeseriesCanvasPart();
		Part part_resistance_timeseries_canvas = new ResistanceTimeseriesCanvasPart();
		
		Part part_voltage_derivative_canvas = new VoltageDerivativeCanvasPart();
		Part part_current_derivative_canvas = new CurrentDerivativeCanvasPart();
		Part part_resistance_derivative_canvas = new ResistanceDerivativeCanvasPart();
		
		Part part_current_voltage_trace_canvas = new CurrentVoltageTraceCanvasPart();
		Part part_current_voltage_cloud_canvas = new CurrentVoltageCloudCanvasPart();
		
		// Grid
		SplitDockGrid grid = new SplitDockGrid();
		
		grid.addDockable(0, 0, 2, 1, part_file.getDockable());
		grid.addDockable(0, 1, 2, 3, part_property.getDockable());
		
		grid.addDockable(2, 0, 2, 1, part_voltage_density_canvas.getDockable());
		grid.addDockable(4, 0, 2, 1, part_current_density_canvas.getDockable());
		grid.addDockable(6, 0, 2, 1, part_resistance_density_canvas.getDockable());
		
		grid.addDockable(2, 1, 2, 1, part_voltage_timeseries_canvas.getDockable());
		grid.addDockable(4, 1, 2, 1, part_current_timeseries_canvas.getDockable());
		grid.addDockable(6, 1, 2, 1, part_resistance_timeseries_canvas.getDockable());
		
		grid.addDockable(2, 2, 2, 1, part_voltage_derivative_canvas.getDockable());
		grid.addDockable(4, 2, 2, 1, part_current_derivative_canvas.getDockable());
		grid.addDockable(6, 2, 2, 1, part_resistance_derivative_canvas.getDockable());
		
		grid.addDockable(2, 3, 3, 1, part_current_voltage_trace_canvas.getDockable());
		grid.addDockable(5, 3, 3, 1, part_current_voltage_cloud_canvas.getDockable());
		
		// Station
		SplitDockStation station = new SplitDockStation();
		station.dropTree(grid.toTree());
		
		// Controller
		DockController controller = new DockController();
		controller.setTheme(new EclipseTheme());
		controller.add(station);
		
		// Frame
		
		JFrame frame = new JFrame("Software für die Analyse von Messdaten");
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
