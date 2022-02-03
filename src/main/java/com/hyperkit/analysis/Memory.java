package com.hyperkit.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Memory
{

	private static java.io.File userHome = new java.io.File(System.getProperty("user.home"));
	
	private static java.io.File analysisMemory = new java.io.File(userHome, ".analysisrc");
	
	private static java.io.File currentDirectory;
	
	public static java.io.File getCurrentDirectory()
	{
		if (currentDirectory == null)
		{
			try (FileInputStream input = new FileInputStream(analysisMemory))
			{
				JSONObject object = new JSONObject(new JSONTokener(input));
				
				currentDirectory = new File(object.getString("currentDirectory"));
			}
			catch (Exception e)
			{
				currentDirectory = userHome;
			}
		}
		return currentDirectory;
	}
	
	public static void setCurrentDirectory(java.io.File file)
	{
		if (file.isDirectory())
		{
			currentDirectory = file;
		}
		else
		{
			currentDirectory = file.getParentFile();
		}
		
		try (FileWriter writer = new FileWriter(analysisMemory))
		{
			JSONObject object = new JSONObject();
			
			object.put("currentDirectory", file.getAbsolutePath());
			
			writer.write(object.toString());
		}
		
		catch (Exception e)
		{
			// ignore
		}
	}

}
