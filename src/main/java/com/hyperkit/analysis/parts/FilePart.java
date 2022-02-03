package com.hyperkit.analysis.parts;

import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hyperkit.analysis.Bus;
import com.hyperkit.analysis.Dataset;
import com.hyperkit.analysis.Part;
import com.hyperkit.analysis.actions.parts.FilePartAddAction;
import com.hyperkit.analysis.actions.parts.FilePartRemoveAction;
import com.hyperkit.analysis.adapters.FileListCellRenderer;
import com.hyperkit.analysis.events.parts.FilePartAddEvent;
import com.hyperkit.analysis.events.parts.FilePartRemoveEvent;
import com.hyperkit.analysis.events.parts.FilePartSelectEvent;
import com.hyperkit.analysis.events.parts.PropertyPartChangeEvent;

public class FilePart extends Part
{
	
	private DefaultListModel<Dataset> model;
	private JList<Dataset> list;

	public FilePart()
	{
		super("Files", "icons/parts/file.png");
		
		FilePart self = this;
		
		addAction(new FilePartAddAction(this));
		addAction(new FilePartRemoveAction(this));
		
		model = new DefaultListModel<>();
		
		list = new JList<Dataset>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new FileListCellRenderer());
		list.addListSelectionListener(
			new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					Bus.getInstance().broadcastEvent(new FilePartSelectEvent(self, list.getSelectedValue()));
				}
			}
		);
	}

	@Override
	protected Component createComponent()
	{
		return new JScrollPane(list);
	}
	
	public DefaultListModel<Dataset> getModel()
	{
		return model;
	}
	public JList<Dataset> getList()
	{
		return list;
	}
	
	public boolean handleEvent(FilePartAddEvent event)
	{
		model.addElement(event.getASDFile());
		
		return true;
	}
	public boolean handleEvent(FilePartRemoveEvent event)
	{
		return model.removeElement(event.getASDFile());
	}
	public boolean handleEvent(PropertyPartChangeEvent event)
	{
		list.repaint();
		
		return true;
	}

}
