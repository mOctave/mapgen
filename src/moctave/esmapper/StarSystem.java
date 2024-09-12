// Copyright (c) 2024 by mOctave
//
// This program is free software: you can redistribute it and/or modify it under the
// terms of the GNU Affero General Public License as published by the Free Software
// Foundation, either version 3 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
// PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License along with
// this program. If not, see <https://www.gnu.org/licenses/>.

package moctave.esmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			} else if (child.getName().equals("habitable")) {
				setHabitableDistance(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("invisible fence")) {
				setInvisibleFenceDistance(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("jump range")) {
				setJumpRange(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("starfield density")) {
				setStarfieldDensity(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("arrival")) {
				if (child.getChildren().size() > 0) {
					for (Node grand : child.getChildren()) {
						if (grand.getName().equals("link")) {
							setArrivalDistanceHyper(Builder.asDouble(grand, TYPE));
						} else if (grand.getName().equals("jump")) {
							setArrivalDistanceJump(Builder.asDouble(grand, TYPE));
						}
					}
				} else {
					setArrivalDistanceHyper(Builder.asDouble(child, TYPE));
					setArrivalDistanceJump(Builder.asDouble(child, TYPE));
				}
			} else if (child.getName().equals("departure")) {
				if (child.getChildren().size() > 0) {
					for (Node grand : child.getChildren()) {
						if (grand.getName().equals("link")) {
							setDepartureDistanceHyper(Builder.asDouble(grand, TYPE));
						} else if (grand.getName().equals("jump")) {
							setDepartureDistanceJump(Builder.asDouble(grand, TYPE));
						}
					}
				} else {
					setDepartureDistanceHyper(Builder.asDouble(child, TYPE));
					setDepartureDistanceJump(Builder.asDouble(child, TYPE));
				}
			} else if (child.getName().equals("trade")) {
				Builder.mapDouble(child, commodities, TYPE);
			} else if (child.getName().equals("hazard")) {
				Builder.mapDouble(child, hazards, TYPE);
			} else if (child.getName().equals("asteroid")) {
				Builder.mapDoubleArray(child, asteroids, TYPE);
			} else if (child.getName().equals("minable")) {
				Builder.mapDoubleArray(child, minables, TYPE);
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

	private double arrivalDistanceHyper = 0;
	private double arrivalDistanceJump = 0;
	private double departureDistanceHyper = 0;
	private double departureDistanceJump = 0;

	private double habitableDistance = 0;

	//private List<Node> belts = new ArrayList<>();

	//ramscoop stuff

	private double invisibleFenceDistance = 10000;

	private double jumpRange = 0;
	//private List<Sprite> hazes = new ArrayList<>();

	private List<String> hyperlinks = new ArrayList<>();

	private Map<String, Double[]> asteroids = new HashMap<>();
	private Map<String, Double[]> minables = new HashMap<>();

	private Map<String, Double> commodities = new HashMap<>();

	//private Map<String, Double> roamingFleets = new HashMap<>();
	//private Map<String, Double[]> raidingFleets = new HashMap<>();

	private Map<String, Double> hazards = new HashMap<>();
	private double starfieldDensity = 1;

	private List<StellarObject> objects = new ArrayList<>();

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
			} else if (child.getName().equals("habitable")) {
				setHabitableDistance(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("invisible fence")) {
				setInvisibleFenceDistance(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("jump range")) {
				setJumpRange(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("starfield density")) {
				setStarfieldDensity(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("trade")) {
				Builder.mapDouble(child, commodities, TYPE);
			} else if (child.getName().equals("hazard")) {
				Builder.mapDouble(child, hazards, TYPE);
			} else if (child.getName().equals("asteroid")) {
				Builder.mapDoubleArray(child, asteroids, TYPE);
			} else if (child.getName().equals("minable")) {
				Builder.mapDoubleArray(child, minables, TYPE);
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
	 * (2) Every named object in the system either has the "uninhabited"
	 * attribute, or is a wormhole.
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

			if (
				!planet.getAttributes().contains("uninhabited")
				&& planet.getWormhole() == null
			) {
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

	public double getArrivalDistanceHyper() {
		return arrivalDistanceHyper;
	}

	public double getArrivalDistanceJump() {
		return arrivalDistanceJump;
	}

	public double getDepartureDistanceHyper() {
		return departureDistanceHyper;
	}

	public double getDepartureDistanceJump() {
		return departureDistanceJump;
	}

	public double getHabitableDistance() {
		return habitableDistance;
	}

	public double getInvisibleFenceDistance() {
		return invisibleFenceDistance;
	}

	public double getJumpRange() {
		return jumpRange;
	}

	public List<String> getHyperlinks() {
		return hyperlinks;
	}

	public double getStarfieldDensity() {
		return starfieldDensity;
	}

	public List<StellarObject> getObjects() {
		return objects;
	}

	public double getCommodityPrice(String commodity) {
		try {
			return commodities.get(commodity);
		} catch (NullPointerException e) {
			Logger.warn("No price for commodity %s in system %s.", commodity, name);
			return 0;
		}
	}

	public Double[] getAsteroidData(String asteroid) {
		return asteroids.get(asteroid);
	}

	public double getAsteroidCount(String asteroid) {
		Double[] arr = asteroids.get(asteroid);
		if (arr == null)
			return 0;
		
		return arr[0];
	}

	public double getAsteroidEnergy(String asteroid) {
		Double[] arr = asteroids.get(asteroid);
		if (arr == null)
			return 0;
		
		return arr[1];
	}

	public Double[] getMinableData(String minable) {
		return minables.get(minable);
	}

	public double getMinableCount(String minable) {
		Double[] arr = minables.get(minable);
		if (arr == null)
			return 0;
		
		return arr[0];
	}

	public double getMinableEnergy(String minable) {
		Double[] arr = minables.get(minable);
		if (arr == null)
			return 0;
		
		return arr[1];
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

	public void setArrivalDistanceHyper(double arrivalDistanceHyper) {
		this.arrivalDistanceHyper = arrivalDistanceHyper;
	}

	public void setArrivalDistanceJump(double arrivalDistanceJump) {
		this.arrivalDistanceJump = arrivalDistanceJump;
	}

	public void setDepartureDistanceHyper(double departureDistanceHyper) {
		this.departureDistanceHyper = departureDistanceHyper;
	}

	public void setDepartureDistanceJump(double departureDistanceJump) {
		this.departureDistanceJump = departureDistanceJump;
	}

	public void setHabitableDistance(double habitableDistance) {
		this.habitableDistance = habitableDistance;
	}

	public void setInvisibleFenceDistance(double invisibleFenceDistance) {
		this.invisibleFenceDistance = invisibleFenceDistance;
	}

	public void setJumpRange(double jumpRange) {
		this.jumpRange = jumpRange;
	}

	public void addHyperlink(String hyperlink) {
		hyperlinks.add(hyperlink);
	}

	public void removeHyperlink(String hyperlink) {
		hyperlinks.remove(hyperlink);
	}

	public void setStarfieldDensity(double starfieldDensity) {
		this.starfieldDensity = starfieldDensity;
	}

	public void addObject(StellarObject object) {
		objects.add(object);
	}
}
