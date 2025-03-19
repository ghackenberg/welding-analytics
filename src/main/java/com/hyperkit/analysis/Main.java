package com.hyperkit.analysis;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hyperkit.analysis.events.values.AverageChangeEvent;
import com.hyperkit.analysis.events.values.FontChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.HistogramChangeEvent;
import com.hyperkit.analysis.events.values.ProgressChangeEvent;
import com.hyperkit.analysis.events.values.StrokeChangeEvent;
import com.hyperkit.analysis.events.values.WindowChangeEvent;
import com.hyperkit.analysis.helpers.ImageHelper;
import com.hyperkit.analysis.parts.FilePart;
import com.hyperkit.analysis.parts.PropertyPart;
import com.hyperkit.analysis.parts.canvas.clouds.CurrentVoltageCloudCanvasPart;
import com.hyperkit.analysis.parts.canvas.derivatives.CurrentDerivativeCanvasPart;
import com.hyperkit.analysis.parts.canvas.derivatives.PowerDerivativeCanvasPart;
import com.hyperkit.analysis.parts.canvas.derivatives.ResistanceDerivativeCanvasPart;
import com.hyperkit.analysis.parts.canvas.derivatives.VoltageDerivativeCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.CurrentHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.PowerHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.ResistanceHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.histograms.VoltageHistogramCanvasPart;
import com.hyperkit.analysis.parts.canvas.timeseries.CurrentTimeseriesCanvasPart;
import com.hyperkit.analysis.parts.canvas.timeseries.PowerTimeseriesCanvasPart;
import com.hyperkit.analysis.parts.canvas.timeseries.ResistanceTimeseriesCanvasPart;
import com.hyperkit.analysis.parts.canvas.timeseries.VoltageTimeseriesCanvasPart;
import com.hyperkit.analysis.parts.canvas.traces.CurrentVoltageTraceCanvasPart;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;

public class Main
{
	
	private static int frame = 0;
	private static int window = 5000;
	private static int average = 0;
	private static int histogram = 1000;
	private static int stroke = 1;
	private static int font = new JLabel().getFont().getSize();
	
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
		
		ImageIcon icon_analysis = ImageHelper.getImageIcon("icons/main.png", 64);
		
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
		
		// Frame
		
		JSpinner progressSpinner = new JSpinner(new SpinnerNumberModel(frame, 0, 10000000, 1));
		progressSpinner.addChangeListener(
			_ ->
			{
				frame = (int) progressSpinner.getValue();
				
				Bus.getInstance().broadcastEvent(new FrameChangeEvent(frame));
			}
		);
		
		// Window
		
		JSpinner windowSpinner = new JSpinner(new SpinnerNumberModel(window, 100, 100000, 100));
		windowSpinner.addChangeListener(
			_ ->
			{
				window = (int) windowSpinner.getValue();
				
				Bus.getInstance().broadcastEvent(new WindowChangeEvent(window));
			}
		);
		
		// Average
		
		JSpinner averageSpinner = new JSpinner(new SpinnerNumberModel(average, 0, 100, 1));
		averageSpinner.addChangeListener(
			_ ->
			{
				average = (int) averageSpinner.getValue();
				
				Bus.getInstance().broadcastEvent(new AverageChangeEvent(average));
			}
		);
		
		// Histogram
		
		JSpinner histogramSpinner = new JSpinner(new SpinnerNumberModel(histogram, 10, 10000, 10));
		histogramSpinner.addChangeListener(
			_ ->
			{
				histogram = (int) histogramSpinner.getValue();
						
				Bus.getInstance().broadcastEvent(new HistogramChangeEvent(histogram));
			}
		);
		
		// Stroke
		
		JSpinner strokeSpinner = new JSpinner(new SpinnerNumberModel(stroke, 1, 5, 1));
		strokeSpinner.addChangeListener(
			_ ->
			{
				stroke = (int) strokeSpinner.getValue();
						
				Bus.getInstance().broadcastEvent(new StrokeChangeEvent(stroke));
			}
		);
		
		// Stroke
		
		JSpinner fontSpinner = new JSpinner(new SpinnerNumberModel(font, 1, 16, 1));
		fontSpinner.addChangeListener(
			_ ->
			{
				font = (int) fontSpinner.getValue();
						
				Bus.getInstance().broadcastEvent(new FontChangeEvent(font));
			}
		);
		
		// Delta
		
		JSlider deltaSlider = new JSlider(-1000, 1000, 10);
		
		JSpinner deltaSpinner = new JSpinner(new SpinnerNumberModel(10, -1000, 1000, 1));
		
		deltaSlider.setMajorTickSpacing(500);
		deltaSlider.setMinorTickSpacing(100);
		deltaSlider.setPaintTicks(true);
		deltaSlider.setPaintTrack(true);
		deltaSlider.addChangeListener(
			_ ->
			{
				deltaSpinner.setValue(deltaSlider.getValue());
			}
		);
		
		deltaSpinner.addChangeListener(
			_ ->
			{
				deltaSlider.setValue((int) deltaSpinner.getValue());
			}
		);
		
		// Timer
		
		Timer timer = new Timer(10,
			_ ->
			{
				progressSpinner.setValue((int) progressSpinner.getValue() + (int) deltaSlider.getValue());
			}
		);
		
		// Delay
		
		JSpinner delay = new JSpinner(new SpinnerNumberModel(30, 1, 60, 1));
		delay.addChangeListener(
			_ ->
			{
				timer.setDelay(1000 / (int) delay.getValue());
			}
		);
		
		// Play
		
		ImageIcon icon_play = ImageHelper.getImageIcon("icons/parts/play.png");		
		ImageIcon icon_pause = ImageHelper.getImageIcon("icons/parts/pause.png");
		
