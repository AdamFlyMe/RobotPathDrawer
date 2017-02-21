package com.bbhsjellyfish.pathdrawer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PathDrawer extends JFrame {

	private static final long serialVersionUID = 8789458880987594340L;

	private static final NumberFormat number = new DecimalFormat("0");

	private String license;

	public static void main(final String[] args) {
		number.setMaximumFractionDigits(12);

		final PathDrawer w = new PathDrawer();
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private final ArrayList<Point> points = new ArrayList<>();

	private JMenuBar menuBar;
	private JMenu path;
	private JMenuItem newPath;
	private JMenuItem openPath;
	private JMenuItem savePath;
	private JMenuItem saveImage;
	private JMenu display;
	private JRadioButtonMenuItem displayEnglish;
	private JRadioButtonMenuItem displayCode;
	private JMenu help;
	private JMenuItem about;

	private JTextArea text;
	private JTable pointTable;

	private JLabel mouseInfo, pointInfo;

	private Editor editor;

	private JFileChooser fc;

	public PathDrawer() {
		try {
			license = new String(Files.readAllBytes(Paths.get("LICENSE")));
		} catch (final IOException e1) {
			final JEditorPane pane = new JEditorPane("text/html", "<html><body>Could not find the license. To run this program, please redownload the software from <a href=\"http://www.twitter.com/FTCJellyfish\">Team 4654's Twitter page</a>.</body></html>");
			pane.setEditable(false);
			pane.setBackground(new Color(238, 238, 238));
			pane.addHyperlinkListener(e -> {
				if (e.getEventType() != EventType.ACTIVATED) return;
				try {
					if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(e.getURL().toURI());
				} catch (final Exception exception) {
					exception.printStackTrace();
				}
			});
			JOptionPane.showMessageDialog(null, pane, "License not found", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		setTitle("FIRST Res-Q Path Drawer");

		this.setSize(640, 480);
		setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);

		try {
			setIconImage(ImageIO.read(new URL("http://2.bp.blogspot.com/-ESTdlwiIfeU/VfmJmvKHp_I/AAAAAAAAA6g/O2WgzRKM6cY/s1600/FIRST_ResQFNL.jpg")));
		} catch (final Exception e) {}

		final ActionListener menuListener = e -> {
			if (e.getSource() == newPath) {
				points.clear();
				if (pointTable.isEditing()) pointTable.getCellEditor().cancelCellEditing();
				repaint();
				changeText();
			} else if (e.getSource() == savePath) {
				fc.setFileFilter(new FileNameExtensionFilter("Path (*.path)", "path"));
				if (fc.showSaveDialog(PathDrawer.this) == JFileChooser.APPROVE_OPTION) {
					try {
						Files.write(fc.getSelectedFile().toPath(), writeString(points).getBytes());
					} catch (final Exception exception1) {}
				}
			} else if (e.getSource() == openPath) {
				fc.setFileFilter(new FileNameExtensionFilter("Path (*.path)", "path"));
				if (fc.showOpenDialog(PathDrawer.this) == JFileChooser.APPROVE_OPTION) {
					try {
						final ArrayList<Point> newPoints = readString(new String(Files.readAllBytes(fc.getSelectedFile().toPath())));
						points.clear();
						points.addAll(newPoints);
					} catch (final Exception exception2) {
						JOptionPane.showMessageDialog(PathDrawer.this, "<html>" + fc.getSelectedFile().getAbsolutePath() + "<br>Could not read this file.<br>This is not a valid graph file, or its format is not currently supported.</html>", "Error Reading File", JOptionPane.ERROR_MESSAGE);
					}
				}

				changeText();
				repaint();
			} else if (e.getSource() == saveImage) {
				fc.setFileFilter(new FileNameExtensionFilter("PNG Image (*.png)", "png"));
				if (fc.showSaveDialog(PathDrawer.this) == JFileChooser.APPROVE_OPTION) {
					final BufferedImage img = new BufferedImage(editor.getWidth(), editor.getHeight(), BufferedImage.TYPE_INT_ARGB);
					final Graphics2D g = img.createGraphics();

					editor.paint(g);

					g.dispose();

					try {
						ImageIO.write(img, "png", fc.getSelectedFile());
					} catch (final IOException exception3) {
						JOptionPane.showMessageDialog(PathDrawer.this, "Could not save file: " + exception3.getMessage());
					}
				}
			} else if (e.getSource() == displayCode || e.getSource() == displayEnglish) {
				changeText();
			} else if (e.getSource() == about) {
				final JTextArea area = new JTextArea("FIRST Res-Q Path Drawer v. 1.0.0\nCreated for the 2015-2016 FTC Challenge for The Jellyfish (FTC Team 4654)'s autonomous op mode.\n\nFIRST and FTC are registered trademarks of FIRST, FIRST Res-Q, and the FIRST Res-Q Logo are service marks of FIRST. © 2016 FIRST. All rights reserved.\n\nThe FIRST Res-Q path drawer is available for use under " + license);
				area.setEditable(false);
				area.setOpaque(false);
				area.setWrapStyleWord(true);
				area.setLineWrap(true);
				final JOptionPane pane = new JOptionPane(new JScrollPane(area), JOptionPane.PLAIN_MESSAGE);
				final Dialog d = pane.createDialog(PathDrawer.this, "About");
				d.setSize(640, 480);
				d.setLocationRelativeTo(null);
				d.setVisible(true);
			}
		};

		fc = new JFileChooser(new File(System.getProperty("user.dir")));
		fc.setAcceptAllFileFilterUsed(false);

		menuBar = new JMenuBar();

		path = new JMenu("Path");
		path.setMnemonic(KeyEvent.VK_P);

		newPath = new JMenuItem("New");
		newPath.setMnemonic(KeyEvent.VK_N);
		newPath.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		newPath.addActionListener(menuListener);
		path.add(newPath);

		openPath = new JMenuItem("Open");
		openPath.setMnemonic(KeyEvent.VK_O);
		openPath.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		openPath.addActionListener(menuListener);
		path.add(openPath);

		savePath = new JMenuItem("Save");
		savePath.setMnemonic(KeyEvent.VK_S);
		savePath.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		savePath.addActionListener(menuListener);
		path.add(savePath);

		saveImage = new JMenuItem("Save As Image...");
		saveImage.setMnemonic(KeyEvent.VK_I);
		saveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		saveImage.addActionListener(menuListener);
		path.add(saveImage);

		display = new JMenu("Display...");
		display.setMnemonic(KeyEvent.VK_D);

		displayEnglish = new JRadioButtonMenuItem("English", true);
		displayEnglish.addActionListener(menuListener);
		displayEnglish.setMnemonic(KeyEvent.VK_E);
		display.add(displayEnglish);

		displayCode = new JRadioButtonMenuItem("Code");
		displayCode.addActionListener(menuListener);
		displayCode.setMnemonic(KeyEvent.VK_C);
		display.add(displayCode);

		final ButtonGroup group = new ButtonGroup();
		group.add(displayEnglish);
		group.add(displayCode);

		path.add(display);

		menuBar.add(path);

		help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);

		about = new JMenuItem("About");
		about.setMnemonic(KeyEvent.VK_A);
		about.addActionListener(menuListener);
		help.add(about);

		menuBar.add(help);

		menuBar.add(Box.createHorizontalGlue());

		pointInfo = new JLabel();
		menuBar.add(pointInfo);

		menuBar.add(Box.createHorizontalStrut(30));

		mouseInfo = new JLabel();
		menuBar.add(mouseInfo);

		this.add(menuBar, BorderLayout.PAGE_START);

		final JPanel panel = new JPanel(new GridLayout(2, 1));

		text = new JTextArea();
		text.setEditable(false);
		text.setMargin(new Insets(10, 10, 0, 10));
		panel.add(new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

		pointTable = new JTable(new DefaultTableModel() {

			private static final long serialVersionUID = 4401954669960434751L;

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public int getRowCount() {
				return points.size();
			}

			@Override
			public Object getValueAt(final int r, final int c) {
				return c == 2 ? points.get(r).reverse : 0.0125 * (c == 0 ? points.get(r).point.getX() : points.get(r).point.getY());
			}

			@Override
			public void setValueAt(final Object value, final int r, final int c) {
				if (c == 2) {
					points.get(r).reverse = (Boolean) value;
					changeText();
					repaint();
					return;
				}

				if (!(value instanceof String)) return;

				final String text = (String) value;
				if (text.isEmpty()) {
					points.remove(r);
					changeText();
					repaint();
					return;
				}

				try {
					final double d = Double.parseDouble(text) * 80;
					final Point2D point = points.get(r).point;
					if (c == 0) {
						point.setLocation(d, point.getY());
					} else {
						point.setLocation(point.getX(), d);
					}
				} catch (final NumberFormatException e) {}

				repaint();
				changeText();
			}

			@Override
			public Class<?> getColumnClass(final int c) {
				return c == 2 ? Boolean.class : Number.class;
			}

		});
		pointTable.getTableHeader().setReorderingAllowed(false);
		pointTable.getTableHeader().setResizingAllowed(false);
		pointTable.setPreferredScrollableViewportSize(new Dimension(10, 10));
		pointTable.setDefaultRenderer(Number.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 2473161287107241401L;

			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
				final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (!(c instanceof JLabel) || !(value instanceof Number)) return c;

				final JLabel tc = (JLabel) c;
				tc.setText(number.format(((Number) value).doubleValue()));
				return tc;
			}

		});
		final JTextField editorText = new JTextField();
		editorText.setBorder(BorderFactory.createEmptyBorder());
		pointTable.setDefaultEditor(Number.class, new DefaultCellEditor(editorText));
		pointTable.getColumnModel().getColumn(0).setHeaderValue("X");
		pointTable.getColumnModel().getColumn(1).setHeaderValue("Y");
		pointTable.getColumnModel().getColumn(2).setHeaderValue("Reverse");
		pointTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "del");
		pointTable.getActionMap().put("del", new AbstractAction() {

			private static final long serialVersionUID = -2168993453781014420L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				for (int i = pointTable.getSelectedRows().length - 1; i >= 0; i--) {
					final int r = pointTable.getSelectedRows()[i];

					points.remove(r);
				}
				pointTable.clearSelection();
				changeText();
				repaint();
			}
		});
		panel.add(new JScrollPane(pointTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

		this.add(panel, BorderLayout.LINE_START);

		final MouseAdapter ad = new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON2) return;

				final Point2D p = inv.transform(e.getPoint(), null);

				if (e.isControlDown()) p.setLocation(40 * Math.round(p.getX() / 40), 40 * Math.round(p.getY() / 40));

				final Point point = new Point(p, e.getButton() == MouseEvent.BUTTON3);
				if (points.size() == 0 || !point.equals(points.get(points.size() - 1))) points.add(point);
				editor.repaint();
				changeText();
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
				final Point2D p = inv.transform(e.getPoint(), null);
				mouseInfo.setText(String.format("x: %.7g, y: %.7g   ", p.getX() * .0125, p.getY() * .0125));
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				mouseInfo.setText("");
			}
		};

		editor = new Editor();
		editor.addMouseListener(ad);
		editor.addMouseMotionListener(ad);
		this.add(editor);

		changeText();

		setVisible(true);
	}

	private AffineTransform trans, inv;

	private class Editor extends JComponent {

		private static final long serialVersionUID = -4650960914485249215L;

		@Override
		protected void paintComponent(final Graphics gg) {
			if (!(gg instanceof Graphics2D)) return;

			final Graphics2D g = (Graphics2D) gg;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			final int d = Math.abs(getWidth() - getHeight()) / 2;
			final boolean dx = getWidth() > getHeight();
			final int dim = dx ? getHeight() : getWidth();

			final BufferedImage field = new BufferedImage(481, 481, BufferedImage.TYPE_INT_RGB);

			final Graphics2D f = field.createGraphics();

			f.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			f.setColor(Color.GRAY);

			f.fillRect(0, 0, 480, 480);

			f.setColor(Color.BLACK);
			f.setStroke(new BasicStroke(1.5F));

			for (int x = 0; x < 6; x++) {
				for (int y = 0; y < 6; y++) {
					f.drawRect(80 * x, 80 * y, 80, 80);// 80 * (x + 1), 80 * (y + 1));
				}
			}

			f.setStroke(new BasicStroke(9F));
			f.setColor(Color.BLUE);
			f.drawLine(6, 481, 481, 6);
			f.fill(new Polygon(new int[] {0, 0, 46, 103}, new int[] {0, 114, 160, 103}, 4));
			f.fill(new Polygon(new int[] {481, 481, 433, 376}, new int[] {481, 365, 319, 376}, 4));

			f.setColor(Color.RED);
			f.drawLine(0, 475, 475, 0);
			f.fill(new Polygon(new int[] {0, 114, 160, 103}, new int[] {0, 0, 46, 103}, 4));
			f.fill(new Polygon(new int[] {481, 365, 319, 376}, new int[] {481, 481, 433, 376}, 4));

			f.setStroke(new BasicStroke(6));
			f.draw(new Polygon(new int[] {127, 199, 237, 237}, new int[] {5, 77, 77, 5}, 4));
			f.draw(new Polygon(new int[] {245, 245, 317, 317}, new int[] {5, 77, 77, 5}, 4));

			f.setColor(Color.BLUE);
			f.draw(new Polygon(new int[] {476, 404, 404, 476}, new int[] {354, 282, 244, 244}, 4));
			f.draw(new Polygon(new int[] {476, 404, 404, 476}, new int[] {236, 236, 164, 164}, 4));

			f.setColor(Color.WHITE);
			f.setStroke(new BasicStroke(3));
			f.drawLine(280, 2, 280, 78);
			f.drawLine(477, 199, 401, 199);

			f.setStroke(new BasicStroke(8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			f.drawLine(160, 46, 46, 160);
			f.drawLine(319, 433, 433, 319);

			f.setStroke(new BasicStroke(4));
			f.setColor(Color.BLACK);
			f.drawLine(0, 114, 48, 162);
			f.drawLine(114, 0, 162, 48);
			f.drawLine(479, 365, 431, 317);
			f.drawLine(365, 479, 317, 431);

			f.setStroke(new BasicStroke(2));
			f.setColor(Color.DARK_GRAY);
			f.drawLine(0, 0, 103, 103);
			f.drawLine(481, 481, 376, 376);

			trans = new AffineTransform();
			trans.translate(dx ? d : 0, dx ? 0 : d);
			trans.scale(0.00145 * dim, 0.00145 * dim);
			trans.translate(100, 100);
			trans.rotate(-Math.PI / 4, 240, 240);

			try {
				inv = trans.createInverse();
			} catch (final NoninvertibleTransformException e) {}

			g.drawImage(field, trans, null);

			f.dispose();

			if (!points.isEmpty()) {
				Point2D p0 = trans.transform(points.get(0).point, null);
				g.setColor(points.get(0).reverse ? Color.GREEN : Color.YELLOW);
				g.fillRect((int) (p0.getX() - 1), (int) (p0.getY() - 1), 3, 3);
				for (int i = 1; i < points.size(); i++) {
					final Point2D p1 = trans.transform(points.get(i).point, null);
					g.setColor(points.get(i).reverse ? Color.GREEN : Color.YELLOW);
					g.fillRect((int) (p1.getX() - 1), (int) (p1.getY() - 1), 3, 3);
					g.drawLine((int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY());
					p0 = p1;
				}
			}
		}
	}

	protected void changeText() {
		String text = "", code = "";

		for (int i = 1; i < points.size(); i++) {
			text += String.format("Move %sward %.5g ft\n", points.get(i).reverse ? "back" : "for", 0.025 * points.get(i).point.distance(points.get(i - 1).point));
			code = String.format("instructions.push(createMoveInstruction(%.9f, %b));\n%s", 0.0125 * points.get(i).point.distance(points.get(i - 1).point), points.get(i).reverse, code);

			if (i != points.size() - 1) {
				double t1 = Math.atan2(points.get(i).point.getY() - points.get(i - 1).point.getY(), points.get(i).point.getX() - points.get(i - 1).point.getX());
				double t2 = Math.atan2(points.get(i + 1).point.getY() - points.get(i).point.getY(), points.get(i + 1).point.getX() - points.get(i).point.getX());

				if (points.get(i).reverse) {
					t1 += Math.PI;
					t1 %= Math.PI * 2;
				}

				if (points.get(i + 1).reverse) {
					t2 += Math.PI;
					t2 %= Math.PI * 2;
				}

				double t = t1 - t2;
				if (t <= -Math.PI) {
					t += Math.PI * 2;
				} else if (t > Math.PI) {
					t -= Math.PI * 2;
				}

				if (t != 0) {
					text += String.format("Rotate %.5g radians (%.5g\u00B0) %sclockwise\n", Math.abs(t), Math.abs(Math.toDegrees(t)), t < 0 ? "" : "counter");
					code = String.format("instructions.push(createRotateInstruction(%.9f));\n%s", t, code);
				}
			}
		}

		this.text.setText((displayEnglish.isSelected() ? text : code + "\t\t") + "\t\t\t\t\t");
		this.text.setFont(new Font(displayEnglish.isSelected() ? Font.SANS_SERIF : Font.MONOSPACED, Font.PLAIN, 11));

		pointInfo.setText(String.format("%d points", points.size()));

		pointTable.revalidate();
	}

	protected static class Point {

		public final Point2D point;
		public boolean reverse;

		public Point(final Point2D point, final boolean reverse) {
			this.point = point;
			this.reverse = reverse;
		}

		public Point(final float x, final float y, final boolean reverse) {
			this(new Point2D.Float(x, y), reverse);
		}

		public Point(final Point2D point) {
			this(point, false);
		}

		public Point(final float x, final float y) {
			this(new Point2D.Float(x, y));
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (o == null) return false;
			if (getClass() != o.getClass()) return false;

			final Point other = (Point) o;
			if (point != other.point) {
				if (point == null || other.point == null) return false;
				if (point.distance(other.point) > 8) return false;
			}

			return true;
		}

	}

	public static String writeString(final ArrayList<Point> points) {
		String ret = "[";

		for (final Point p : points)
			ret += String.format("{\"x\":%.9f,\"y\":%.9f,\"reverse\":%b},", p.point.getX(), p.point.getY(), p.reverse);

		return ret.substring(0, ret.length() - 1) + "]";
	}

	public static ArrayList<Point> readString(final String s) {
		ArrayList<?> json = parse(s);
		if (json.size() == 1) {
			if (!(json.get(0) instanceof ArrayList<?>)) throw new IllegalArgumentException();

			json = (ArrayList<?>) json.get(0);
		}

		final ArrayList<Point> ret = new ArrayList<>();

		for (final Object o : json) {
			if (!(o instanceof Map<?, ?>)) continue;

			final Map<?, ?> map = (Map<?, ?>) o;

			final float x = map.get("x") instanceof Number ? ((Number) map.get("x")).floatValue() : 0;
			final float y = map.get("y") instanceof Number ? ((Number) map.get("y")).floatValue() : 0;
			final boolean reverse = map.get("reverse") instanceof Boolean ? (Boolean) map.get("reverse") : false;

			ret.add(new Point(x, y, reverse));
		}

		return ret;
	}

	public static ArrayList<Object> parse(final String string) {
		final HashMap<String, Object> braceObjs = new HashMap<>();

		String s = string;

		int lastAddress = (int) (System.currentTimeMillis() % 32768);

		final Pattern quotes = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"");
		Matcher m = quotes.matcher(s);
		while (m.find()) {
			final String v = m.group(1);
			final String k = "@" + lastAddress++;

			braceObjs.put(k, v.replaceAll("\\\\(.)", "$1"));
			s = s.replace(m.group(), k);

			m = quotes.matcher(s);
		}

		s = s.replaceAll("\\s+", "");

		final Pattern braces = Pattern.compile("\\[[^\\[\\]\\{\\}]*\\]|\\{[^\\[\\]\\{\\}]*?\\}");

		m = braces.matcher(s);
		while (m.find()) {
			final String g = m.group();
			if (g.charAt(0) == '[') {
				final String[] terms = g.split("[\\[\\]\\,]");
				final ArrayList<Object> termList = new ArrayList<>(terms.length);
				for (final String term : terms) {
					if (term.isEmpty()) continue;

					if (term.startsWith("\"")) {
						termList.add(term.substring(1, term.length() - 1));
					} else if (term.startsWith("@")) {
						termList.add(braceObjs.get(term));
					} else if (term.equals("true")) {
						termList.add(true);
					} else if (term.equals("false")) {
						termList.add(false);
					} else {
						try {
							termList.add(Double.parseDouble(term));
						} catch (final NumberFormatException e) {
							termList.add(null);
						}
					}
				}

				final String k = "@" + lastAddress++;

				s = s.replace(g, k);
				if (s.equals(k)) return termList;
				braceObjs.put(k, termList);
			} else {
				final String[] terms = g.split("[\\{\\}\\,]");
				final Map<String, Object> termList = new HashMap<>();
				for (final String term : terms) {
					if (term.isEmpty()) continue;

					final String[] kvs = term.split(":");
					final String k = (kvs[0].startsWith("@") ? (String) braceObjs.get(kvs[0]) : kvs[0].replace("\"", "")).toLowerCase();
					if (kvs[1].startsWith("\"")) {
						termList.put(k, kvs[1].substring(1, kvs[1].length() - 1));
					} else if (kvs[1].startsWith("@")) {
						termList.put(k, braceObjs.get(kvs[1]));
					} else if (kvs[1].equals("true")) {
						termList.put(k, true);
					} else if (kvs[1].equals("false")) {
						termList.put(k, false);
					} else {
						try {
							termList.put(k, Double.parseDouble(kvs[1]));
						} catch (final NumberFormatException e) {
							termList.put(k, null);
						}
					}
				}

				final String k = "@" + lastAddress++;

				s = s.replace(g, k);
				if (s.equals(k)) {
					final ArrayList<Object> ret = new ArrayList<>(1);
					ret.add(termList);
					return ret;
				}
				braceObjs.put(k, termList);
			}

			m = braces.matcher(s);
		}

		return null;
	}
}
