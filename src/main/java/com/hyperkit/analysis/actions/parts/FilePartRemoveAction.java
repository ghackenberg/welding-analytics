package com.hyperkit.analysis.actions.parts;

import bibliothek.gui.Dockable;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.actions.PartAction;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.parts.FilePart;

public class FilePartRemoveAction extends PartAction<FilePart>
{

	public FilePartRemoveAction(FilePart part)
	{
		super(part, "Remove file", "Remove file", FilePartAddAction.class.getClassLoader().getResource("icons/actions/parts/file_remove.png"));
	}
	
	@Override
	public void action(Dockable dockable)
	{
		if (!getPart().getList().isSelectionEmpty())
		{
			Bus.getInstance().broadcastEvent(new FilePartRemoveEvent(getPart(), getPart().getList().getSelectedValue()));
		}
	}

}
