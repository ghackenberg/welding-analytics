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
	
	private static int width;
	private static int height;
	private static int stroke;
	private static int font;
	
	static
	{
		try (FileInputStream input = new FileInputStream(analysisMemory))
		{
			JSONObject object = new JSONObject(new JSONTokener(input));
			
			currentDirectory = new File(object.getString("currentDirectory"));
			
			width = object.getInt("diagramWidth");
			height = object.getInt("diagramHeight");
			stroke = object.getInt("diagramStroke");
			font = object.getInt("diagramFont");
		}
		catch (Exception e)
		{
			currentDirectory = userHome;
			
			width = 640;
			height = 480;
			stroke = 2;
			font =  16;
		}
	}
	
	public static java.io.File getCurrentDirectory()
	{
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
		save();
	}
	
	public static int getDiagramWidth() 
	{
		return width;
	}
	public static void setDiagramWidth(int value)
	{
		width = value;
		
		save();
	}
	
	public static int getDiagramHeight() 
	{
		return height;
	}
	public static void setDiagramHeight(int value)
	{
		height = value;
		
		save();
	}
	
	public static int getDiagramStroke() 
	{
		return stroke;
	}
	public static void setDiagramStroke(int value)
	{
		stroke = value;
		
		save();
	}
	
	public static int getDiagramFont() 
	{
		return font;
	}
	public static void setDiagramFont(int value)
	{
		font = value;
		
		save();
	}
	
	private static void save()
	{
		try (FileWriter writer = new FileWriter(analysisMemory))
		{
			JSONObject object = new JSONObject();
			
			object.put("currentDirectory", currentDirectory.getAbsolutePath());
			
			object.put("diagramWidth", width);
			object.put("diagramHeight", height);
			object.put("diagramStroke", stroke);
			object.put("diagramFont", font);
			
			writer.write(object.toString());
		}
		catch (Exception e)
		{
			// ignore
		}
	}

}
