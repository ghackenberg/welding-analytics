package com.hyperkit.analysis.actions;

import java.net.URL;

import com.hyperkit.analysis.Action;
import com.hyperkit.analysis.Part;

public abstract class PartAction<P extends Part> extends Action
{
	
	private P part;

	public PartAction(P part, String text)
	{
		super(text);
		
		this.part = part;
	}
	public PartAction(P part, String text, String tooltip)
	{
		super(text, tooltip);
		
		this.part = part;
	}
	public PartAction(P part, String text, String tooltip, URL icon)
	{
		super(text, tooltip, icon);
		
		this.part = part;
	}
	
	public P getPart()
	{
		return part;
	}

}
