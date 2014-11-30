package com.hyperkit.analysis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Handler
{
	
	public final boolean handleEvent(Event event)
	{
		boolean result = true;
		
		Class<?> type = event.getClass();
		
		while (Event.class.isAssignableFrom(type) && ! type.isAssignableFrom(Event.class))
		{
			try
			{
				Method method = getClass().getMethod("handleEvent", type);
				
				result = result && (boolean) method.invoke(this, event);
				
				break;
			}
			catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
				
				result = false;
				
				break;
			}
			catch (NoSuchMethodException e)
			{
				type = type.getSuperclass();
			}
		}
		
		return result;
	}

}
