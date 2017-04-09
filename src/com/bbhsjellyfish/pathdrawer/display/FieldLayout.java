package com.bbhsjellyfish.pathdrawer.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public enum FieldLayout {

	EMPTY(-1, "Blank Field"),
	VORTEX(2016, "Velocity Vortex") {

		@Override
		public void paintField(final Graphics2D g) {
			super.paintField(g);

			g.setStroke(new BasicStroke(20.0F));
			g.setColor(Color.BLUE);
			g.fill(new Polygon(new int[] {0, 28, 1440, 1440}, new int[] {0, 0, 1412, 1440}, 4));
			g.setColor(Color.RED);
			g.fill(new Polygon(new int[] {0, 0, 1412, 1440}, new int[] {0, 28, 1440, 1440}, 4));

			g.setColor(Color.BLACK);
			g.fillRect(600, 600, 240, 240);
			g.fill(new Polygon(new int[] {1440, 1440, 1387, 1077, 1130}, new int[] {0, 310, 363, 53, 0}, 5));
			g.fill(new Polygon(new int[] {0, 310, 363, 53, 0}, new int[] {1440, 1440, 1387, 1077, 1130}, 5));
			g.setColor(Color.BLUE);
			g.fill(new Polygon(new int[] {600, 840, 840, 820, 820, 620}, new int[] {600, 600, 840, 820, 620, 620}, 6));
			g.fill(new Polygon(new int[] {53, 53, 120, 363}, new int[] {1077, 1320, 1387, 1387}, 4));
			g.setColor(Color.RED);
			g.fill(new Polygon(new int[] {600, 600, 840, 820, 620, 620}, new int[] {600, 840, 840, 820, 820, 620}, 6));
			g.fill(new Polygon(new int[] {1077, 1320, 1387, 1387}, new int[] {53, 53, 120, 363}, 4));

			g.setColor(Color.RED.darker());
			g.fillOval(696, 534, 210, 210);
			g.setColor(Color.BLUE.darker());
			g.fillOval(534, 696, 210, 210);

			g.setColor(Color.YELLOW);
			g.drawLine(826, 614, 614, 826);

			g.setStroke(new BasicStroke(10.0F));
			for (int i = 0; i < 7; i++) {
				final double theta = Math.PI / 4 + 2 * Math.PI / 7 * i;
				final int xOff = (int) Math.round(Math.cos(theta) * 103.5);
				final int yOff = (int) Math.round(Math.sin(theta) * 103.5);
				g.setColor(Color.RED);
				g.drawLine(826, 614, 826 - xOff, 614 + yOff);
				g.setColor(Color.BLUE);
				g.drawLine(614, 826, 614 + xOff, 826 - yOff);
			}
			g.drawOval(510, 722, 208, 208);
			g.setColor(Color.RED);
			g.drawOval(722, 510, 208, 208);

			g.setColor(Color.YELLOW);
			g.fillOval(811, 599, 30, 30);
			g.fillOval(599, 811, 30, 30);

			g.setStroke(new BasicStroke(10.0F));
			g.setColor(Color.WHITE);
			g.drawLine(5, 360, 235, 360);
			g.drawLine(360, 5, 360, 235);
			g.drawLine(5, 840, 235, 840);
			g.drawLine(840, 5, 840, 235);
		}

	};

	public final int year;
	public final String name;

	private FieldLayout(final int year, final String name) {
		this.year = year;
		this.name = name;
	}

	@Override
	public String toString() {
		return year < 0 ? name : String.format("%s (%d - %d)", name, year, year + 1);
	}

	/**
	 * Draw the field with the specified {@link Graphics2D}. Drawn at 10px = 1in, starting at
	 * {@code (0, 0)}, with the red alliance's starting location at the bottom. Use
	 * {@link Graphics2D#setTransform(java.awt.geom.AffineTransform)} to change the scale and other
	 * properties.
	 * @param g - The {@code Graphics2D} object to draw with.
	 */
	public void paintField(final Graphics2D g) {
		g.setClip(0, 0, 1440, 1440);

		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, 1440, 1440);

		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(5));
		for (int n = 0; n <= 1440; n += 240) {
			g.drawLine(n, 0, n, 1440);
			g.drawLine(0, n, 1440, n);
		}

		g.setClip(-1440, -1440, 2880, 2880);
	}

}
