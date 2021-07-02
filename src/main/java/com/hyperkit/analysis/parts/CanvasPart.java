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
	
	private double domain_lower_custom = -Double.MAX_VALUE;
	private double domain_upper_custom = +Double.MAX_VALUE;
	
	private double range_lower_custom = -Double.MAX_VALUE;
	private double range_upper_custom = +Double.MAX_VALUE;
	
	private double domain_lower;
	private double domain_upper;
	private double domain_delta;
	
	private double range_lower;
	private double range_upper;
	private double range_delta;
	
	private int mouse_previous_x = Integer.MAX_VALUE;
	private int mouse_previous_y = Integer.MAX_VALUE;
	
	private int mouse_current_x = Integer.MAX_VALUE;
	private int mouse_current_y = Integer.MAX_VALUE;
	
	public CanvasPart(String title, String domain, String range, boolean zoom_domain, boolean zoom_range)
	{
		this(title, domain, range, ChartPart.class.getClassLoader().getResource("icons/parts/canvas.png"), zoom_domain, zoom_range);
	}
	
	public CanvasPart(String title, String domain, String range, URL icon, boolean zoom_domain, boolean zoom_range)
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
					
					domain_lower = Math.max(domain_lower, domain_lower_custom);
					domain_upper = Math.min(domain_upper, domain_upper_custom);
					
					range_lower = Math.max(range_lower, range_lower_custom);
					range_upper = Math.min(range_upper, range_upper_custom);
					
					domain_delta = domain_upper - domain_lower;
					range_delta = range_upper - range_lower;
					
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
						graphics.drawLine((int) projectDomain(x * dx), (int) projectRange(range_lower), (int) projectDomain(x * dx), (int) projectRange(range_upper));
					}
					
					for (double y = Math.ceil(range_lower / dy); y <= Math.floor(range_upper / dy); y++)
					{
						graphics.setColor(y == 0 ? MEDIUM : HIGH);
						graphics.drawLine((int) projectDomain(domain_lower), (int) projectRange(y * dy), (int) projectDomain(domain_upper), (int) projectRange(y * dy));
					}
					
					self.paintComponent(graphics2D);
					
					drawLine(graphics2D, LOW, domain_lower, range_lower, domain_upper, range_lower);
					drawLine(graphics2D, LOW, domain_lower, range_upper, domain_lower, range_lower);
					
					graphics.setColor(LOW);
					graphics.fillPolygon(new int[] {(int) projectDomain(domain_upper), (int) projectDomain(domain_upper), (int) projectDomain(domain_upper) + padding_right / 2}, new int[] {(int) projectRange(range_lower) - padding_right / 3, (int) projectRange(range_lower) + padding_right / 3, (int) projectRange(range_lower)}, 3);
					
					graphics.setColor(LOW);
					graphics.fillPolygon(new int[] {(int) projectDomain(domain_lower) - padding_top / 3, (int) projectDomain(domain_lower) + padding_top / 3, (int) projectDomain(domain_lower)}, new int[] {(int) projectRange(range_upper), (int) projectRange(range_upper), (int) projectRange(range_upper) - padding_top / 2}, 3);
					
					for (double x = Math.ceil(domain_lower / dx); x <= Math.floor(domain_upper / dx); x++)
					{
						string = String.format("%." + digits(dx) + "f", x * dx);
						
						if (x == 0 && string.startsWith("-"))
						{
							string = string.substring(1);
						}
						
						bounds = metrics.getStringBounds(string, graphics);
						
						graphics.setColor(LOW);
						graphics.drawLine((int) projectDomain(x * dx), (int) projectRange(range_lower) - 2, (int) projectDomain(x * dx), (int) projectRange(range_lower) + 2);
						graphics.drawString(string, (int) (projectDomain(x * dx) - bounds.getWidth() / 2), (int) (projectRange(range_lower) + 2 + bounds.getHeight()));
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
						graphics.drawLine((int) projectDomain(domain_lower) - 2, (int) projectRange(y * dy), (int) projectDomain(domain_lower) + 2, (int) projectRange(y * dy));
						
						transform = graphics2D.getTransform();
						
						graphics2D.translate(projectDomain(domain_lower) - bounds.getHeight() / 2, projectRange(y * dy) + bounds.getWidth() / 2);
						graphics2D.rotate(- Math.PI / 2);
						graphics.setColor(LOW);
						graphics.drawString(string, 0, 0);
						
						graphics2D.setTransform(transform);
					}
					
					bounds = metrics.getStringBounds(domain, graphics);
					
					graphics.setColor(LOW);
					graphics.drawString(domain, (int) (projectDomain(domain_lower + domain_delta / 2) - bounds.getWidth() / 2), (int) (projectRange(range_lower) + padding_bottom / 3 * 2 + bounds.getHeight() / 2));
					
					bounds = metrics.getStringBounds(range, graphics);
					
					transform = graphics2D.getTransform();
					
					graphics2D.translate(projectDomain(domain_lower) - padding_left / 3 * 2 - bounds.getHeight() / 2, projectRange(range_lower + range_delta / 2) + bounds.getWidth() / 2);
					graphics2D.rotate(- Math.PI / 2);
					graphics.setColor(LOW);
					graphics.drawString(range, 0, 0);
					
					graphics2D.setTransform(transform);

					// Draw selection marker
					
					if (mouse_previous_x != Integer.MAX_VALUE && mouse_previous_y != Integer.MAX_VALUE)
					{
						int x1 = Math.min(Math.max(mouse_previous_x, padding_left), panel.getWidth() - padding_right);
						int y1 = Math.min(Math.max(mouse_previous_y, padding_top), panel.getHeight() - padding_bottom);

						int x2 = Math.min(Math.max(mouse_current_x, padding_left), panel.getWidth() - padding_right);
						int y2 = Math.min(Math.max(mouse_current_y, padding_top), panel.getHeight() - padding_bottom);
						
						if (!zoom_domain)
						{
							x1 = padding_left;
							x2 = panel.getWidth() - padding_right;
						}
						if (!zoom_range)
						{
							y1 = padding_top;
							y2 = panel.getHeight() - padding_bottom;
						}
						
						drawRectangle(graphics2D, Color.BLACK, x1, y1, x2, y2);
						
						if (zoom_domain)
						{
							drawLine(graphics2D, Color.BLACK, x1, y1, x1, y2);
							drawLine(graphics2D, Color.BLACK, x2, y1, x2, y2);
						}
						if (zoom_range)
						{
							drawLine(graphics2D, Color.BLACK, x1, y1, x2, y1);
							drawLine(graphics2D, Color.BLACK, x1, y2, x2, y2);
						}
					}
				}
			}
		};
		panel.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (zoom_domain)
				{
					if (mouse_previous_x >= e.getX())
					{
						domain_lower_custom = -Double.MAX_VALUE;
						domain_upper_custom = +Double.MAX_VALUE;
					}
					else
					{
						int x1 = Math.min(Math.max(mouse_previous_x, padding_left), panel.getWidth() - padding_right);
						int x2 = Math.min(Math.max(e.getX(), padding_left), panel.getWidth() - padding_right);
						
						domain_lower_custom = projectScreenX(x1);
						domain_upper_custom = projectScreenX(x2);	
					}
				}
				if (zoom_range)
				{
					if (mouse_previous_y >= e.getY())
					{
						range_lower_custom = -Double.MAX_VALUE;
						range_upper_custom = +Double.MAX_VALUE;
					}
					else
					{
						int y1 = Math.min(Math.max(mouse_previous_y, padding_top), panel.getHeight() - padding_bottom);
						int y2 = Math.min(Math.max(e.getY(), padding_top), panel.getHeight() - padding_bottom);
						
						range_lower_custom = projectScreenY(y2);
						range_upper_custom = projectScreenY(y1);
					}
				}
				
				updateMousePrevious(Integer.MAX_VALUE, Integer.MAX_VALUE);
				updateMouseCurrent(e.getX(), e.getY());
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (zoom_domain || zoom_range)
				{
					updateMousePrevious(e.getX(), e.getY());
				}
				updateMouseCurrent(e.getX(), e.getY());
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				updateMouseCurrent(Integer.MAX_VALUE, Integer.MAX_VALUE);
			}
			@Override
			public void mouseEntered(MouseEvent e)
			{
				updateMouseCurrent(e.getX(), e.getY());
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				updateMouseCurrent(e.getX(), e.getY());
			}
		});
		panel.addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				updateMouseCurrent(e.getX(), e.getY());
			}
			@Override
			public void mouseDragged(MouseEvent e)
			{
				updateMouseCurrent(e.getX(), e.getY());
			}
		});
		panel.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				updateMouseCurrent(e.getX(), e.getY());
			}
		});
		panel.setBackground(Color.white);
	}
	
	protected int getPaddingLeft()
	{
		return padding_left;
	}
	protected int getPaddingRight()
	{
		return padding_right;
	}
	protected int getPaddingTop()
	{
		return padding_top;
	}
	protected int getPaddingBottom()
	{
		return padding_bottom;
	}
	
	private double projectScreenX(double sx)
	{
		double width = panel.getWidth();
		
		double px = (sx - padding_left) / (width - padding_left - padding_right);
		
		return domain_lower + px * domain_delta;
	}
	
	private double projectScreenY(double sy)
	{
		double height = panel.getHeight();
		
		double py = (sy - padding_top) / (height - padding_top - padding_bottom);
		
		return range_upper - py * range_delta;
	}
	
	private void updateMousePrevious(int sx, int sy)
	{
		mouse_previous_x = sx;
		mouse_previous_y = sy;
	}
	
	private void updateMouseCurrent(int sx, int sy)
	{
		mouse_current_x = sx;
		mouse_current_y = sy;
		
		panel.repaint();
	}
	
	protected boolean check(double x, double y)
	{
		return x >= domain_lower && x <= domain_upper && y >= range_lower && y <= range_upper;
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
	
	protected double getDomainLower()
	{
		return domain_lower;
	}
	protected double getDomainUpper()
	{
		return domain_upper;
	}
	
	protected double getRangeLower()
	{
		return range_lower;
	}
	protected double getRangeUpper()
	{
		return range_upper;
	}

	protected int getMousePreviousX()
	{
		return mouse_previous_x;
	}
	protected int getMousePreviousY()
	{
		return mouse_previous_y;	
	}
	
	protected int getMouseCurrentX()
	{
		return mouse_current_x;
	}
	protected int getMouseCurrentY()
	{
		return mouse_current_y;	
	}
	
	protected double getMousePreviousDomainValue()
	{
		return projectScreenX(mouse_previous_x);
	}
	protected double getMousePreviousRangeValue()
	{
		return projectScreenY(mouse_previous_y);
	}
	
	protected double getMouseCurrentDomainValue()
	{
		return projectScreenX(mouse_current_x);
	}
	protected double getMouseCurrentRangeValue()
	{
		return projectScreenY(mouse_current_y);
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
	
	protected double projectDomain(double x)
	{
		return padding_left + (width - padding_left - padding_right) * (x - domain_lower) / domain_delta;
	}
	
	protected double projectRange(double y)
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
		if (check(x, y))
		{
			int sx = (int) projectDomain(x);
			int sy = (int) projectRange(y);
			
			drawPoint(graphics, color, sx, sy);	
		}
	}
	
	protected void drawPoint(Graphics graphics, Color color, int x, int y)
	{
		graphics.setColor(color);
		
		graphics.fillOval(x - 2, y - 2, 4, 4);
	}
	
	protected void drawLine(Graphics2D graphics, Color color, double x1, double y1, double x2, double y2)
	{
		boolean check1 = check(x1, y1);
		boolean check2 = check(x2, y2);
		
		int sx1 = (int) projectDomain(x1);
		int sy1 = (int) projectRange(y1);
		
		int sx2 = (int) projectDomain(x2);
		int sy2 = (int) projectRange(y2);
		
		if (check1 && check2)
		{	
			drawLine(graphics, color, sx1, sy1, sx2, sy2);	
		}
		else
		{
			int top = padding_top;
			int left = padding_left;
			int right = panel.getWidth() - padding_right;
			int bottom = panel.getHeight() - padding_bottom;
			
			// Cross left bar
			if (sx1 < left && sx2 > left)
			{
				sy1 += (int) ((sy2 - sy1) * (left - sx1) / (double) (sx2 - sx1));
				sx1 = left;
			}
			else if (sx1 > left && sx2 < left)
			{
				sy2 += (int) ((sy1 - sy2) * (left - sx2) / (double) (sx1 - sx2));
				sx2 = left;
			}
			
			// Cross right bar
			if (sx1 < right && sx2 > right)
			{
				sy2 += (int) ((sy1 - sy2) * (right - sx2) / (double) (sx1 - sx2));
				sx2 = right;
			}
			else if (sx1 > right && sx2 < right)
			{
				sy1 += (int) ((sy2 - sy1) * (right - sx1) / (double) (sx2 - sx1));
				sx1 = right;
			}
			
			// Cross top bar
			if (sy1 < top && sy2 > top)
			{
				sx1 += (int) ((sx2 - sx1) * (top - sy1) / (double) (sy2 - sy1)); 
				sy1 = top;
			}
			else if (sy1 > top && sy2 < top)
			{
				sx2 += (int) ((sx1 - sx2) * (top - sy2) / (double) (sy1 - sy2)); 
				sy2 = top;
			}
			
			// Cross bottom bar
			if (sy1 < bottom && sy2 > bottom)
			{
				sx2 += (int) ((sx1 - sx2) * (bottom - sy2) / (double) (sy1 - sy2));
				sy2 = bottom;
			}
			else if (sy1 > bottom & sy2 < bottom)
			{
				sx1 += (int) ((sx2 - sx1) * (bottom - sy1) / (double) (sy2 - sy1));
				sy1 = bottom;
			}
			
			// Draw (if necessary)
			if (sx1 >= left && sx1 <= right && sy1 >= top && sy1 <= bottom)
			{
				if (sx2 >= left && sx2 <= right && sy2 >= top && sy2 <= bottom)
				{
					drawLine(graphics, color, sx1, sy1, sx2, sy2);
				}
			}
		}
	}
	
	protected void drawLine(Graphics2D graphics, Color color, int x1, int y1, int x2, int y2)
	{
		graphics.setColor(color);
		
		graphics.drawLine(x1, y1, x2, y2);
	}
	
	protected void drawMarker(Graphics2D graphics, Color color, double x, double y)
	{
		drawLine(graphics, Color.BLACK, x, range_lower, x, range_upper);
		drawLine(graphics, Color.BLACK, domain_lower, y, domain_upper, y);
		
		drawPoint(graphics, color, x, y);
	}
	
	protected void drawRectangle(Graphics2D graphics, Color color, double x1, double y1, double x2, double y2)
	{
		int sx1 = (int) projectDomain(x1);
		int sy1 = (int) projectRange(y1);
		
		int sx2 = (int) projectDomain(x2);
		int sy2 = (int) projectRange(y2);
		
		drawRectangle(graphics, color, sx1, sy1, sx2, sy2);
	}
	
	protected void drawRectangle(Graphics2D graphics, Color color, int x1, int y1, int x2, int y2)
	{
		graphics.setComposite(AlphaComposite.SrcOver.derive(0.25f));
		
		graphics.setColor(color);
		
		graphics.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
		
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
	
	protected abstract void paintComponent(Graphics2D graphics);

}
