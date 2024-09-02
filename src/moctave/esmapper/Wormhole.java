package moctave.esmapper;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wormhole {
	public Wormhole(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr("Unnamed wormhole!", node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("display name")) {
				try {
					displayName = args.get(0);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete display name node in wormhole definition.",
						child);
				}
			} else if (child.getName().equals("mappable")) {
				mappable = true;
			} else if (child.getName().equals("link")) {
				try {
					links.put(args.get(0), args.get(1));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete link node in wormhole definition.",
						child);
				}
			} else if (child.getName().equals("color")) {
				if (args.size() >= 3) {
					try {
						color = new Color(
							Float.parseFloat(args.get(0)),
							Float.parseFloat(args.get(1)),
							Float.parseFloat(args.get(2))
						);
					} catch (NumberFormatException e) {
						Logger.nodeErr("Non-numeric wormhole color.", child);
					}
				} else {
					try {
						color = Main.getColor(args.get(0));
					} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete color node in wormhole definition.",
						child);
					}
				}
			}
		}
	}

	private String name;
	private String displayName = "???";

	private boolean mappable = false;
	private Map<String, String> links = new HashMap<>();
	private Color color = Main.getColor("map wormhole");

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isMappable() {
		return mappable;
	}

	public String getLink(String s) {
		return links.get(s);
	}

	public Map<String, String> getLinks() {
		return links;
	}

	public Color getColor() {
		return color;
	}

	public Color getDim() {
		return new Color(
			(float) color.getRed() / 255,
			(float) color.getGreen() / 255,
			(float) color.getBlue() / 255,
			(float) color.getAlpha() / 255 * 0.33f
		);
	}
}
