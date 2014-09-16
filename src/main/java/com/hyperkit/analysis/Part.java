package com.hyperkit.analysis;

import java.awt.Component;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;

public abstract class Part
{
	
	private String title;
	private URL icon;
	private List<Action> actions = new LinkedList<>();
	private DefaultDockActionSource source;
	private DefaultDockable dockable;
	private Component component;
	
	public Part(String title)
	{
		this(title, Part.class.getClassLoader().getResource("icons/part.png"));
	}
	public Part(String title, URL icon)
	{
		this.title = title;
		this.icon = icon;
		
		Bus.getInstance().addPart(this);
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

			dockable =  new DefaultDockable(getComponent(), getTitle(), getIcon());	
			dockable.setActionOffers(source);
		}
		
		return dockable;
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
	
	public final boolean triggerEvent(Event event)
	{
		return Bus.getInstance().broadcastEvent(event);
	}
	public final boolean handleEvent(Event event)
	{
		boolean  result = true;
		
		Class<?> type = event.getClass();
		
		while (Event.class.isAssignableFrom(type) && ! type.isAssignableFrom(Event.class))
		{
			try
			{
				Method method = getClass().getMethod("handleEvent", type);
				
				result = result && (boolean) method.invoke(this, event);
				
				break;
			}
			catch (NoSuchMethodException e)
			{
				type = type.getSuperclass();
			}
			catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
		}
		
		return result;
	}

}
