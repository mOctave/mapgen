package moctave.esmapper;

public class Logger {
	public static final String BLACK = "\u001B[30m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String MAGENTA = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	public static final String WHITE = "\u001B[37m";
	public static final String RESET = "\u001B[0m";

	public static void nodeErr(String message, Node node) {
		System.err.printf(
			"%sNode Error on line %d of %s: %s%n\t(Node: %s)%s%n",
			RED,
			node.getLine(),
			node.getFile().getName(),
			message,
			node.toString(),
			RESET
		);
	}

	public static void nodeWarn(String message, Node node) {
		System.err.printf(
			"%sNode Warning on line %d of %s: %s%n\t(Node: %s)%s%n",
			YELLOW,
			node.getLine(),
			node.getFile().getName(),
			message,
			node.toString(),
			RESET
		);
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
