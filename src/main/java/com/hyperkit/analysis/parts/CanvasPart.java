package com.hyperkit.analysis.parts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;

public abstract class CanvasPart extends Part {
	
	private List<ASDFile> files = new ArrayList<>();
	
	private JPanel panel;
	
	public CanvasPart(String title) {
		super(title, ChartPart.class.getClassLoader().getResource("icons/parts/canvas.png"));
		
		CanvasPart self = this;
		
		panel = new JPanel()
		{
			private static final long serialVersionUID = -1222107750580828320L;
			
			@Override
			protected void paintComponent(Graphics graphics)
			{
				super.paintComponent(graphics);
				self.paintComponent(graphics);
			}
		};
		panel.setBackground(Color.white);
	}
	
	@Override
	protected Component createComponent()
	{
		return panel;
	}
	
	public List<ASDFile> getFiles()
	{
		return files;
	}
	
	public JPanel getPanel()
	{
		return panel; 
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{		
		files.add(event.getASDFile());
		
		panel.repaint();
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		files.remove(event.getASDFile());
		
		panel.repaint();
		
		return true;
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		panel.repaint();
		
		return true;
	}
	
	protected abstract void paintComponent(Graphics graphics);

}
