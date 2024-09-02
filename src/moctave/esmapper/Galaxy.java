package moctave.esmapper;

import java.util.List;

public class Galaxy {
	public static final String TYPE = "galaxy";
	public Galaxy(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("pos")) {
				try {
					positionX = Double.parseDouble(args.get(0));
					positionY = Double.parseDouble(args.get(1));
				} catch (NumberFormatException e) {
					Logger.nodeErr(Logger.NUMBER_FORMAT_DOUBLE, TYPE, child);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr(Logger.INCOMPLETE_NODE, TYPE, child);
				}
			} else if (child.getName().equals("sprite")) {
				try {
					sprite = new Sprite(child);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr(Logger.INCOMPLETE_NODE, TYPE, child);
				}
			}
		}
	}

	private String name;
	private double positionX = 0;
	private double positionY = 0;
	
	private Sprite sprite;

	public String getName() {
		return name;
	}

	public double getX() {
		return positionX;
	}

	public double getY() {
		return positionY;
	}

	public Sprite getSprite() {
		return sprite;
	}
}
