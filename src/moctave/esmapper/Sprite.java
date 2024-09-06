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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A sprite containing a file reference and (potentially) scale
 * or animation information.
 */
public class Sprite {
	public static final String TYPE = "sprite";
	public Sprite(Node node) {
		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_MISSING_FILENAME, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			if (child.getName().equals("scale")) {
				setScale(Builder.asDouble(child, TYPE));
			}
		}
	}

	private String name;
	private double scale = 1;

	public String toString() {
		return String.format("%s @%.3fx", name, scale);
	}

	/**
	 * Find an image file corresponding to this sprite.
	 * Works by searching through plugins in reverse order and checking
	 * files against a regular expression. Vanilla content is handled last.
	 * @return A {@link File} object that matches this sprite's name.
	 */
	public File resolve() {
		List<String> paths = new ArrayList<>(Main.getPlugins());
		Collections.reverse(paths);
		paths.add(Main.getGameDir());

		for (String path : paths) {
			String dir = path;
			if (!dir.endsWith("/"))
				dir += "/";

			dir += "images/";

			File base = new File(dir + name);
			try {
				Pattern pattern = Pattern.compile(
					base.getName().split("\\.")[0].replace("/", "\\/")
					+ "[\\+\\-\\~]?\\.\\S+",
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

	// Getters
	public String getName() {
		return name;
	}

	public double getScale() {
		return scale;
	}

	// Setters
	public void setName(String name) {
		this.name = name;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}
