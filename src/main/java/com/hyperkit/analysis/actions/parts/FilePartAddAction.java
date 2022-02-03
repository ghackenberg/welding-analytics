package com.hyperkit.analysis.actions.parts;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Memory;
import com.hyperkit.analysis.actions.PartAction;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.files.ASDFile;
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
		
		chooser.setFileFilter(new FileNameExtensionFilter("ASD file", "asd"));
		
		int result = chooser.showOpenDialog(getPart().getComponent());
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			
			Memory.setCurrentDirectory(file);
			
			Enumeration<ASDFile> file_existing = getPart().getModel().elements();
			
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
					ASDFile asdFile = new ASDFile(file);
					
					if (asdFile.getLengthMeasured() > 0)
					{
						Bus.getInstance().broadcastEvent(new FilePartAddEvent(getPart(), asdFile));
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
