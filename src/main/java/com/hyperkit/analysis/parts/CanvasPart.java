package com.hyperkit.analysis.parts;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;
import com.hyperkit.analysis.files.ASDFile;

public abstract class CanvasPart extends Part
{
	
	private static final Color LOW = new Color(0,0,0);
	private static final Color MEDIUM = new Color(160,160,160);
	private static final Color HIGH = new Color(224,224,224);
	
	private List<ASDFile> files = new ArrayList<>();
	
	private JPanel panel;
	
	private int width;
	private int height;
	
	private int padding_top = 10;
	private int padding_left = 50;
	private int padding_right = 10;
	private int padding_bottom = 50;
	
	private double domain_lower;
	private double domain_upper;
	private double domain_delta;
	private double domain_rect = Double.MAX_VALUE;
	private double domain_mouse = Double.MAX_VALUE;
	
	private double range_lower;
	private double range_upper;
	private double range_delta;
	private double range_rect = Double.MAX_VALUE;
	private double range_mouse = Double.MAX_VALUE;
	
	public CanvasPart(String title, String domain, String range)
	{
		this(title, domain, range, ChartPart.class.getClassLoader().getResource("icons/parts/canvas.png"));
	}
	
	public CanvasPart(String title, String domain, String range, URL icon)
	{
		super(title, icon);
		
		CanvasPart self = this;
		
		panel = new JPanel()
		{
			private static final long serialVersionUID = -1222107750580828320L;
			
			@Override
			protected void paintComponent(Graphics graphics)
			{
				synchronized (files)
				{
					super.paintComponent(graphics);
					
					Graphics2D graphics2D = (Graphics2D) graphics;
					
					width = panel.getWidth();
					height = panel.getHeight();
					
					prepareData();
					
					domain_lower = +Double.MAX_VALUE;
					domain_upper = -Double.MAX_VALUE;
					
					range_lower = +Double.MAX_VALUE;
					range_upper = -Double.MAX_VALUE;
					
					for (ASDFile file : files)
					{
						domain_lower = Math.min(domain_lower, getDomainMinimum(file));
						domain_upper = Math.max(domain_upper, getDomainMaximum(file));
						
						range_lower = Math.min(range_lower, getRangeMinimum(file));
						range_upper = Math.max(range_upper, getRangeMaximum(file));
					}
					
					if (domain_lower == Double.MAX_VALUE)
					{
						domain_lower = 0;
						domain_upper = 1;
						
						range_lower = 0;
						range_upper = 1;
					}
					
					if (domain_lower == domain_upper)
					{
						domain_lower -= 0.5;
						domain_upper += 0.5;
					}
					
					if (range_lower == range_upper)
					{
						range_lower -= 0.5;
						range_upper += 0.5;
					}
					
					domain_delta = domain_upper - domain_lower;
					range_delta = range_upper - range_lower;
					
					domain_lower -= domain_delta * 0.1;
					domain_upper += domain_delta * 0.1;
					
					range_lower -= range_delta * 0.1;
					range_upper += range_delta * 0.1;
					
					domain_delta *= 1.2;
					range_delta *= 1.2;
					
					FontMetrics metrics = graphics.getFontMetrics();
					String string;
					Rectangle2D bounds;
					AffineTransform transform;
					
					int xticks = 1;
					int yticks = 1;
					
					double dx;
					double dy;
					
					double unit;
					
					do
					{
						dx = crop((domain_upper - domain_lower) / xticks);
						
						string = String.format("%." + digits(dx) + "f", domain_upper);
						bounds = metrics.getStringBounds(string, graphics);
						
						unit = bounds.getWidth();
						
						string = String.format("%." + digits(dx) + "f", domain_lower);
						bounds = metrics.getStringBounds(string, graphics);
						
						unit = Math.max(unit, bounds.getWidth());
					}
					while (xticks++ < (width - padding_left - padding_right) / unit / 2);
					
					do
					{
						dy = crop((range_upper - range_lower) / yticks);
						
						string = String.format("%." + digits(dy) + "f", range_upper);
						bounds = metrics.getStringBounds(string, graphics);
						
						unit = bounds.getWidth();
						
						string = String.format("%." + digits(dy) + "f", range_lower);
						bounds = metrics.getStringBounds(string, graphics);
						
						unit = Math.max(unit, bounds.getWidth());
					}
					while (yticks++ < (height - padding_top - padding_bottom) / unit / 2);
					
					for (double x = Math.ceil(domain_lower / dx); x <= Math.floor(domain_upper / dx); x++)
					{	
						graphics.setColor(x == 0 ? MEDIUM : HIGH);
						graphics.drawLine((int) projectX(x * dx), (int) projectY(range_lower), (int) projectX(x * dx), (int) projectY(range_upper));
					}
					
					for (double y = Math.ceil(range_lower / dy); y <= Math.floor(range_upper / dy); y++)
					{
						graphics.setColor(y == 0 ? MEDIUM : HIGH);
						graphics.drawLine((int) projectX(domain_lower), (int) projectY(y * dy), (int) projectX(domain_upper), (int) projectY(y * dy));
					}
					
					self.paintComponent(graphics);
					
					drawLine(graphics, LOW, domain_lower, range_lower, domain_upper, range_lower);
					drawLine(graphics, LOW, domain_lower, range_upper, domain_lower, range_lower);
					
					graphics.setColor(LOW);
					graphics.fillPolygon(new int[] {(int) projectX(domain_upper), (int) projectX(domain_upper), (int) projectX(domain_upper) + padding_right / 2}, new int[] {(int) projectY(range_lower) - padding_right / 3, (int) projectY(range_lower) + padding_right / 3, (int) projectY(range_lower)}, 3);
					
					graphics.setColor(LOW);
					graphics.fillPolygon(new int[] {(int) projectX(domain_lower) - padding_top / 3, (int) projectX(domain_lower) + padding_top / 3, (int) projectX(domain_lower)}, new int[] {(int) projectY(range_upper), (int) projectY(range_upper), (int) projectY(range_upper) - padding_top / 2}, 3);
					
					for (double x = Math.ceil(domain_lower / dx); x <= Math.floor(domain_upper / dx); x++)
					{
						string = String.format("%." + digits(dx) + "f", x * dx);
						
						if (x == 0 && string.startsWith("-"))
						{
							string = string.substring(1);
						}
						
						bounds = metrics.getStringBounds(string, graphics);
						
						graphics.setColor(LOW);
						graphics.drawLine((int) projectX(x * dx), (int) projectY(range_lower) - 2, (int) projectX(x * dx), (int) projectY(range_lower) + 2);
						graphics.drawString(string, (int) (projectX(x * dx) - bounds.getWidth() / 2), (int) (projectY(range_lower) + 2 + bounds.getHeight()));
					}
					
					for (double y = Math.ceil(range_lower / dy); y <= Math.floor(range_upper / dy); y++)
					{
						string = String.format("%." + digits(dy) + "f", y * dy);
						
						if (y == 0 && string.startsWith("-"))
						{
							string = string.substring(1);
						}
						
						bounds = metrics.getStringBounds(string, graphics);
						
						graphics.setColor(LOW);
						graphics.drawLine((int) projectX(domain_lower) - 2, (int) projectY(y * dy), (int) projectX(domain_lower) + 2, (int) projectY(y * dy));
						
						transform = graphics2D.getTransform();
						
						graphics2D.translate(projectX(domain_lower) - bounds.getHeight() / 2, projectY(y * dy) + bounds.getWidth() / 2);
						graphics2D.rotate(- Math.PI / 2);
						graphics.setColor(LOW);
						graphics.drawString(string, 0, 0);
						
						graphics2D.setTransform(transform);
					}
					
					bounds = metrics.getStringBounds(domain, graphics);
					
					graphics.setColor(LOW);
					graphics.drawString(domain, (int) (projectX(domain_lower + domain_delta / 2) - bounds.getWidth() / 2), (int) (projectY(range_lower) + padding_bottom / 3 * 2 + bounds.getHeight() / 2));
					
					bounds = metrics.getStringBounds(range, graphics);
					
					transform = graphics2D.getTransform();
					
					graphics2D.translate(projectX(domain_lower) - padding_left / 3 * 2 - bounds.getHeight() / 2, projectY(range_lower + range_delta / 2) + bounds.getWidth() / 2);
					graphics2D.rotate(- Math.PI / 2);
					graphics.setColor(LOW);
					graphics.drawString(range, 0, 0);
					
					graphics2D.setTransform(transform);
					
					// Draw interaction markers
					
					if (domain_rect != Double.MAX_VALUE && range_rect != Double.MAX_VALUE)
					{
						// Draw selection marker
						
						drawRectangle(graphics2D, Color.BLACK, domain_rect, range_rect, domain_mouse, range_mouse);
						
						drawLine(graphics2D, Color.BLACK, domain_rect, range_rect, domain_mouse, range_rect);
						drawLine(graphics2D, Color.BLACK, domain_rect, range_rect, domain_rect, range_mouse);
					}
					
					// Draw mouse marker
					
					drawPoint(graphics2D, Color.BLACK, domain_mouse, range_mouse);
					
					drawLine(graphics2D, Color.BLACK, domain_mouse, Math.min(range_mouse, range_lower), domain_mouse, Math.max(range_mouse, range_upper));
					drawLine(graphics2D, Color.BLACK, Math.min(domain_mouse, domain_lower), range_mouse, Math.max(domain_mouse, domain_upper), range_mouse);
				}
			}
		};
		panel.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				domain_rect = Double.MAX_VALUE;
				range_rect = Double.MAX_VALUE;
				
				updateMouseMarker(e.getX(), e.getY());
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				domain_rect = calculateMouseX(e.getX());
				range_rect = calculateMouseY(e.getY());
				
				updateMouseMarker(e.getX(), e.getY());
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				updateMouseMarker(Double.MAX_VALUE, Double.MAX_VALUE);
			}
			@Override
			public void mouseEntered(MouseEvent e)
			{
				updateMouseMarker(e.getX(), e.getY());
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				updateMouseMarker(e.getX(), e.getY());
			}
		});
		panel.addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				updateMouseMarker(e.getX(), e.getY());
			}
			@Override
			public void mouseDragged(MouseEvent e)
			{
				updateMouseMarker(e.getX(), e.getY());
			}
		});
		panel.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				updateMouseMarker(e.getX(), e.getY());
			}
		});
		panel.setBackground(Color.white);
	}
	
	private double calculateMouseX(double sx)
	{
		double width = panel.getWidth();
		
		double px = (sx - padding_left) / (width - padding_left - padding_right);
		
		return domain_lower + px * domain_delta;
	}
	
	private double calculateMouseY(double sy)
	{
		double height = panel.getHeight();
		
		double py = (sy - padding_top) / (height - padding_top - padding_bottom);
		
		return range_upper - py * range_delta;
	}
	
	private void updateMouseMarker(double sx, double sy)
	{
		domain_mouse = calculateMouseX(sx);
		range_mouse = calculateMouseY(sy);
		
		panel.repaint();
	}
	
	protected double crop(double value)
	{
		if (value > 0)
		{
			if (value < 1)
			{
				int count = 0;
				
				while (value < 1)
				{
					value *= 10;
					count++;
				}
				
				value = Math.ceil(value);
				
				while (count > 0)
				{
					value /= 10;
					count--;
				}
			}
			else
			{
				value = Math.ceil(value);
			}		
		}
		
		return value;
	}
	
	protected int digits(double value)
	{
		if (value > 0 && value < 1)
		{
			int count = 0;
			
			while (value < 1)
			{
				value *= 10;
				count++;
			}
			
			return count;
		}
		
		return 0;
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
		synchronized (files)
		{
			files.add(event.getASDFile());
		}
		
		panel.repaint();
		
		return true;
	}
	
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		synchronized (files)
		{
			files.remove(event.getASDFile());
		}
		
		panel.repaint();
		
		return true;
	}
	
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		panel.repaint();
		
		return true;
	}
	
	protected double projectX(double x)
	{
		return padding_left + (width - padding_left - padding_right) * (x - domain_lower) / domain_delta;
	}
	
	protected double projectY(double y)
	{
		return padding_top + (height - padding_top - padding_bottom) - (height - padding_top - padding_bottom) * (y - range_lower) / range_delta;
	}
	
	protected double calculateColor(int value, double shade, double progress)
	{
		return shade * value + (255 - shade * value) * progress;
	}
	
	protected Color calculateColor(Color color, double shade, double progress)
	{
		double r = calculateColor(color.getRed(), shade, progress);
		double g = calculateColor(color.getGreen(), shade, progress);
		double b = calculateColor(color.getBlue(), shade, progress);
		
		return new Color((int) r, (int) g, (int) b);
	}
	
	protected Color calculateColor(ASDFile file, double shade, double progress)
	{
		return calculateColor(file.getColor(), shade, progress);
	}
	
	protected void drawPoint(Graphics graphics, Color color, double x, double y)
	{
		graphics.setColor(color);
		graphics.fillOval((int) projectX(x) - 2, (int) projectY(y) - 2, 4, 4);
	}
	
	protected void drawLine(Graphics graphics, Color color, double x1, double y1, double x2, double y2)
	{	
		graphics.setColor(color);
		graphics.drawLine((int) projectX(x1), (int) projectY(y1), (int) projectX(x2), (int) projectY(y2));
	}
	
	protected void drawRectangle(Graphics2D graphics, Color color, double x1, double y1, double x2, double y2)
	{
		int sx1 = (int) projectX(x1);
		int sy1 = (int) projectY(y1);
		
		int sx2 = (int) projectX(x2);
		int sy2 = (int) projectY(y2);
		
		graphics.setComposite(AlphaComposite.SrcOver.derive(0.5f));
		
		graphics.setColor(color);
		graphics.fillRect(Math.min(sx1, sx2), Math.min(sy1, sy2), Math.abs(sx2 - sx1), Math.abs(sy2 - sy1));
		
		graphics.setComposite(AlphaComposite.SrcOver);
	}
	
	protected abstract void prepareData();
	
	protected abstract double getDomainMinimum(ASDFile file);
	protected abstract double getRangeMinimum(ASDFile file);
	
	protected abstract double getDomainMaximum(ASDFile file);
	protected abstract double getRangeMaximum(ASDFile file);
	
	protected abstract int getDataLength(ASDFile file);
	
	protected abstract double getDomainValue(ASDFile file, int index);	
	protected abstract double getRangeValue(ASDFile file, int index);
	
	protected abstract void paintComponent(Graphics graphics);

}
