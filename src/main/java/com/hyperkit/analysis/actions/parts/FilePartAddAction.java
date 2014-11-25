package com.hyperkit.analysis.actions.parts;

import java.io.File;
import java.util.Enumeration;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import bibliothek.gui.Dockable;

import com.hyperkit.analysis.actions.PartAction;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.files.ASDFile;
import com.hyperkit.analysis.parts.FilePart;

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
		
		chooser.setFileFilter(new FileNameExtensionFilter("ASD file", "asd"));
		
		int result = chooser.showOpenDialog(getPart().getComponent());
		
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			
			Enumeration<ASDFile> file_existing = getPart().getModel().elements();
			
			while (file_existing.hasMoreElements())
			{
				if (file_existing.nextElement().getFile().equals(file))
				{
					return;
				}
			}
			
			ASDFile asdFile = new ASDFile(file);
			
			if (asdFile.getLength() > 0)
			{
				getPart().triggerEvent(new FilePartAddEvent(getPart(), new ASDFile(file)));
			}
			else
			{
				JOptionPane.showMessageDialog(getPart().getComponent(), "File could not be loaded!");
			}
		}
	}

}
