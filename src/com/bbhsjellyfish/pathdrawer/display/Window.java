package com.bbhsjellyfish.pathdrawer.display;

import java.awt.Frame;

import javax.swing.JFrame;

public class Window extends JFrame {

	private static final long serialVersionUID = 3264076406613458362L;

	public Window() {
		this.setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(Frame.MAXIMIZED_BOTH);

		add(new Field());
	}

}
