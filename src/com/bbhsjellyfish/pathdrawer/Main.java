package com.bbhsjellyfish.pathdrawer;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bbhsjellyfish.pathdrawer.display.Window;

public class Main {

	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}

		final Window w = new Window();
		w.setVisible(true);
	}

}
