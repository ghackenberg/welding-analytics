package com.hyperkit.analysis.parts;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;

import org.apache.commons.io.FileUtils;

import com.hyperkit.analysis.Part;

public class HelpPart extends Part
{
	
	public HelpPart()
	{
		super("Help", HelpPart.class.getClassLoader().getResource("icons/parts/help.png"));
	}

	@Override
	protected Component createComponent()
	{
		try
		{
			return new JLabel(FileUtils.readFileToString(new File(HelpPart.class.getClassLoader().getResource("help.html").toURI())));
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

}
