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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;

public class Parser {
	public Parser (
		File file,
		boolean isConfig,
		boolean isPlugin
	) {
		this.file = file;
		this.isConfig = isConfig;
		this.isPlugin = isPlugin;
	}

	private Node currentNode;
	private int lineNumber = 0;

	// The file to parse.
	private File file;
	
	// True if the file is a config file, false if it's an ES datafile.
	private boolean isConfig;
	
	private boolean isPlugin;

	public void parse() {
		try {
			Scanner s = new Scanner(file);

			Deque<Node> nodeStack = new ArrayDeque<>();

			int tabs = 0;
			int lastTabs = 0;
			currentNode = null;

			while (s.hasNextLine()) {
				lineNumber ++;
				String line = s.nextLine();
				tabs = countLeadingTabs(line);
				if (tabs > lastTabs && currentNode != null) {
					nodeStack.push(currentNode);
				} else {
					while (nodeStack.size() > tabs) {
						nodeStack.pop();
					}
				}
				currentNode = makeNewNode(line);

				if (nodeStack.isEmpty() && currentNode != null) {
					if (isConfig) {
						Main.addConfigNode(currentNode);
						if (currentNode.getName().equals("extends")) {
							for (String filename : currentNode.getArgs()) {
								Logger.notify("Parsing extended config file %s.", filename);
								Parser parser = new Parser(new File(filename), true, false);
								parser.parse();
							}
						}
					} else {
						Main.addNode(currentNode);
					}
				} else if (currentNode != null) {
					nodeStack.peek().addChild(currentNode);
				}

				lastTabs = tabs;
			}

			s.close();
		} catch (FileNotFoundException e) {
			Logger.err("No such file as %s.", file.getAbsolutePath());
		}
	}

	public Node makeNewNode(String line) {
		String trimmedLine = line.trim();
		List<String> data = new ArrayList<>();
		char splitOn = ' ';
		String currentItem = "";
		boolean isEmpty = true;
		for (char c : trimmedLine.toCharArray()) {
			if (isEmpty && c != ' ') {
				isEmpty = false;
				if (c == '"') {
					// Item will end with a double quote, ignore this one.
					splitOn = '"';
					continue;
				} else if (c == '`') {
					// Item will end with a backtick, ignore this one.
					splitOn = '`';
					continue;
				} else {
					// Item will end when the word it's in does.
					splitOn = ' ';
				}
			} else if (isEmpty) {
				continue;
			}


			if (c == splitOn) {
				// Found end of item, add it to the list and start on the next one
				data.add(currentItem);
				currentItem = "";
				isEmpty = true;
				continue;
			} else if (c == '#' && splitOn == ' ') {
				// Ignore everything after a comment
				break;
			}

			// Add to the current item
			currentItem += c;
		}

		// Items can end at the end of the line, too
		if (!currentItem.isEmpty()) {
			data.add(currentItem);
		}

		if (data.size() == 0)
			return null;

		// The first entry is the node name, everything else is args.
		String nodeName = data.get(0);
		data.remove(0);

		int flag = Node.NORMAL;

		// Check for flags
		if (nodeName.equals("add")) {
			flag = Node.ADD;
			nodeName = data.get(0);
			data.remove(0);
		} else if (nodeName.equals("remove")) {
			flag = Node.REMOVE;
			nodeName = data.get(0);
			data.remove(0);
		}


		return new Node(nodeName, flag, data, new ArrayList<>(), lineNumber, file, isPlugin);
	}

	public static String trimComments(String line) {
		return line.split("#")[0];
	}

	public static int countLeadingTabs(String line) {
		int i = 0;
		while (i < line.length() && line.charAt(i) == '\t') {
			i++;
		}
		return i;
	}

	public static boolean containsOnlyWhitespace(String line) {
		// If the line is empty, for our purposes it's all whitespace.
		if (line == null || line.isEmpty())
			return true;

		// Check if the line contains anything that isn't whitespace.
		for (int i = 0; i < line.length(); i++) {
			if (!Character.isWhitespace(line.charAt(i)))
				return false;
		}

		return true;
	}

	public File getFile() {
		return file;
	}

	public boolean isConfig() {
		return isConfig;
	}
}