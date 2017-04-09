package com.bbhsjellyfish.pathdrawer.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

public class Field extends JComponent {

	private static final long serialVersionUID = 3857358925626724728L;

	@Override
	protected void paintComponent(final Graphics gg) {
		if (!(gg instanceof Graphics2D)) return;
		final Graphics2D g = (Graphics2D) gg;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final double scale = Math.min(getWidth(), getHeight()) / 2250.0;

		final AffineTransform trans = AffineTransform.getTranslateInstance(getWidth() * 0.5, getHeight() * 0.5);
		trans.scale(scale, scale);
		trans.rotate(Math.PI / 4);
		trans.translate(-720, -720);
		g.setTransform(trans);

		FieldLayout.VORTEX.paintField(g);
	}

}
