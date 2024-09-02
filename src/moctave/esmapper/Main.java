package moctave.esmapper;

import java.io.File;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class Main {
	public static final Font UBUNTU = getFontAsset("/ubuntu.ttf");
	public static final BufferedImage LEGEND_BG_LEFT_TOP = getImageAsset("/legend-lt.png");
	public static final BufferedImage LEGEND_BG_LEFT_BOTTOM = getImageAsset("/legend-lb.png");
	public static final BufferedImage[] LEGEND_BG_LEFT = new BufferedImage[]{
		getImageAsset("/legend-l0.png"),
		getImageAsset("/legend-l1.png"),
		getImageAsset("/legend-l2.png"),
		getImageAsset("/legend-l3.png"),
		getImageAsset("/legend-l4.png"),
		getImageAsset("/legend-l5.png"),
		getImageAsset("/legend-l6.png"),
		getImageAsset("/legend-l7.png"),
		getImageAsset("/legend-l8.png"),
		getImageAsset("/legend-l9.png")
	};

	private static Font getFontAsset(String res) {
		try {
			InputStream is = Main.class.getResourceAsStream(res);
			return Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (Exception e) {
			Logger.warn(
				"Could not load font at %s. The default system font will be used instead.",
				res
			);
			e.printStackTrace();
		}

		return Font.decode(null);
	}

	private static BufferedImage getImageAsset(String res) {
		try {
			InputStream is = Main.class.getResourceAsStream(res);
			return ImageIO.read(is);
		} catch (Exception e) {
			Logger.err(
				"Could not load image at %s.",
				res
			);
			e.printStackTrace();
		}

		return null;
	}

	private static List<Node> configNodes = new ArrayList<>();

	private static List<Node> nodes = new ArrayList<>();
	private static int totalNodeCount = 0;

	private static String gameDir = "";
	private static List<String> plugins = new ArrayList<>();

	// Maps and viewports.
	private static HashMap<String, Map> maps = new HashMap<>();
	private static HashMap<String, Legend> legends = new HashMap<>();
	private static List<Viewport> viewports = new ArrayList<>();

	// All defined objects.
	private static HashMap<String, Color> colors = new HashMap<>();
	private static HashMap<String, List<String>> eventLists = new HashMap<>();

	public static void main(String[] args) {
		System.out.println("Mapping Systems!");
		if (args.length == 0) {
			Logger.err("No config file specified.");
			System.exit(1);
		}

		Logger.notify("Loading config...");
		Parser configParser = new Parser(new File(args[0]), true, false);
		configParser.parse();
		loadConfig();

		System.out.printf("Config loaded: %d top-level nodes found.%n", configNodes.size());

		// Parse game data files
		Logger.notify("Parsing data files...");
		List<String> paths = new ArrayList<>(plugins);
		paths.add(0, gameDir);
		System.out.println(paths);
		for (String path : paths) {
			String dir = path;
			if (!dir.endsWith("/"))
				dir += "/";

			dir += "data/";

			if (path.equals(gameDir)) {
				Logger.notify("Parsing vanilla Endless Sky");
				examineFile(new File(dir), false);
			} else {
				File f = new File(path);
				Logger.notify("Parsing plugin (%s)", f.getName());
				examineFile(new File(dir), true);
			}
		}

		System.out.printf("Parsing complete: %d top-level nodes found.%n", nodes.size());
		System.out.printf("A total of %d nodes were parsed.%n", totalNodeCount);

		Logger.notify("Setting up global data...");
		for (Node node : nodes) {
			if (node.getName().equals("color")) {
				try {
					if (node.getArgs().size() == 5) {
						addColor(node.getArgs().get(0), new Color(
							Float.parseFloat(node.getArgs().get(1)),
							Float.parseFloat(node.getArgs().get(2)),
							Float.parseFloat(node.getArgs().get(3)),
							Float.parseFloat(node.getArgs().get(4))
						));
					} else {
						addColor(node.getArgs().get(0), new Color(
							Float.parseFloat(node.getArgs().get(1)),
							Float.parseFloat(node.getArgs().get(2)),
							Float.parseFloat(node.getArgs().get(3))
						));
					}
				} catch (Exception e) {
					Logger.nodeErr("Invalid color definition.", node);
					e.printStackTrace();
				}
			}
		}
		System.out.printf("Total colors: %d.%n", colors.size());

		for (Map map : maps.values()) {
			map.load();
			map.draw();
		}

		for (Legend legend : legends.values()) {
			legend.draw();
		}

		for (Viewport viewport : viewports) {
			viewport.draw();
			viewport.save(viewport.getFileFormat());
		}
	}

	public static void loadConfig() {
		for (Node node : configNodes) {
			if (node.getName().equals("event list")) {
				try {
					List<String> events = new ArrayList<>();
					for (Node child : node.getChildren()) {
						events.add(child.getName());
					}

					eventLists.put(node.getArgs().get(0), events);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete event list node in config.",
						node);
				}
			} else if (node.getName().equals("game directory")) {
				try {
					gameDir = node.getArgs().get(0);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete game directory node in config.",
						node);
				}
			} else if (node.getName().equals("plugin directory")) {
				try {
					plugins.add(node.getArgs().get(0));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete plugin directory node in config.",
						node);
				}
			}
		}

		for (Node node : configNodes) {
			if (node.getName().equals("map")) {
				try {
					maps.put(node.getArgs().get(0), new Map(node));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete map node in config.",
						node);
				}
			} else if (node.getName().equals("legend")) {
				System.out.println("Adding legend " + node.getArgs().get(0));
				try {
					legends.put(node.getArgs().get(0), new Legend(node));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete legend node in config.",
						node);
				}
			} else if (node.getName().equals("viewport")) {
				try {
					viewports.add(new Viewport(node));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete viewport node in config.",
						node);
				}
			}
		}
	}

	public static void examineFile(File file, boolean isPlugin) {
		if (file.getName().endsWith(".txt")) {
			// Any .txt files are presumed to be text files, and so are parsed.
			Parser p = new Parser(file, false, isPlugin);
			p.parse();
		}

		if (file.isDirectory()) {
			// Every file in a directory is itself examined and/or parsed.
			for (File child : file.listFiles()) {
				examineFile(child, isPlugin);
			}
		}
	}

	public static void addNode(Node node) {
		nodes.add(node);
	}

	public static void addConfigNode(Node node) {
		configNodes.add(node);
	}

	public static void incrementTNC() {
		totalNodeCount ++;
	}

	public static int getTotalNodeCount() {
		return totalNodeCount;
	}

	public static void addColor(String name, Color color) {
		colors.put(name, color);
	}

	public static Color getColor(String key) {
		Color c = colors.get(key);
		if (c == null) {
			c = Color.WHITE;
			Logger.warn(
				"Reference to undefined color %s, using white instead.", key);
		}

		return c;
	}

	public static List<String> getEventList(String key) {
		List<String> l = eventLists.get(key);
		if (l == null) {
			l = new ArrayList<>();
			Logger.warn(
				"Reference to undefined event list %s, using an empty list instead.", key);
		}

		return l;
	}

	public static String getGameDir() {
		return gameDir;
	}

	public static List<Node> getNodes() {
		return nodes;
	}

	public static File getSpriteByName(String name) {
		// Go through plugins in reverse order for the search, then do vanilla content.
		List<String> paths = new ArrayList<>(plugins);
		Collections.reverse(paths);
		paths.add(gameDir);

		for (String path : paths) {
			String dir = path;
			if (!dir.endsWith("/"))
				dir += "/";

			dir += "images/";

			File base = new File(dir + name);
			try {
				Pattern pattern = Pattern.compile(
					base.getName().split("\\.")[0]+"[\\+\\-\\~]?\\.\\S+",
					Pattern.CASE_INSENSITIVE
				);
				for (File f : base.getParentFile().listFiles()) {
					if (pattern.matcher(f.getName()).find()) {
						if (f.exists()) {
							return f;
						}
					}
				}
			} catch (NullPointerException e) {
				// Plugin is likely missing an images directory,
				// nothing to be concerned about.
			}
		}

		Logger.warn("Could not find sprite with name %s.", name);
		return null;
	}

	public static Color getColorFromArgs(List<String> args) {
		Color color = null;

		if (args.size() >= 4) {
			try {
				color = new Color(
					Float.parseFloat(args.get(0)),
					Float.parseFloat(args.get(1)),
					Float.parseFloat(args.get(2)),
					Float.parseFloat(args.get(3))
				);
			} catch (NumberFormatException e) {
				Logger.err("Incorrectly formatted RGBA color.");
			}
		} else if (args.size() == 3) {
			try {
				color = new Color(
					Float.parseFloat(args.get(0)),
					Float.parseFloat(args.get(1)),
					Float.parseFloat(args.get(2))
				);
			} catch (NumberFormatException e) {
				Logger.err("Incorrectly formatted RGB color.");
			}
		} else {
			try {
				color = Main.getColor(args.get(0));
			} catch (IndexOutOfBoundsException e) {
			Logger.err("Incorrectly formatted color name.");
			}
		}

		return color;
	}

	public static Legend getLegend(String key) {
		Legend l = legends.get(key);

		if (l == null) {
			Logger.warn("No legend with name %s.", key);
		}

		return l;
	}

	public static Map getMap(String key) {
		Map m = maps.get(key);

		if (m == null) {
			Logger.warn("No map with name %s.", key);
		}

		return m;
	}
}
