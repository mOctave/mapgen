package moctave.esmapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
	public static final String TYPE = "event";
	public static final List<String> ALLOWED_MODIFICATIONS = Arrays.asList(new String[] {
		"galaxy", "government", "fleet", "planet", "news",
		"shipyard", "system", "outfitter"
	});

	public Event(Node node) {
		
		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("link")) {
				try {
					addLink(args.get(0), args.get(1));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, TYPE, child);
				}
			} else if (child.getName().equals("unlink")) {
				try {
					addUnlink(args.get(0), args.get(1));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, TYPE, child);
				}
			} else if (ALLOWED_MODIFICATIONS.contains(child.getName())) {
				modifierNodes.add(child);
			}
		}
	}
	
	private String name;

	// Date and visitation info excluded

	// Links to modify
	private Map<String, String> linkMap = new HashMap<>();
	private Map<String, String> unlinkMap = new HashMap<>();

	// Objects to modify
	private List<Node> modifierNodes = new ArrayList<>();

	// Substitutions and conditions excluded

	public void apply(GalacticMap map) {
		long startTime = System.nanoTime();
		Logger.notify("Applying event %s...", name);

		for (Node node : modifierNodes) {
			String label;
			try {
				label = node.getArgs().get(0);
			} catch (IndexOutOfBoundsException e) {
				Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, TYPE, node);
				continue;
			}

			if (node.getName().equals("galaxy")) {
				map.getGalaxy(label).applyModifiers(node);
			} else if (node.getName().equals("government")) {
				map.getGovernment(label).applyModifiers(node);
			} else if (node.getName().equals("planet")) {
				map.getPlanet(label).applyModifiers(node);
			} else if (node.getName().equals("system")) {
				map.getSystem(label).applyModifiers(node);
			} else if (node.getName().equals("wormhole")) {
				map.getWormhole(label).applyModifiers(node);
			}
		}

		// Links and unlinks are applied last
		for (String link : linkMap.keySet()) {
			map.getSystem(link).addHyperlink(linkMap.get(link));
			map.getSystem(linkMap.get(link)).addHyperlink(link);
		}

		for (String link : unlinkMap.keySet()) {
			map.getSystem(link).removeHyperlink(linkMap.get(link));
			map.getSystem(linkMap.get(link)).removeHyperlink(link);
		}

		Logger.confirm(
			"Finished applying event %s in %.3f seconds.",
			this.name,
			Logger.getTimeSince(startTime)
		);
	}



	public String getName() {
		return name;
	}

	public String getLink(String key) {
		return linkMap.get(key);
	}

	public Map<String, String> getLinks() {
		return linkMap;
	}

	public String getUnlink(String key) {
		return unlinkMap.get(key);
	}

	public Map<String, String> getUnlinks() {
		return unlinkMap;
	}

	public List<Node> getModifierNodes() {
		return modifierNodes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addLink(String key, String value) {
		linkMap.put(key, value);
	}

	public void addUnlink(String key, String value) {
		unlinkMap.put(key, value);
	}

	public void addModifierNode(Node node) {
		modifierNodes.add(node);
	}

}
