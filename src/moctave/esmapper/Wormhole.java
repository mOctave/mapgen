package moctave.esmapper;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wormhole {
	public static final String TYPE = "wormhole";
	public Wormhole(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("display name")) {
				displayName = Builder.asString(child, TYPE);
			} else if (child.getName().equals("mappable")) {
				mappable = true;
			} else if (child.getName().equals("link")) {
				try {
					links.put(args.get(0), args.get(1));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, TYPE, child);
				}
			} else if (child.getName().equals("color")) {
				color = Builder.asColor(child, TYPE);
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
