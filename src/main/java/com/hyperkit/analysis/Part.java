package com.hyperkit.analysis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;

public abstract class Part extends Handler
{
	
	private String title;
	private URL icon;
	private List<Action> actions = new LinkedList<>();
	private DefaultDockActionSource source;
	private DefaultDockable dockable;
	private JToolBar toolbar;
	private JPanel container;
	private Component component;
	
	public Part(String title)
	{
		this(title, Part.class.getClassLoader().getResource("icons/part.png"));
	}
	
	public Part(String title, URL icon)
	{
		this.title = title;
		this.icon = icon;
		
		Bus.getInstance().addHandler(this);
	}
	
	public final boolean addAction(Action action)
	{
		return actions.add(action);
	}
	
	public final boolean removeAction(Action action)
	{
		return actions.remove(action);
	}
	
	public final String getTitle()
	{
		return title;
	}
	
	public final Icon getIcon()
	{
		return new ImageIcon(new ImageIcon(icon).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
	}
	
	public final List<Action> getActions()
	{
		return actions;
	}
	
	public final Dockable getDockable()
	{
		if (dockable == null)
		{
			source = new DefaultDockActionSource();
			for (Action action : actions)
			{
				source.add(action);
			}

			dockable =  new DefaultDockable(getContainer(), getTitle(), getIcon());	
			dockable.setActionOffers(source);
		}
		
		return dockable;
	}
	
	public final JToolBar getToolBar()
	{
		if (toolbar == null)
		{			
			toolbar = new JToolBar();
			toolbar.setFloatable(false);
			toolbar.setLayout(new WrapLayout(FlowLayout.LEFT));
		}
		
		return toolbar;
	}
	
	public final Component getComponent()
	{
		if (component == null)
		{
			component = createComponent();
		}
		
		return component;
	}
	
	protected abstract Component createComponent();
	
	public final JPanel getContainer()
	{
		if (container == null)
		{
			container = new JPanel(new BorderLayout());
			container.add(getToolBar(), BorderLayout.NORTH);
			container.add(getComponent(), BorderLayout.CENTER);
		}
		
		return container;
	}

}
