package moctave.esmapper;

import java.awt.Color;

public class Government {
	public static final String TYPE = "government";
	public Government(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {

			if (child.getName().equals("color")) {
				color = Builder.asColor(child, TYPE);
			}
		}
	}

	private String name;

	private Color color;

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}
}
