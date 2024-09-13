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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Builder {
	public static Node checkRemoval(Node node) {
		if (node.getFlag() == Node.REMOVE)
			return null;

		return node;
	}

	public static String asString(Node node, String parent) {
		if (node.getFlag() == Node.REMOVE)
			return null;

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
		if (node.getFlag() == Node.REMOVE)
			return 0;

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

	public static int asInt(Node node, String parent, int def) {
		if (node.getFlag() == Node.REMOVE)
			return def;

		List<String> args = node.getArgs();

		if (args.size() < 1) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return def;
		}

		if (args.size() > 1) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		try {
			return Integer.parseInt(args.get(0));
		} catch (NumberFormatException e) {
			Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_INT, parent, node);
			return def;
		}
	}

	public static double asDouble(Node node, String parent) {
		if (node.getFlag() == Node.REMOVE)
			return 0;

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

	public static double asDouble(Node node, String parent, double def) {
		if (node.getFlag() == Node.REMOVE)
			return def;

		List<String> args = node.getArgs();

		if (args.size() < 1) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return def;
		}

		if (args.size() > 1) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		try {
			return Double.parseDouble(args.get(0));
		} catch (NumberFormatException e) {
			Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_REAL, parent, node);
			return def;
		}
	}

	public static void mapDouble(Node node, Map<String, Double> dest, String parent) {
		List<String> args = node.getArgs();

		if (args.size() < 2) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return;
		}

		if (args.size() > 2) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		if (node.getFlag() == Node.REMOVE) {
			try {
				dest.remove(args.get(0));
			} catch (NullPointerException e) {
				Logger.nodeErr(Logger.ERROR_NULL_MAP_KEY, parent, node);
			}
		} else {
			try {
				dest.put(args.get(0), Double.parseDouble(args.get(1)));
			} catch (NumberFormatException e) {
				Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_REAL, parent, node);
			}
		}
	}

	public static void mapDoubleArray(Node node, Map<String, Double[]> dest, String parent) {
		List<String> args = node.getArgs();

		if (args.size() < 2) {
			Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, parent, node);
			return;
		}

		if (args.size() > 2) {
			Logger.nodeWarn(Logger.WARNING_EXTRA_ARGS, parent, node);
		}

		Double[] doubles = new Double[args.size() - 1];

		if (node.getFlag() == Node.REMOVE) {
			try {
				dest.remove(args.get(0));
			} catch (NullPointerException e) {
				Logger.nodeErr(Logger.ERROR_NULL_MAP_KEY, parent, node);
			}
		} else {
			try {
				for (int i = 0; i < doubles.length; i++) {
					doubles[i] = Double.parseDouble(args.get(i + 1));
				}
				dest.put(args.get(0), doubles);
			} catch (NumberFormatException e) {
				Logger.nodeErr(Logger.ERROR_NUMBER_FORMAT_REAL, parent, node);
			}
		}
	}

	public static RectCoordinate asCoordinate(Node node, String parent) {
		if (node.getFlag() == Node.REMOVE)
			return null;

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
		if (node.getFlag() == Node.REMOVE)
			return null;


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
		if (node.getFlag() == Node.REMOVE)
			return Color.WHITE;

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

	public static List<String> modifyList(
		List<String> original,
		Node node,
		String parent
	) {
		List<String> args = node.getArgs();
		List<String> newList = new ArrayList<>(original);

		if (node.getFlag() == Node.ADD) {
			for (String arg : args) {
				newList.add(arg);
			}
			return newList;
		} else if (node.getFlag() == Node.REMOVE) {
			if (args.size() == 0)
				return new ArrayList<>();

			for (String arg : args) {
				newList.remove(arg);
			}
			return newList;
		}
		
		if (node.getFlag() != Node.NORMAL)
			Logger.nodeWarn(Logger.WARNING_WRONG_FLAG, parent, node);

		return args;
	}

	public static String modifyDescription(
		String original,
		boolean priorChanges,
		Node node,
		String parent
	) {
		String desc = original;

		if (node.getFlag() == Node.ADD || priorChanges) {
			desc += asString(node, parent) + "\n";
		} else if (node.getFlag() == Node.REMOVE) {
			desc = "\t";
		} else {
			if (node.getFlag() != Node.NORMAL)
				Logger.nodeWarn(Logger.WARNING_WRONG_FLAG, parent, node);
	
			desc = "\t" + asString(node, parent) + "\n";
		}

		return desc;
	}
}
