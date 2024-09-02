package moctave.esmapper;

import java.awt.Color;
import java.util.List;

public class Builder {
	public static String asString(Node node, String parent) {
		List<String> args = node.getArgs();

		if (args.size() < 1) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return null;
		}

		if (args.size() > 1) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		return args.get(0);
	}

	public static int asInt(Node node, String parent) {
		List<String> args = node.getArgs();

		if (args.size() < 1) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return 0;
		}

		if (args.size() > 1) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		try {
			return Integer.parseInt(args.get(0));
		} catch (NumberFormatException e) {
			Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_INT, parent, node);
			return 0;
		}
	}

	public static double asDouble(Node node, String parent) {
		List<String> args = node.getArgs();

		if (args.size() < 1) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return 0;
		}

		if (args.size() > 1) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		try {
			return Double.parseDouble(args.get(0));
		} catch (NumberFormatException e) {
			Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_REAL, parent, node);
			return 0;
		}
	}

	public static RectCoordinate asCoordinate(Node node, String parent) {
		List<String> args = node.getArgs();

		if (args.size() < 2) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return null;
		}

		if (args.size() > 2) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		try {
			return new RectCoordinate(
				Double.parseDouble(args.get(0)),
				Double.parseDouble(args.get(1))
			);
		} catch (NumberFormatException e) {
			Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_REAL, parent, node);
			return null;
		}
	}

	public static Sprite asSprite(Node node, String parent) {
		List<String> args = node.getArgs();

		if (args.size() < 1) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return null;
		}

		if (args.size() > 1) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		return new Sprite(node);
	}

	public static Color asColor(Node node, String parent) {
		List<String> args = node.getArgs();
		Color color = null;

		if (args.size() < 1) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return null;
		}

		if (args.size() > 4) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		} else if (args.size() == 2) {
			Logger.nodeWarn(Logger.WARNING_UNLIKELY_ARGS, parent, node);
		}

		if (args.size() >= 4) {
			try {
				color = new Color(
					Float.parseFloat(args.get(0)),
					Float.parseFloat(args.get(1)),
					Float.parseFloat(args.get(2)),
					Float.parseFloat(args.get(3))
				);
			} catch (NumberFormatException e) {
				Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_REAL, parent, node);
			}
		} else if (args.size() == 3) {
			try {
				color = new Color(
					Float.parseFloat(args.get(0)),
					Float.parseFloat(args.get(1)),
					Float.parseFloat(args.get(2))
				);
			} catch (NumberFormatException e) {
				Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_REAL, parent, node);
			}
		} else {
			color = Main.getColor(args.get(0));
		}

		return color;
	}

	public static Color resolveColor(List<String> args) {
		Color color = null;

		if (args.size() < 1) {
			Logger.err("Incomplete color definition.");
			return null;
		}

		if (args.size() > 4) {
			Logger.warn("Extra arguments in color definition.");
		} else if (args.size() == 2) {
			Logger.warn("Unlikely number of arguments in color definition.");
		}

		if (args.size() >= 4) {
			try {
				color = new Color(
					Float.parseFloat(args.get(0)),
					Float.parseFloat(args.get(1)),
					Float.parseFloat(args.get(2)),
					Float.parseFloat(args.get(3))
				);
			} catch (NumberFormatException e) {
				Logger.err("Improperly formatted numbers in RGBA color definition.");
			}
		} else if (args.size() == 3) {
			try {
				color = new Color(
					Float.parseFloat(args.get(0)),
					Float.parseFloat(args.get(1)),
					Float.parseFloat(args.get(2))
				);
			} catch (NumberFormatException e) {
				Logger.err("Improperly formatted numbers in RGB color definition.");
			}
		} else {
			color = Main.getColor(args.get(0));
		}

		return color;
	}
}
