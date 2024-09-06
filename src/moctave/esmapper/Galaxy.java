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

public class Galaxy implements EventModifiableObject {
	public static final String TYPE = "galaxy";
	public Galaxy(Node node) {

		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			if (child.getName().equals("pos")) {
				position = Builder.asCoordinate(child, TYPE);
			} else if (child.getName().equals("sprite")) {
				sprite = Builder.asSprite(child, TYPE);
			}
		}
	}

	private String name;
	private RectCoordinate position = new RectCoordinate();
	
	private Sprite sprite;

	@Override
	public void applyModifiers(Node node) {
		for (Node child : node.getChildren()) {
			if (child.getName().equals("pos")) {
				position = Builder.asCoordinate(child, TYPE);
			} else if (child.getName().equals("sprite")) {
				sprite = Builder.asSprite(child, TYPE);
			}
		}
	}

	public String getName() {
		return name;
	}

	public RectCoordinate getPosition() {
		return position;
	}

	public Sprite getSprite() {
		return sprite;
	}
}
