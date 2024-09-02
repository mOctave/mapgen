package moctave.esmapper;

import java.util.List;

public class Galaxy {
	public Galaxy(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr("Unnamed galaxy sprite!", node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("pos")) {
				try {
					try {
						positionX = Double.parseDouble(args.get(0));
						positionY = Double.parseDouble(args.get(1));
					} catch (NumberFormatException e) {
						Logger.nodeErr("Non-numeric system position.", child);
					}
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete position node in galaxy definition.",
						child);
				}
			} else if (child.getName().equals("sprite")) {
				try {
					sprite = args.get(0);

					for (Node grand : child.getChildren()) {
						if (grand.getName().equals("scale")) {
							try {
								scale = Double.parseDouble(grand.getArgs().get(0));
							} catch (NumberFormatException e) {
								Logger.nodeErr("Non-numeric image scale.", grand);
							}
						}
					}
					
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete sprite node in galaxy definition.",
						child);
				}
			}
		}
	}

	private String name;
	private double positionX = 0;
	private double positionY = 0;
	
	private String sprite;
	private double scale = 1;

	public String getName() {
		return name;
	}

	public double getX() {
		return positionX;
	}

	public double getY() {
		return positionY;
	}

	public String getSprite() {
		return sprite;
	}

	public double getScale() {
		return scale;
	}
}
