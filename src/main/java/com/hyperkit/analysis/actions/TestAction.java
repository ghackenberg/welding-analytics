package com.hyperkit.analysis.actions;

import bibliothek.gui.Dockable;

import com.hyperkit.analysis.Action;

public class TestAction extends Action
{

	public TestAction()
	{
		super("Test");
	}
	
	@Override
	public void action(Dockable dockable)
	{
		System.out.println("Test action triggered!");
	}

}
