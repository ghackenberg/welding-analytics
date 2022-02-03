package com.hyperkit.analysis.helpers;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageHelper {

	public static ImageIcon getImageIcon(String path, int size) {
		try {
			URL url = ImageHelper.class.getClassLoader().getResource(path);
			Image image = ImageIO.read(url).getScaledInstance(size, size, Image.SCALE_SMOOTH);
			return new ImageIcon(image);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ImageIcon getImageIcon(String path) {
		return getImageIcon(path, 16);
	}
	
}
