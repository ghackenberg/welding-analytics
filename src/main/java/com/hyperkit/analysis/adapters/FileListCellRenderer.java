package com.hyperkit.analysis.adapters;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.hyperkit.analysis.Dataset;

public class FileListCellRenderer extends JLabel implements ListCellRenderer<Dataset>
{
	
	private static final long serialVersionUID = 7571729831895799038L;

	@Override
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Dataset value, int index, boolean isSelected, boolean cellHasFocus)
	{
		setText(value.getName());
		setIcon(value.getIcon());
		
		if (isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);
		
		return this;
	}

}