		JToggleButton button_play = new JToggleButton(icon_play);
		button_play.addActionListener(
			_ ->
			{
				if (button_play.isSelected())
				{
					button_play.setIcon(icon_pause);
					timer.start();
				}
				else
				{
					button_play.setIcon(icon_play);
					timer.stop();
				}
			}
		);
		
		// Help
		
		ImageIcon icon_help = ImageHelper.getImageIcon("icons/parts/help.png");
		
		JButton button_help = new JButton(icon_help);
		button_help.addActionListener(
			_ ->
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
		headbar.setLayout(new WrapLayout(FlowLayout.LEFT));
		
		headbar.add(new JLabel("Progress:"));
		headbar.add(progress);
		
		headbar.add(new JLabel("Frame:"));
		headbar.add(progressSpinner);
		headbar.add(new JLabel("Step:"));
		headbar.add(deltaSlider);
		headbar.add(deltaSpinner);
		headbar.add(new JLabel("FPS:"));
		headbar.add(delay);
		headbar.add(new JLabel("Window:"));
		headbar.add(windowSpinner);
		headbar.add(new JLabel("Average:"));
		headbar.add(averageSpinner);
		headbar.add(new JLabel("Histogram:"));
		headbar.add(histogramSpinner);
		headbar.add(new JLabel("Stroke:"));
		headbar.add(strokeSpinner);
		headbar.add(new JLabel("Font:"));
		headbar.add(fontSpinner);
		
		headbar.add(button_play);
		headbar.add(button_help);
		
		// Footbar
		
		JToolBar footbar = new JToolBar("Footbar");
		footbar.setFloatable(false);
		footbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		footbar.add(new JLabel("(c) 2021 Dr. Georg Hackenberg, Raimundstra�e 9, 4701 Bad Schallerbach, �sterreich"));
		
		// Dock
		
		// Part
		Part part_file = new FilePart();
		Part part_property = new PropertyPart();
		
		Part part_voltage_density_canvas = new VoltageHistogramCanvasPart(frame, average, histogram);
		Part part_current_density_canvas = new CurrentHistogramCanvasPart(frame, average, histogram);
		Part part_resistance_density_canvas = new ResistanceHistogramCanvasPart(frame, average, histogram);
		Part part_power_density_canvas = new PowerHistogramCanvasPart(frame, average, histogram);
		
		Part part_voltage_timeseries_canvas = new VoltageTimeseriesCanvasPart(frame, window, average);
		Part part_current_timeseries_canvas = new CurrentTimeseriesCanvasPart(frame, window, average);
		Part part_resistance_timeseries_canvas = new ResistanceTimeseriesCanvasPart(frame, window, average);
		Part part_power_timeseries_canvas = new PowerTimeseriesCanvasPart(frame, window, average);
		
		Part part_voltage_derivative_canvas = new VoltageDerivativeCanvasPart(frame, window, average);
		Part part_current_derivative_canvas = new CurrentDerivativeCanvasPart(frame, window, average);
		Part part_resistance_derivative_canvas = new ResistanceDerivativeCanvasPart(frame, window, average);
		Part part_power_derivative_canvas = new PowerDerivativeCanvasPart(frame, window, average);
		
		Part part_current_voltage_average_trace_canvas = new CurrentVoltageTraceCanvasPart(frame, window, average);
		Part part_current_voltage_cloud_canvas = new CurrentVoltageCloudCanvasPart(frame, window, average);
		
		// Grid
		SplitDockGrid grid = new SplitDockGrid();
		
		grid.addDockable(0, 0, 1, 2, part_file.getDockable());
		grid.addDockable(0, 2, 1, 2, part_property.getDockable());
		
		grid.addDockable(1, 0, 1, 1, part_voltage_density_canvas.getDockable());
		grid.addDockable(2, 0, 1, 1, part_voltage_timeseries_canvas.getDockable());
		grid.addDockable(3, 0, 1, 1, part_voltage_derivative_canvas.getDockable());
		
		grid.addDockable(1, 1, 1, 1, part_current_density_canvas.getDockable());
		grid.addDockable(2, 1, 1, 1, part_current_timeseries_canvas.getDockable());
		grid.addDockable(3, 1, 1, 1, part_current_derivative_canvas.getDockable());
		
		grid.addDockable(1, 2, 1, 1, part_resistance_density_canvas.getDockable());
		grid.addDockable(2, 2, 1, 1, part_resistance_timeseries_canvas.getDockable());
		grid.addDockable(3, 2, 1, 1, part_resistance_derivative_canvas.getDockable());
		
		grid.addDockable(1, 3, 1, 1, part_power_density_canvas.getDockable());
		grid.addDockable(2, 3, 1, 1, part_power_timeseries_canvas.getDockable());
		grid.addDockable(3, 3, 1, 1, part_power_derivative_canvas.getDockable());
		
		grid.addDockable(4, 0, 2, 2, part_current_voltage_average_trace_canvas.getDockable());
		grid.addDockable(4, 2, 2, 2, part_current_voltage_cloud_canvas.getDockable());
		
		// Quick fix for dockingframes
		String version = System.getProperty("java.version");
		if (!version.contains(".")) {
			System.setProperty("java.version", "1." + version + ".0");	
		}
		
		// Station
		SplitDockStation station = new SplitDockStation();
		station.dropTree(grid.toTree());
		
		// Controller
		DockController controller = new DockController();
		controller.setTheme(new EclipseTheme());
		controller.add(station);
		
		// Frame
		
		JFrame frame = new JFrame("Welding Data Analysis Platform");
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
