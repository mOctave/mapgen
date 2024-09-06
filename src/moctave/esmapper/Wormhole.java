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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wormhole implements EventModifiableObject {
	public static final String TYPE = "wormhole";
	public Wormhole(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("display name")) {
				displayName = Builder.asString(child, TYPE);
			} else if (child.getName().equals("mappable")) {
				mappable = true;
			} else if (child.getName().equals("link")) {
				try {
					links.put(args.get(0), args.get(1));
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, TYPE, child);
				}
			} else if (child.getName().equals("color")) {
				color = Builder.asColor(child, TYPE);
			}
		}
	}

	private String name;
	private String displayName = "???";

	private boolean mappable = false;
	private Map<String, String> links = new HashMap<>();
	private Color color = Main.getColor("map wormhole");

	@Override
	public void applyModifiers(Node node) {
		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("display name")) {
				displayName = Builder.asString(child, TYPE);

				if (displayName == null)
					displayName = "???";
			} else if (child.getName().equals("mappable")) {
				mappable = true && !(child.getFlag() == Node.REMOVE);
			} else if (child.getName().equals("link")) {
				if (child.getFlag() == Node.REMOVE) {
					links = new HashMap<>();
				} else {
					try {
						links.put(args.get(0), args.get(1));
					} catch (IndexOutOfBoundsException e) {
						Logger.nodeErr(Logger.ERROR_INCOMPLETE_NODE, TYPE, child);
					}
				}
			} else if (child.getName().equals("color")) {
				color = Builder.asColor(child, TYPE);
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isMappable() {
		return mappable;
	}

	public String getLink(String s) {
		return links.get(s);
	}

	public Map<String, String> getLinks() {
		return links;
	}

	public Color getColor() {
		return color;
	}

	public Color getDim() {
		return new Color(
			(float) color.getRed() / 255,
			(float) color.getGreen() / 255,
			(float) color.getBlue() / 255,
			(float) color.getAlpha() / 255 * 0.33f
		);
	}
}
