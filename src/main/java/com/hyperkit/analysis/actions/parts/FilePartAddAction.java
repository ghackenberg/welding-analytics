package com.hyperkit.analysis.actions.parts;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.Memory;
import com.hyperkit.analysis.actions.PartAction;
import com.hyperkit.analysis.datasets.ASDDataset;
import com.hyperkit.analysis.datasets.HDFDataset;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.parts.FilePart;

import bibliothek.gui.Dockable;

public class FilePartAddAction extends PartAction<FilePart>
{
	
	public FilePartAddAction(FilePart part)
	{
		super(part, "Add file", "Add file", FilePartAddAction.class.getClassLoader().getResource("icons/actions/parts/file_add.png"));
	}
	
	@Override
	public void action(Dockable dockable)
	{
		JFileChooser chooser = new JFileChooser();
		
		chooser.setCurrentDirectory(Memory.getCurrentDirectory());
		
		chooser.setFileFilter(new FileNameExtensionFilter("Data file", "asd", "tdp"));
		
		int result = chooser.showOpenDialog(getPart().getComponent());
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			
			Memory.setCurrentDirectory(file);
			
			Enumeration<Dataset> file_existing = getPart().getModel().elements();
			
			while (file_existing.hasMoreElements())
			{
				if (file_existing.nextElement().getFile().equals(file))
				{
					JOptionPane.showMessageDialog(getPart().getComponent(), "File is loaded already!");
					
					return;
				}
			}
			
			Thread thread = new Thread(() -> {
				try
				{
					Dataset absFile = null;
					
					if (file.getName().endsWith(".asd"))
					{
						absFile = new ASDDataset(file);	
					}
					else if (file.getName().endsWith(".tpd"))
					{
						absFile = new HDFDataset(file);
					}
					
					if (absFile != null && absFile.getLengthMeasured() > 0)
					{
						Bus.getInstance().broadcastEvent(new FilePartAddEvent(getPart(), absFile));
					}
					else
					{
						JOptionPane.showMessageDialog(getPart().getComponent(), "File does not contain readable data!");
					}
				}
				catch (IOException exception)
				{
					JOptionPane.showMessageDialog(getPart().getComponent(), "File could not be loaded!");
				}
			});
			thread.start();
		}
	}

}
