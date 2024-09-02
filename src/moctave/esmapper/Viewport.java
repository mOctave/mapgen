package moctave.esmapper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Viewport extends DrawnItem {
	public static final List<String> VALID_COMPONENTS = 
		Arrays.asList(new String[]{"map", "image", "legend", "line", "oval", "rect", "text"});

	public Viewport(Node node) {
		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr("Unnamed viewport!", node);
		}

		offset = new Point(0, 0);

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("size")) {
				try {
					try {
						size = new Dimension(
							Integer.parseInt(args.get(0)),
							Integer.parseInt(args.get(1))
						);
					} catch (NumberFormatException e) {
						Logger.nodeErr("Non-numeric system position.", child);
					}
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete size node in viewport definition.",
						child);
				}
			} else if (child.getName().equals("file format")) {
				try {
					fileFormat = args.get(0);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete file format node in viewport definition.",
						child);
				}
			} else if (VALID_COMPONENTS.contains(child.getName())) {
				components.add(child);
			}
		}

		offset = new Point(0, 0);
		setupGraphics();
		fillCanvas(Color.BLACK);
	}

	private String fileFormat = "png";
	private List<Node> components = new ArrayList<>();

	@Override
	public String getType() {
		return "viewport";
	}

	// Viewports don't need any custom data to be loaded.
	@Override
	public void load() {}

	// Draw the viewport.
	@Override
	public void draw() {
		long startTime = System.nanoTime();
		Logger.notify("Drawing viewport %s...", this.name);

		for (Node component : components) {
			List<String> args = component.getArgs();

			if (component.getName().equals("map")) {
				System.out.println("Drawing map...");

				// Easter egg
				int flipMode = DrawnItem.FLIP_NONE;
				for (Node grand : components) {
					if (grand.getName().equals("australia")) {
						flipMode = DrawnItem.FLIP_VERTICAL;
					}
				}
				try {
					drawImage(
						Main.getMap(args.get(0)).getImage(),
						Double.parseDouble(args.get(1)),
						Double.parseDouble(args.get(2)),
						flipMode
					);
				} catch (Exception e) {
					Logger.nodeErr("Failed drawing map to viewport.", component);
				}
			} else if (component.getName().equals("image")) {
				System.out.println("Drawing image...");
				try {
					File f = Main.getSpriteByName(args.get(0));
					Image img = ImageIO.read(f);
					drawImage(
						img,
						Double.parseDouble(args.get(1)),
						Double.parseDouble(args.get(2))
					);
				} catch (Exception e) {
					Logger.nodeErr("Failed drawing image sprite to viewport.", component);
				}
			} else if (component.getName().equals("legend")) {
				System.out.println("Drawing legend...");
				try {
					Legend legend = Main.getLegend(args.get(0));
					if (legend.getAlignment() == Legend.ALIGN_LEFT) {
						drawImage(
							legend.getImage(),
							0,
							Double.parseDouble(args.get(1))
						);
					} else {
						drawImage(
							legend.getImage(),
							size.getWidth() - 200,
							Double.parseDouble(args.get(1))
						);
					}
				} catch (Exception e) {
					Logger.nodeErr("Failed drawing legend to viewport.", component);
				}
			} else {
				Color chosenColor = Color.WHITE;
				for (Node child : component.getChildren()) {
					if (child.getName().equals("color")) {
						chosenColor = Main.getColorFromArgs(child.getArgs());
					}
				}

				if (component.getName().equals("line")) {
				System.out.println("Drawing line...");
				try {
					drawLine(
							Double.parseDouble(args.get(0)),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							Double.parseDouble(args.get(3)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr("Failed drawing line to viewport.", component);
					}
				} else if (component.getName().equals("oval")) {
					System.out.println("Drawing oval...");
					try {
						drawOval(
							Double.parseDouble(args.get(0)),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							Double.parseDouble(args.get(3)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr("Failed drawing oval to viewport.", component);
					}
				} else if (component.getName().equals("rect")) {
					System.out.println("Drawing rectangle...");
					try {
						drawRect(
							Double.parseDouble(args.get(0)),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							Double.parseDouble(args.get(3)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr("Failed drawing rectangle to viewport.", component);
					}
				} else if (component.getName().equals("text")) {
					System.out.println("Drawing text...");
					try {
						drawString(
							args.get(0),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr("Failed drawing text to viewport.", component);
					}
				}
			}
		}

		Logger.confirm(
			"Finished drawing viewport %s in %.3f seconds.",
			this.name,
			Logger.getTimeSince(startTime)
		);
	}

	// Getters and setters
	public String getFileFormat() {
		return fileFormat;
	}

	public List<Node> getComponents() {
		return components;
	}
}
