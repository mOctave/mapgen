package moctave.esmapper;

import java.util.List;

import java.io.File;

// A node on the data tree.
public class Node {
	public static final int NORMAL = 0;
	public static final int ADD = 1;
	public static final int REMOVE = -1;

	public Node(
		String name,
		int flag,
		List<String> args,
		List<Node> children,
		int line,
		File file,
		boolean fromPlugin
	) {
		this.name = name;
		this.flag = flag;
		this.args = args;
		this.children = children;
		this.line = line;
		this.file = file;
		this.fromPlugin = fromPlugin;

		Main.incrementTNC();
	}

	private String name;

	private int flag = NORMAL;

	private List<String> args;
	private List<Node> children;
	private boolean fromPlugin;

	private int line;
	private File file;

	public String toString() {
		return String.format(
			"Node{name: %s, args: %s, children: %d}",
			name,
			args.toString(),
			children.size()
		);
	}

	public void addChild(Node child) {
		children.add(child);
	}

	public String getName() {
		return name;
	}

	public int getFlag() {
		return flag;
	}

	public List<String> getArgs() {
		return args;
	}

	public List<Node> getChildren() {
		return children;
	}

	public boolean fromPlugin() {
		return fromPlugin;
	}

	public int getLine() {
		return line;
	}

	public File getFile() {
		return file;
	}
}
