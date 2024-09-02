package moctave.esmapper;

import java.util.ArrayList;
import java.util.List;

public class StarSystem {
	public StarSystem(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr("Unnamed system!", node);
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
				try {
					try {
						positionX = Double.parseDouble(args.get(0));
						positionY = Double.parseDouble(args.get(1));
					} catch (NumberFormatException e) {
						Logger.nodeErr("Non-numeric system position.", child);
					}
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete position node in system definition.",
						child);
				}
			} else if (child.getName().equals("government")) {
				try {
					government = args.get(0);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete government node in system definition.",
						child);
				}
			} else if (child.getName().equals("attributes")) {
				for (String arg : args) {
					addAttribute(arg);
				}
			} else if (child.getName().equals("music")) {
				try {
					music = args.get(0);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete music node in system definition.",
						child);
				}
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

	private double positionX;
	private double positionY;

	private String government = "";
	private List<String> attributes = new ArrayList<>();
	private String music;

	private List<StellarObject> objects = new ArrayList<>();
	private List<String> hyperlinks = new ArrayList<>();

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
			System.out.println("No named objects. Uninhabited!");
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
		List<StellarObject> uncheckedObjects = getObjects();
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

	public double getX() {
		return positionX;
	}

	public double getY() {
		return positionY;
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

	public void setPosition(double x, double y) {
		this.positionX = x;
		this.positionY = y;
	}

	public void setX(double x) {
		this.positionX = x;
	}

	public void setY(double y) {
		this.positionY = y;
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
}
