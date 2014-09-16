package com.hyperkit.analysis.parts;

import java.awt.Component;

import javax.swing.JLabel;

import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.events.parts.FilePartSelectEvent;

public class PropertyPart extends Part
{
	
	private JLabel label;

	public PropertyPart()
	{
		super("Properties", PropertyPart.class.getClassLoader().getResource("icons/parts/property.png"));
		
		label = new JLabel();
	}

	@Override
	protected Component createComponent()
	{
		return label;
	}
	
	public boolean handleEvent(FilePartSelectEvent e)
	{
		if (e.getASDFile() != null)
		{
			label.setText(e.getASDFile().getFile().getAbsolutePath());
		}
		else
		{
			label.setText("");
		}
		
		return true;
	}

}
