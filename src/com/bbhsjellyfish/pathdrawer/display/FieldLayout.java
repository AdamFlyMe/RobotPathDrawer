package com.bbhsjellyfish.pathdrawer.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public enum FieldLayout {

	RESQ(2015, "Res-Q") {

		@Override
		public void paintField(final Graphics2D g) {
			super.paintField(g);

			g.setStroke(new BasicStroke(20.0F));
			g.setColor(Color.BLUE);
			g.fill(new Polygon(new int[] {0, 28, 1440, 1440}, new int[] {0, 0, 1412, 1440}, 4));
			g.drawRect(495, 15, 210, 210);
			g.setColor(Color.RED);
			g.fill(new Polygon(new int[] {0, 0, 1412, 1440}, new int[] {0, 28, 1440, 1440}, 4));
			g.drawRect(15, 495, 210, 210);
		}

	},
	VORTEX(2016, "Velocity Vortex");

	public final int year;
	public final String name;

	private FieldLayout(final int year, final String name) {
		this.year = year;
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("%s (%d - %d)", name, year, year + 1);
	}

	/**
	 * Draw the field with the specified {@link Graphics2D}. Drawn at 10px = 1in, starting at
	 * {@code (0, 0)}, with the red alliance's starting location at the bottom. Use
	 * {@link Graphics2D#setTransform(java.awt.geom.AffineTransform)} to change the scale and other
	 * properties.
	 * @param g - The {@code Graphics2D} object to draw with.
	 */
	public void paintField(final Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, 1440, 1440);

		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(5));
		for (int n = 0; n <= 1440; n += 240) {
			g.drawLine(n, 0, n, 1440);
			g.drawLine(0, n, 1440, n);
		}
	}

}
