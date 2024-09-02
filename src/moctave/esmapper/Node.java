package moctave.esmapper;

import java.util.List;

import java.io.File;

// A node on the data tree.
public class Node {
	public Node(
		String name,
		List<String> args,
		List<Node> children,
		int line,
		File file,
		boolean fromPlugin
	) {
		this.name = name;
		this.args = args;
		this.children = children;
		this.line = line;
		this.file = file;
		this.fromPlugin = fromPlugin;

		Main.incrementTNC();
	}

	private String name;
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
