package com.hyperkit.analysis.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.hyperkit.analysis.files.ASDFile;

public class FileListCellRenderer extends JLabel implements ListCellRenderer<ASDFile>
{
	
	private static final long serialVersionUID = 7571729831895799038L;

	@Override
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, ASDFile value, int index, boolean isSelected, boolean cellHasFocus)
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
