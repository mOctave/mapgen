package moctave.esmapper;

import java.util.ArrayList;
import java.util.List;

public class StarSystem implements EventModifiableObject {
	public static final String TYPE = "system";
	public StarSystem(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("inaccessible")) {
				inaccessible = true;
			} else if (child.getName().equals("hidden")) {
				hidden = true;
			} else if (child.getName().equals("shrouded")) {
				shrouded = true;
			} else if (child.getName().equals("pos")) {
				setPosition(Builder.asCoordinate(child, TYPE));
			} else if (child.getName().equals("government")) {
				setGovernment(Builder.asString(child, TYPE));
			} else if (child.getName().equals("attributes")) {
				for (String arg : args) {
					addAttribute(arg);
				}
			} else if (child.getName().equals("music")) {
				setMusic(Builder.asString(child, TYPE));
			} else if (child.getName().equals("object")) {
				addObject(new StellarObject(child));
			} else if (child.getName().equals("link")) {
				for (String arg : args) {
					addHyperlink(arg);
				}
			}
		}
	}

	private String name;

	private boolean inaccessible = false;
	private boolean hidden = false;
	private boolean shrouded = false;

	private RectCoordinate position;

	private String government = "";
	private List<String> attributes = new ArrayList<>();
	private String music;

	private List<StellarObject> objects = new ArrayList<>();
	private List<String> hyperlinks = new ArrayList<>();

	@Override
	public void applyModifiers(Node node) {
		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();
			boolean objectsModified = false;

			if (child.getName().equals("inaccessible")) {
				inaccessible = true && !(child.getFlag() == Node.REMOVE);
			} else if (child.getName().equals("hidden")) {
				hidden = true && !(child.getFlag() == Node.REMOVE);
			} else if (child.getName().equals("shrouded")) {
				shrouded = true && !(child.getFlag() == Node.REMOVE);
			} else if (child.getName().equals("pos")) {
				setPosition(Builder.asCoordinate(child, TYPE));
			} else if (child.getName().equals("government")) {
				setGovernment(Builder.asString(child, TYPE));
			} else if (child.getName().equals("attributes")) {
				attributes = Builder.modifyList(attributes, node, TYPE);
			} else if (child.getName().equals("music")) {
				setMusic(Builder.asString(child, TYPE));
			} else if (child.getName().equals("object")) {
				if (child.getFlag() == Node.ADD || objectsModified) {
					addObject(new StellarObject(child));
				} else {
					objects = new ArrayList<>();
					if (child.getFlag() != Node.REMOVE) {
						addObject(new StellarObject(child));
					}
				}

				objectsModified = true;
			} else if (child.getName().equals("link")) {
				if (child.getFlag() == Node.REMOVE) {
					if (args.size() > 0) {
						hyperlinks.remove(args.get(0));
					} else {
						hyperlinks = new ArrayList<>();
					}
				} else {
					for (String arg : args) {
						addHyperlink(arg);
					}
				}
			}
		}
	}

	public String toString() {
		String attributeSummary = "";

		if (inaccessible) attributeSummary += "inaccessible, ";
		if (hidden) attributeSummary += "hidden, ";
		if (shrouded) attributeSummary += "shrouded, ";

		for (String attribute : attributes)
			attributeSummary += attribute + ", ";

		if (attributeSummary.isEmpty()) attributeSummary = ", ";

		String str = String.format(
			"StarSystem{name: %s, position: %s, attributes: %sgovernment: %s, objects: %d}",
			name,
			position,
			attributeSummary,
			government,
			objects.size()
		);

		return str;
	}

	/**
	 * A system is marked as uninhabited when:
	 * (1) There are no named objects in the system, or
	 * (2) Every named object in the system has the "uninhabited" attribute.
	 * Being given the "Uninhabited" government does not cause a system to be
	 * marked as uninhabited.
	 * @param map The set of map data to check against.
	 * @return Whether or not this system is marked as uninhabited.
	 */
	public boolean isUninhabited(GalacticMap map) {
		List<StellarObject> namedObjects = getAllNamedObjects();
		if (namedObjects.size() == 0) {
			return true;
		}
		
		for (StellarObject obj : namedObjects) {
			Planet planet = map.getPlanet(obj.getName());

			if (!planet.getAttributes().contains("uninhabited")) {
				return false;
			}
		}

		return true;
	}

	public List<StellarObject> getAllNamedObjects() {
		List<StellarObject> uncheckedObjects = new ArrayList<>(getObjects());
		List<StellarObject> namedObjects = new ArrayList<>();
		
		while (uncheckedObjects.size() > 0) {
			StellarObject obj = uncheckedObjects.get(0);

			if (obj.isNamed()) {
				namedObjects.add(obj);
			}
	
			if (obj.hasNamedChildren()) {
				for (StellarObject child : obj.getChildren()) {
					uncheckedObjects.add(child);
				}
			}

			uncheckedObjects.remove(0);
		}

		return namedObjects;
	}

	// Accessors
	public String getName() {
		return name;
	}

	public boolean isInaccessible() {
		return inaccessible;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isShrouded() {
		return shrouded;
	}

	public RectCoordinate getPosition() {
		return position;
	}

	public double getX() {
		return position.getX();
	}

	public double getY() {
		return position.getY();
	}

	public String getGovernment() {
		return government;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public String getMusic() {
		return music;
	}

	public List<StellarObject> getObjects() {
		return objects;
	}

	public List<String> getHyperlinks() {
		return hyperlinks;
	}

	// Mutators

	// The name of a system cannot be changed: it has no mutators.

	public void setInaccessible(boolean inaccessible) {
		this.inaccessible = inaccessible;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setShrouded(boolean shrouded) {
		this.shrouded = shrouded;
	}

	public void setPosition(RectCoordinate position) {
		this.position = position;
	}

	public void setGovernment(String government) {
		this.government = government;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(String attribute) {
		this.attributes.add(attribute);
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public void addObject(StellarObject object) {
		objects.add(object);
	}

	public void addHyperlink(String hyperlink) {
		hyperlinks.add(hyperlink);
	}

	public void removeHyperlink(String hyperlink) {
		hyperlinks.remove(hyperlink);
	}
}
