package moctave.esmapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Planet {
	public static final String TYPE = "planet";
	public Planet(Node node) {
		// Names are optional for stellar objects.
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			String name = child.getName();
			List<String> args = child.getArgs();

			if (name.equals("attributes")) {
				for (String attribute : args) {
					addAttribute(attribute);
				}
			} else if (name.equals("landscape")) {
				setLandscape(Builder.asSprite(child, TYPE));
			} else if (name.equals("music")) {
				setMusic(Builder.asString(child, TYPE));
			} else if (name.equals("description")) {
				setDescription(getDescription() + Builder.asString(child, TYPE) + "\n");
			} else if (name.equals("spaceport")) {
				setSpaceportDescription(getSpaceportDescription() + Builder.asString(child, TYPE) + "\n");
			} else if (name.equals("port")) {
				setPortNode(child);
			} else if (name.equals("government")) {
				setGovernment(Builder.asString(child, TYPE));
			} else if (name.equals("shipyard")) {
				addShipyard(Builder.asString(child, TYPE));
			} else if (name.equals("outfitter")) {
				addOutfitter(Builder.asString(child, TYPE));
			} else if (child.getName().equals("required reputation")) {
				setRequiredReputation(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("bribe")) {
				setBribe(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("security")) {
				setSecurity(Builder.asDouble(child, TYPE));
			} else if (child.getName().equals("wormhole")) {
				setWormhole(Builder.asString(child, TYPE));
			} else if (child.getName().equals("tribute")) {
				try {
					try {
						setTributeValue(Integer.parseInt(args.get(0)));
					} catch (NumberFormatException e) {
						Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_INT, TYPE, child);
					}
					for (Node grand : child.getChildren()) {
						if (grand.getName().equals("threshold")) {
							try {
								setTributeThreshold(Integer.parseInt(grand.getArgs().get(0)));
							} catch (NumberFormatException e) {
								Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_INT, TYPE, grand);
							}
						} else if (grand.getName().equals("fleet")) {
							try {
								addTributeFleet(
									grand.getArgs().get(0),
									Integer.parseInt(grand.getArgs().get(1))
								);
							} catch (Exception e) {
								Logger.nodeErr(Logger.ERROR_OBJECT_CREATION, TYPE, grand);
							}
						}
					}
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, TYPE, child);
				}
			}
		}
	}

	private String name = null;

	private List<String> attributes = new ArrayList<>();
	private Sprite landscape = null;
	private String music = null;

	private String description = "\t";
	private String spaceportDescription = "\t";
	private Node port = null;

	private String government = null;

	private List<String> shipyards = new ArrayList<>();
	private List<String> outfitters = new ArrayList<>();

	private double requiredReputation = 0;
	private double bribe = 0.01;
	private double security = 0.25;

	private String wormhole = null;

	private int tributeValue = 0;
	private int tributeThreshold = 4000;
	private Map<String, Integer> tributeFleets = new HashMap<>();

	// Getters
	public String getName() {
		return name;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	public Sprite getLandscape() {
		return landscape;
	}

	public String getMusic() {
		return music;
	}

	public String getDescription() {
		return description;
	}

	public String getSpaceportDescription() {
		return spaceportDescription;
	}

	public Node getPortNode() {
		return port;
	}

	public String getGovernment() {
		return government;
	}

	public List<String> getShipyards() {
		return shipyards;
	}

	public List<String> getOutfitters() {
		return outfitters;
	}

	public double getRequiredReputation() {
		return requiredReputation;
	}

	public double getBribe() {
		return bribe;
	}

	public double getSecurity() {
		return security;
	}

	public String getWormhole() {
		return wormhole;
	}

	public int getTributeValue() {
		return tributeValue;
	}

	public int getTributeThreshold() {
		return tributeThreshold;
	}

	public Map<String, Integer> getTributeFleets() {
		return tributeFleets;
	}

	// Setters
	public void setName(String name) {
		this.name = name;
	}

	public void addAttribute(String attribute) {
		attributes.add(attribute);
	}

	public void setLandscape(Sprite landscape) {
		this.landscape = landscape;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSpaceportDescription(String spaceport) {
		spaceportDescription = spaceport;
	}

	public void setPortNode(Node port) {
		this.port = port;
	}

	public void setGovernment(String government) {
		this.government = government;
	}

	public void addShipyard(String shipyard) {
		shipyards.add(shipyard);
	}

	public void addOutfitter(String outfitter) {
		outfitters.add(outfitter);
	}

	public void setRequiredReputation(double requiredReputation) {
		this.requiredReputation = requiredReputation;
	}

	public void setBribe(double bribe) {
		this.bribe = bribe;
	}

	public void setSecurity(double security) {
		this.security = security;
	}

	public void setWormhole(String wormhole) {
		this.wormhole = wormhole;
	}

	public void setTributeValue(int tributeValue) {
		this.tributeValue = tributeValue;
	}

	public void setTributeThreshold(int tributeThreshold) {
		this.tributeThreshold = tributeThreshold;
	}

	public void addTributeFleet(String fleet, int count) {
		if (tributeFleets.containsKey(fleet)) {
			tributeFleets.put(fleet, tributeFleets.get(fleet) + count);
		} else {
			tributeFleets.put(fleet, count);
		}
	}

}