package com.hyperkit.analysis.parts.canvas;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.events.values.AverageChangeEvent;
import com.hyperkit.analysis.events.values.FrameChangeEvent;
import com.hyperkit.analysis.events.values.MarkerChangeEvent;
import com.hyperkit.analysis.events.values.WindowChangeEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.CanvasPart;

public abstract class TraceCanvasPart extends CanvasPart
{
	
	private Map<ASDFile, Integer> starts = new HashMap<>();
	private Map<ASDFile, Integer> ends = new HashMap<>();
	private Map<ASDFile, Integer> counts = new HashMap<>();

	private int frame;
	private int window;
	private int average;
	private int padding;
	
	private Map<ASDFile, Integer> marker;
	
	public TraceCanvasPart(String title, String domain, String range, URL icon, int frame, int window, int average, int padding)
	{
		super(title, domain, range, icon);
		
		this.frame = frame;
		this.window = window;
		this.average = average;
		this.padding = padding;
		
		getPanel().addMouseListener(new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				Bus.getInstance().broadcastEvent(new MarkerChangeEvent(null));
			}
			@Override
			public void mouseEntered(MouseEvent e)
			{
				
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				
			}
		});
	}
	
	public boolean handleEvent(WindowChangeEvent event)
	{
		if (window != event.getValue())
		{
			window = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(FrameChangeEvent event)
	{
		if (frame != event.getValue())
		{
			frame = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(AverageChangeEvent event)
	{
		if (average != event.getValue())
		{
			average = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public boolean handleEvent(MarkerChangeEvent event)
	{
		if (marker != event.getValue())
		{
			marker = event.getValue();
			
			getPanel().repaint();
		}
		
		return true;
	}
	
	public int getAverage()
	{
		return average;
	}

	@Override
	protected void prepareData()
	{
		starts.clear();
		ends.clear();
		counts.clear();
		
		for (ASDFile file : getFiles())
		{
			int end = Math.min(frame + 1, file.getLengthDisplayed());
			int start = Math.max(frame + 1 - window, 0);
			int count = Math.max(0, end - start);
			
			starts.put(file, start);
			ends.put(file, end);
			counts.put(file, count);
		}
	}

	@Override
	protected int getDataLength(ASDFile file)
	{
		return counts.get(file);
	}

	@Override
	protected double getDomainValue(ASDFile file, int index)
	{
		return getRawDomainValue(file, starts.get(file) + index);
	}

	@Override
	protected double getRangeValue(ASDFile file, int index)
	{
		return getRawRangeValue(file, starts.get(file) + index);
	}
	
	@Override
	protected void paintComponent(Graphics2D graphics)
	{
		// Draw trace
		
		for (ASDFile file : getFiles())
		{			
			for (int index = 1 + padding; index < getDataLength(file) - padding; index++)
			{	
				double x1 = getDomainValue(file, index - 1);
				double y1 = getRangeValue(file, index - 1);

				double x2 = getDomainValue(file, index);
				double y2 = getRangeValue(file, index);

				double progress = 1 - (index + 1.0) / getDataLength(file);
				
				drawLine(graphics, calculateColor(file, 1, Math.pow(progress, 10)), x1, y1, x2, y2);
			}
			
			for (int index = Math.max(getDataLength(file) - 1 - padding, padding); index < getDataLength(file) - padding; index++)
			{	
				double x = getDomainValue(file, index);
				double y = getRangeValue(file, index);

				double progress = 1 - (index + 1.0) / getDataLength(file);
				
				drawPoint(graphics, calculateColor(file, 0.5, Math.pow(progress, 10)), x, y);
			}
		}
		
		// Update marker

		if (getMouseCurrentX() != Integer.MAX_VALUE && getMouseCurrentY() != Integer.MAX_VALUE)
		{
			marker = new HashMap<>();
			
			for (ASDFile file : getFiles())
			{
				int marker_index = Integer.MAX_VALUE;
				double marker_distance = Double.MAX_VALUE;
				
				for (int index = padding; index < getDataLength(file) - padding; index++)
				{
					double x = getDomainValue(file, index);
					double y = getRangeValue(file, index);
					
					double domain_delta = getMouseCurrentX() - projectDomain(x);
					double range_delta = getMouseCurrentY() - projectRange(y);
					
					double temp = Math.sqrt(domain_delta * domain_delta + range_delta * range_delta);
					
					if (temp < marker_distance)
					{
						marker_index = index;
						marker_distance = temp;
					}
				}
				
				if (marker_index != Integer.MAX_VALUE)
				{
					marker.put(file, starts.get(file) + marker_index);
				}
			}
			
			Bus.getInstance().broadcastEvent(new MarkerChangeEvent(marker));
		}
		
		// Draw marker
		
		if (marker != null)
		{	
			for (Entry<ASDFile, Integer> entry : marker.entrySet())
			{
				ASDFile file = entry.getKey();
				
				Integer index = entry.getValue();
				
				if (index >= padding && starts.containsKey(file))
				{
					drawMarker(graphics, calculateColor(file, 0.5, Math.pow(0, 10)), getRawDomainValue(file, index), getRawRangeValue(file, index));
				}
			}
		}
	}
	
	protected abstract double getRawDomainValue(ASDFile file, int index);
	protected abstract double getRawRangeValue(ASDFile file, int index);

}
