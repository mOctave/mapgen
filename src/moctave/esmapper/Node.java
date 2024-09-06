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
