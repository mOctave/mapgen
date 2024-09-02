package moctave.esmapper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalacticMap extends DrawnItem {

	public GalacticMap(Node node) {
		Point center = new Point(0, 0);

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr("Unnamed map!", node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("size")) {
				try {
					try {
						size = new Dimension(
							Integer.parseInt(args.get(0)),
							Integer.parseInt(args.get(1))
						);
					} catch (NumberFormatException e) {
						Logger.nodeErr("Non-numeric map size.", child);
					}
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete size node in map definition.",
						child);
				}
			} else if (child.getName().equals("center")) {
				try {
					try {
						center = new Point(
							Integer.parseInt(args.get(0)),
							Integer.parseInt(args.get(1))
						);
					} catch (NumberFormatException e) {
						Logger.nodeErr("Non-numeric map center.", child);
					}
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete center node in map definition.",
						child);
				}
			} else if (child.getName().equals("paint")) {
				try {
					paintMode = args.get(0);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete paint node in map definition.",
						child);
				}
			} else if (child.getName().equals("event")) {
				try {
					events.add(args.get(0));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete event node in map definition.",
						child);
				}
			} else if (child.getName().equals("event list")) {
				try {
					for (String event : Main.getEventList(args.get(0))) {
						events.add(event);
					}
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete event list node in map definition.",
						child);
				}
			} else if (child.getName().equals("plugins only")) {
				pluginsOnly = true;
			} else if (child.getName().equals("include hidden")) {
				includeHidden = true;
			} else if (child.getName().equals("include unmappable wormholes")) {
				includeUnmappableWormholes = true;
			} else if (child.getName().equals("paint uninhabited")) {
				paintUninhabited = true;
			}
		}

		offset = new Point(
			(int) (size.getWidth() / 2 - center.getX()),
			(int) (size.getHeight() / 2 - center.getY())
		);

		setupGraphics();
		fillCanvas(Color.BLACK);
	}

	private boolean pluginsOnly = false;
	private String paintMode = "government";
	private boolean includeHidden = false;
	private boolean includeUnmappableWormholes = false;
	private boolean paintUninhabited = false;

	private List<String> events = new ArrayList<>();

	// Defined objects for this map.
	private Map<String, StarSystem> systems = new HashMap<>();
	private Map<String, Galaxy> galaxies = new HashMap<>();
	private Map<String, Government> governments = new HashMap<>();
	private Map<String, Wormhole> wormholes = new HashMap<>();
	private Map<String, Planet> planets = new HashMap<>();

	@Override
	public String getType() {
		return "map";
	}

	// Load the map data
	@Override
	public void load() {
		long startTime = System.nanoTime();
		Logger.notify("Setting up map-specific data...");

		for (Node node : Main.getNodes()) {
			if (!node.fromPlugin() && pluginsOnly)
				continue;

			if (node.getName().equals("system")) {
				addStarSystem(new StarSystem(node));
			} else if (node.getName().equals("galaxy")) {
				addGalaxy(new Galaxy(node));
			} else if (node.getName().equals("government")) {
				addGovernment(new Government(node));
			} else if (node.getName().equals("wormhole")) {
				addWormhole(new Wormhole(node));
			} else if (node.getName().equals("planet")) {
				addPlanet(new Planet(node));
			}
		}

		System.out.printf("Total governments: %d.%n", governments.size());
		System.out.printf("Total galaxies: %d.%n", galaxies.size());
		System.out.printf("Total systems: %d.%n", systems.size());
		System.out.printf("Total wormholes: %d.%n", wormholes.size());
		System.out.printf("Total planets: %d.%n", planets.size());

		Logger.confirm(
			"Finished loading data for map %s in %.3f seconds.",
			this.name,
			Logger.getTimeSince(startTime)
		);
	}

	// Draw the map
	@Override
	public void draw() {
		long startTime = System.nanoTime();
		Logger.notify("Drawing map %s...", this.name);

		for (Galaxy galaxy : galaxies.values()) {
			drawSprite(
				galaxy.getSprite(),
				galaxy.getX(),
				galaxy.getY(),
				true
			);
		}

		for (StarSystem system : systems.values()) {
			if (!system.isHidden() || includeHidden)
				drawSystem(system);
		}

		for (Wormhole wormhole : wormholes.values()) {
			if (wormhole.isMappable() || includeUnmappableWormholes)
				drawWormhole(wormhole);
		}

		Logger.confirm(
			"Finished drawing map %s in %.3f seconds.",
			this.name,
			Logger.getTimeSince(startTime)
		);
	}



	// Map-specific drawing methods
	public void drawSystem(StarSystem system) {
		drawString(
			system.getName(),
			system.getX() + 10,
			system.getY() + 6,
			Main.getColor("map name")
		);

		graphics.setStroke(new BasicStroke(3));
		drawOval(
			system.getX() - 5,
			system.getY() - 5,
			10,
			10,
			selectColor(system, paintMode)
		);

		for (String link : system.getHyperlinks()) {
			drawLink(system, getSystem(link));
		}
	}

	public void drawWormhole(Wormhole wormhole) {
		graphics.setStroke(new BasicStroke(1));
		Map<String, String> done = new HashMap<>();
		for (String key : wormhole.getLinks().keySet()) {
			StarSystem sys1 = getSystem(key);

			String val = wormhole.getLink(key);
			StarSystem sys2 = getSystem(val);

			double[] coords = offsetLineFromEndpoints(
				sys1.getX(),
				sys1.getY(),
				sys2.getX(),
				sys2.getY(),
				9.5
			);

			if (!includeHidden && (sys1.isHidden() || sys2.isHidden()))
				continue;

			// Hide the Eye and other wormholes that aren't attached to planets.
			if (!includeUnmappableWormholes) {
				boolean missingPlanet = true;

				for (StellarObject obj : sys1.getAllNamedObjects()) {
					Planet planet = getPlanet(obj.getName());

					if (
						planet.getWormhole() != null
						&& planet.getWormhole().equals(wormhole.getName())
					) {
						missingPlanet = false;
						break;
					}
				}


				if (missingPlanet) {
					continue;
				}
			}


			// Draw link
			if (!(done.containsKey(val) && done.get(val).equals(key))) {
				drawLine(
					coords[0],
					coords[1],
					coords[2],
					coords[3],
					wormhole.getDim()
				);

				done.put(key, val);
			}

			// Draw arrow
			double[] arrowheadCoords = offsetLineFromEndpoints(
				sys1.getX(),
				sys1.getY(),
				sys2.getX(),
				sys2.getY(),
				38
			);

			double arrowheadX = arrowheadCoords[0];
			double arrowheadY = arrowheadCoords[1];

			double angle = Math.atan2(
				arrowheadCoords[0] - coords[0],
				arrowheadCoords[1] - coords[1]
			);
			double length = 0.3 * (38 - 9.5);

			angle += 30 * 180 / Math.PI;
			double arrowLeftX = arrowheadCoords[0] + Math.sin(angle) * length;
			double arrowLeftY = arrowheadCoords[1] + Math.cos(angle) * length;

			angle -= 60 * 180 / Math.PI;
			double arrowRightX = arrowheadCoords[0] + Math.sin(angle) * length;
			double arrowRightY = arrowheadCoords[1] + Math.cos(angle) * length;

			drawLine(
				arrowheadX,
				arrowheadY,
				arrowLeftX,
				arrowLeftY,
				wormhole.getColor()
			);

			drawLine(
				arrowheadX,
				arrowheadY,
				arrowRightX,
				arrowRightY,
				wormhole.getColor()
			);

			drawLine(
				arrowheadX,
				arrowheadY,
				coords[0],
				coords[1],
				wormhole.getColor()
			);
		}
	}

	public void drawLink(StarSystem sys1, StarSystem sys2) {
		if (!includeHidden && (sys1.isHidden() || sys2.isHidden()))
			return;

		graphics.setStroke(new BasicStroke(1));
		double[] coords = offsetLineFromEndpoints(
			sys1.getX(),
			sys1.getY(),
			sys2.getX(),
			sys2.getY(),
			9.5
		);

		drawLine(
			coords[0],
			coords[1],
			coords[2],
			coords[3],
			Main.getColor("map link")
		);

	}


	public Color selectColor(StarSystem system, String paintMode) {
		if (!paintUninhabited() && system.isUninhabited(this))
			return getGovernment("Uninhabited").getColor();

		// Default is government paint
		return getGovernment(system.getGovernment()).getColor();
	}

	// Getters and setters
	public boolean pluginsOnly() {
		return pluginsOnly;
	}

	public String getPaintMode() {
		return paintMode;
	}

	public boolean includeHidden() {
		return includeHidden;
	}

	public boolean includeUnmappableWormholes() {
		return includeUnmappableWormholes;
	}

	public boolean paintUninhabited() {
		return paintUninhabited;
	}

	public List<String> getEvents() {
		return events;
	}



	public void addStarSystem(StarSystem system) {
		systems.put(system.getName(), system);
	}

	public void addGalaxy(Galaxy galaxy) {
		galaxies.put(galaxy.getName(), galaxy);
	}

	public void addGovernment(Government government) {
		governments.put(government.getName(), government);
	}

	public void addWormhole(Wormhole wormhole) {
		wormholes.put(wormhole.getName(), wormhole);
	}

	public void addPlanet(Planet planet) {
		planets.put(planet.getName(), planet);
	}

	public StarSystem getSystem(String key) {
		StarSystem s = systems.get(key);

		if (s == null) {
			Logger.warn("No system with name %s.", key);
		}

		return s;
	}

	public Galaxy getGalaxy(String key) {
		Galaxy g = galaxies.get(key);

		if (g == null) {
			Logger.warn("No galaxy with name %s.", key);
		}

		return g;
	}

	public Government getGovernment(String key) {
		Government g = governments.get(key);

		if (g == null) {
			Logger.warn("No government with name %s, using Uninhabited instead.", key);
			g = governments.get("Uninhabited");
		}

		return g;
	}

	public Wormhole getWormhole(String key) {
		Wormhole w = wormholes.get(key);

		if (w == null) {
			Logger.warn("No wormhole with name %s.", key);
		}

		return w;
	}

	public Planet getPlanet(String key) {
		Planet p = planets.get(key);

		if (p == null) {
			Logger.warn("No planet with name %s.", key);
		}

		return p;
	}
}
