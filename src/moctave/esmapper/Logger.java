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

public class Logger {
	// ANSI Colours
	public static final String BLACK = "\u001B[30m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String MAGENTA = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	public static final String WHITE = "\u001B[37m";
	public static final String RESET = "\u001B[0m";

	// Node Errors
	public static final int ERROR_INCOMPLETE_NODE = 1;
	public static final int ERROR_NUMBER_FORMAT_INT = 2;
	public static final int ERROR_NUMBER_FORMAT_REAL = 3;
	public static final int ERROR_OBJECT_CREATION = 4;
	public static final int ERROR_UNNAMED_NODE = 5;
	public static final int ERROR_WRONG_VALUE = 6;
	public static final int ERROR_MISSING_FILENAME = 7;
	public static final int ERROR_DRAWING = 8;
	public static final int ERROR_NULL_MAP_KEY = 9;

	public static final String[] ERROR_MESSAGES = new String[]{
		"Successful execution",
		"Incomplete $NODENAME node in $PARENT definition",
		"Invalid number (integer) in $NODENAME node",
		"Invalid number (real) in $NODENAME node",
		"Exception thrown during $PARENT creation",
		"Unnamed $NODENAME node",
		"Unexpected argument for $NODENAME node",
		"Missing filename in $NODENAME node",
		"Failed to draw $NODENAME to $PARENT",
		"Invalid map key while for removing $NODENAME entry"
	};

	// Node Warnings
	public static final int WARNING_EXTRA_ARGS = 1;
	public static final int WARNING_OBJECT_CREATION = 2;
	public static final int WARNING_UNNAMED_NODE = 3;
	public static final int WARNING_UNLIKELY_VALUE = 4;
	public static final int WARNING_UNLIKELY_ARGS = 5;
	public static final int WARNING_WRONG_FLAG = 6;

	public static final String[] WARNING_MESSAGES = new String[]{
		"Successful execution",
		"Extra arguments for $NODENAME node in $PARENT definition",
		"Exception thrown during $PARENT creation",
		"Unnamed $NODENAME node",
		"Unlikely argument for $NODENAME node in $PARENT definition",
		"Unlikely number of arguments for $NODENAME node in $PARENT definition",
		"Unexpected flag for node $NODENAME"
	};

	public static void nodeErr(int errorType, String parent, Node node) {
		try {
			System.err.printf(
				"%sNode Error on line %d of %s: %s%n\t(Node: %s)%s%n",
				RED,
				node.getLine(),
				node.getFile().getName(),
				formatErrorCode(errorType, parent, node, false),
				node.toString(),
				RESET
			);
		} catch (IndexOutOfBoundsException e) {
			err(
				"Invalid node error type! Error Type: %d, Parent: %s, Node: %s",
				errorType,
				parent,
				node.toString()
			);
			e.printStackTrace();
		}
	}

	public static void nodeWarn(int warningType, String parent, Node node) {
		try {
			System.err.printf(
				"%sNode Warning on line %d of %s: %s%n\t(Node: %s)%s%n",
				YELLOW,
				node.getLine(),
				node.getFile().getName(),
				formatErrorCode(warningType, parent, node, false),
				node.toString(),
				RESET
			);
		} catch (IndexOutOfBoundsException e) {
			err(
				"Invalid node warning type! Warning Type: %d, Parent: %s, Node: %s",
				warningType,
				parent,
				node.toString()
			);
			e.printStackTrace();
		}
	}

	public static String formatErrorCode(
		int errorType,
		String parent,
		Node node,
		boolean isWarning
	) {
		String msg;
		if (isWarning)
			msg = WARNING_MESSAGES[errorType];
		else
			msg = ERROR_MESSAGES[errorType];

		msg = msg.replace("$NODENAME", node.getName());
		msg = msg.replace("$PARENT", parent);

		return msg;
	}

	public static void err(String message, Object... args) {
		System.err.printf(
			"%sError: %s%s%n",
			RED,
			String.format(message, args),
			RESET
		);
	}

	public static void warn(String message, Object... args) {
		System.err.printf(
			"%sWarning: %s%s%n",
			YELLOW,
			String.format(message, args),
			RESET
		);
	}

	public static void notify(String message, Object... args) {
		System.out.printf(
			"%s%s%s%n",
			BLUE,
			String.format(message, args),
			RESET
		);
	}

	public static void confirm(String message, Object... args) {
		System.out.printf(
			"%s%s%s%n",
			GREEN,
			String.format(message, args),
			RESET
		);
	}

	public static double getTimeSince(long startTime) {
		double dif = System.nanoTime() - startTime;
		dif = dif / 1000000 / 1000;
		return dif;
	}
}
