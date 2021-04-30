package com.hyperkit.analysis.parts.canvases;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import com.hyperkit.analysis.Main;
import com.hyperkit.analysis.events.AnimationChangeEvent;
import com.hyperkit.analysis.events.PointChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public class PointCloudAnimationCanvasPart extends CanvasPart {
	
	private int point = 1000;
	private int progress = 0;
	
	public PointCloudAnimationCanvasPart() {
		super("Point cloud animation");
		
		// Point
		
		JSpinner pointSpinner = new JSpinner(new SpinnerNumberModel(point, 100, 100000, 100));
		pointSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new PointChangeEvent((int) pointSpinner.getValue()));
			}
		);
		
		// Progress
		
		JSpinner progressSpinner = new JSpinner(new SpinnerNumberModel(progress, 0, Integer.MAX_VALUE, 1));
		progressSpinner.addChangeListener(
			event ->
			{
				this.handleEvent(new AnimationChangeEvent((int) progressSpinner.getValue()));
			}
		);
		
		// Delta
		
		JSpinner delta = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		
		// Timer
		
		Timer timer = new Timer(10,
			event ->
			{
				progressSpinner.setValue((int) progressSpinner.getValue() + (int) delta.getValue());
			}
		);
		
		// Delay
		
		JSpinner delay = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
		delay.addChangeListener(
			event ->
			{
				timer.setDelay((int) delay.getValue());
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

		getToolBar().add(new JLabel("Point count"));
		getToolBar().add(pointSpinner);
		getToolBar().add(new JLabel("Frame number:"));
		getToolBar().add(progressSpinner);
		getToolBar().add(new JLabel("Time step:"));
		getToolBar().add(delta);
		getToolBar().add(new JLabel("Frame delay:"));
		getToolBar().add(delay);
		getToolBar().add(button_play);
	}
	
	public boolean handleEvent(PointChangeEvent event)
	{
		point = event.getPoint();
		
		getPanel().repaint();
		
		return true;
	}
	
	public boolean handleEvent(AnimationChangeEvent event)
	{
		progress = event.getProgress();
		
		getPanel().repaint();
		
		return true;
	}
	
	@Override
	protected void paintComponent(Graphics graphics)
	{	
		double range_lower = +Double.MAX_VALUE;
		double range_upper = -Double.MAX_VALUE;
		
		double domain_lower = +Double.MAX_VALUE;
		double domain_upper = -Double.MAX_VALUE;
		
		for (ASDFile file : getFiles())
		{
			range_lower = Math.min(range_lower, file.getMinVoltageDisplayed());
			range_upper = Math.max(range_upper, file.getMaxVoltageDisplayed());
			
			domain_lower = Math.min(domain_lower, file.getMinCurrentDisplayed());
			domain_upper = Math.max(domain_upper, file.getMaxCurrentDisplayed());
		}
		
		double range_delta = range_upper - range_lower;
		double domain_delta = domain_upper - domain_lower;
		
		range_lower -= range_delta * 0.1;
		range_upper += range_delta * 0.1;
		
		domain_lower -= domain_delta * 0.1;
		domain_upper += domain_delta * 0.1;
		
		range_delta *= 1.2;
		domain_delta *= 1.2;
		
		double width = getPanel().getWidth();
		double height = getPanel().getHeight();
		
		for (ASDFile file : getFiles())
		{
			Color color = file.getColor();
			
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			
			double[][] data = file.getCurrentVoltage(point, progress);
			
			assert data.length == 2;
			assert data[0].length == data[1].length;
			
			for (int index = 0; index < data[0].length; index++)
			{	
				double x = width * (data[0][index] - domain_lower) / domain_delta;
				double y = height - height * (data[1][index] - range_lower) / range_delta;

				double progress = 1 - (1.0) / data[0].length;
				
				double r = red + (255 - red) * progress;
				double g = green + (255 - green) * progress;
				double b = blue + (255 - blue) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.fillOval((int) x - 2, (int) y - 2, 4, 4);
			}
			
			for (int index = 1; index < data[0].length; index++)
			{	
				double x1 = width * (data[0][index - 1] - domain_lower) / domain_delta;
				double y1 = height - height * (data[1][index - 1] - range_lower) / range_delta;

				double x2 = width * (data[0][index] - domain_lower) / domain_delta;
				double y2 = height - height * (data[1][index] - range_lower) / range_delta;

				double progress = 1 - (index + 1.0) / data[0].length;
				
				double r = red * 0.5 + (255 - red * 0.5) * progress;
				double g = green * 0.5 + (255 - green * 0.5) * progress;
				double b = blue * 0.5 + (255 - blue * 0.5) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			}
			
			for (int index = 0; index < data[0].length; index++)
			{	
				double x = width * (data[0][index] - domain_lower) / domain_delta;
				double y = height - height * (data[1][index] - range_lower) / range_delta;

				double progress = 1 - (index + 1.0) / data[0].length;
				
				double r = red + (255 - red) * progress;
				double g = green + (255 - green) * progress;
				double b = blue + (255 - blue) * progress;
				
				graphics.setColor(new Color((int) r, (int) g, (int) b));
				graphics.fillOval((int) x - 2, (int) y - 2, 4, 4);
			}
		}
	}

}
