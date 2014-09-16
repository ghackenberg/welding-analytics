package com.hyperkit.analysis.parts;

import java.awt.Component;

import javax.swing.JLabel;

import com.hyperkit.analysis.Part;

public class TestPart extends Part
{

	public TestPart()
	{
		super("Test");
	}

	@Override
	protected Component createComponent()
	{
		return new JLabel("Test part");
	}

}
