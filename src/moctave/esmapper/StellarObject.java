package moctave.esmapper;

import java.util.ArrayList;
import java.util.List;

public class StellarObject {
	public static final String TYPE = "stellar object";
	public StellarObject(Node node) {

		// Names are optional for stellar objects.
		if (node.getArgs().size() > 0)
			this.name = node.getArgs().get(0);

		for (Node child : node.getChildren()) {
			if (child.getName().equals("sprite")) {
				setSprite(Builder.asSprite(child, TYPE));
			} else if (child.getName().equals("distance")) {
				setDistance(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("period")) {
				setPeriod(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("offset")) {
				setOffset(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("object")) {
				addChild(new StellarObject(child));
			}
		}
	}

	private String name = null;

	private Sprite sprite = null;

	// Orbital data
	private double distance = 0;
	private double period = 0;
	private double offset = 0;

	// Hazards not implemented yet (if ever)

	private List<StellarObject> children = new ArrayList<>();

	public boolean isNamed() {
		if (name == null)
			return false;

		return true;
	}

	public boolean hasNamedChildren() {
		for (StellarObject child : children) {
			if (child.isNamed() || child.hasNamedChildren()) {
				return true;
			}
		}

		return false;
	}

	// Getters
	public String getName() {
		return name;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public double getDistance() {
		return distance;
	}

	public double getPeriod() {
		return period;
	}

	public double getOffset() {
		return offset;
	}

	public List<StellarObject> getChildren() {
		return children;
	}

	// Setters
	public void setName(String name) {
		this.name = name;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setPeriod(double period) {
		this.period = period;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public void addChild(StellarObject object) {
		children.add(object);
	}
}
