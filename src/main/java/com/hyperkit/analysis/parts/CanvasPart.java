package com.hyperkit.analysis.parts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;

public abstract class CanvasPart extends Part {
	
	private List<ASDFile> files = new ArrayList<>();

	private JToolBar toolbar;
	private JPanel panel;
	private JPanel outer;
	
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
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		outer = new JPanel(new BorderLayout());
		outer.add(toolbar, BorderLayout.NORTH);
		outer.add(panel, BorderLayout.CENTER);
	}
	
	@Override
	protected Component createComponent()
	{
		return outer;
	}
	
	public List<ASDFile> getFiles()
	{
		return files;
	}
	
	public JPanel getPanel()
	{
		return panel; 
	}
	
	public JToolBar getToolBar()
	{
		return toolbar;
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
