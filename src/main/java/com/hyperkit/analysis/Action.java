package com.hyperkit.analysis;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

import bibliothek.gui.dock.action.actions.SimpleButtonAction;

public abstract class Action extends SimpleButtonAction
{
	
	public Action(String text)
	{
		this(text, text);
	}
	public Action(String text, String tooltip)
	{
		this(text, tooltip, Action.class.getClassLoader().getResource("icons/action.png"));
	}
	public Action(String text, String tooltip, URL icon)
	{
		setText(text);
		setTooltip(tooltip);
		setIcon(new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));
	}

}
