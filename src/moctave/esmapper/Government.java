package moctave.esmapper;

import java.awt.Color;
import java.util.List;

public class Government {
	public Government(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr("Unnamed government!", node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("color")) {
				color = Main.getColorFromArgs(args);
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
