package moctave.esmapper;

public class Galaxy implements EventModifiableObject {
	public static final String TYPE = "galaxy";
	public Galaxy(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			if (child.getName().equals("pos")) {
				position = Builder.asCoordinate(child, TYPE);
			} else if (child.getName().equals("sprite")) {
				sprite = Builder.asSprite(child, TYPE);
			}
		}
	}

	private String name;
	private RectCoordinate position = new RectCoordinate();
	
	private Sprite sprite;

	@Override
	public void applyModifiers(Node node) {
		for (Node child : node.getChildren()) {
			if (child.getName().equals("pos")) {
				position = Builder.asCoordinate(child, TYPE);
			} else if (child.getName().equals("sprite")) {
				sprite = Builder.asSprite(child, TYPE);
			}
		}
	}

	public String getName() {
		return name;
	}

	public RectCoordinate getPosition() {
		return position;
	}

	public Sprite getSprite() {
		return sprite;
	}
}
