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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Viewport extends DrawnItem {
	public static final String TYPE = "viewport";
	public static final List<String> VALID_COMPONENTS = 
		Arrays.asList(new String[]{"map", "image", "legend", "line", "oval", "rect", "text"});

	public Viewport(Node node) {
		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr(Logger.ERROR_UNNAMED_NODE, TYPE, node);
		}

		for (Node child : node.getChildren()) {
			if (child.getName().equals("size")) {
				size = Builder.asCoordinate(child, TYPE);
			} else if (child.getName().equals("file format")) {
				fileFormat = Builder.asString(child, TYPE);
			} else if (VALID_COMPONENTS.contains(child.getName())) {
				components.add(child);
			}
		}

		offset = new RectCoordinate();
		setupGraphics();
		fillCanvas(Color.BLACK);
	}

	private String fileFormat = "png";
	private List<Node> components = new ArrayList<>();

	@Override
	public String getType() {
		return "viewport";
	}

	// Viewports don't need any custom data to be loaded.
	@Override
	public void load() {}

	// Draw the viewport.
	@Override
	public void draw() {
		long startTime = System.nanoTime();
		Logger.notify("Drawing viewport %s...", this.name);

		for (Node component : components) {
			List<String> args = component.getArgs();

			if (component.getName().equals("map")) {
				System.out.println("Drawing map...");

				// Easter egg
				int flipMode = DrawnItem.FLIP_NONE;
				for (Node grand : components) {
					if (grand.getName().equals("australia")) {
						flipMode = DrawnItem.FLIP_VERTICAL;
					}
				}
				try {
					drawImage(
						Main.getMap(args.get(0)).getImage(),
						Double.parseDouble(args.get(1)),
						Double.parseDouble(args.get(2)),
						flipMode
					);
				} catch (Exception e) {
					Logger.nodeErr(Logger.ERROR_DRAWING, TYPE, component);
				}
			} else if (component.getName().equals("image")) {
				System.out.println("Drawing image...");
				try {
					Sprite sprite = new Sprite(component);
					drawSprite(
						sprite,
						Double.parseDouble(args.get(1)),
						Double.parseDouble(args.get(2)),
						false
					);
				} catch (Exception e) {
					Logger.nodeErr(Logger.ERROR_DRAWING, TYPE, component);
				}
			} else if (component.getName().equals("legend")) {
				System.out.println("Drawing legend...");
				try {
					Legend legend = Main.getLegend(args.get(0));
					if (legend.getAlignment() == Legend.ALIGN_LEFT) {
						drawImage(
							legend.getImage(),
							0,
							Double.parseDouble(args.get(1))
						);
					} else {
						drawImage(
							legend.getImage(),
							size.getX() - 200,
							Double.parseDouble(args.get(1))
						);
					}
				} catch (Exception e) {
					Logger.nodeErr(Logger.ERROR_DRAWING, TYPE, component);
				}
			} else {
				Color chosenColor = Color.WHITE;
				for (Node child : component.getChildren()) {
					if (child.getName().equals("color")) {
						chosenColor = Builder.asColor(child, TYPE);
					}
				}

				if (component.getName().equals("line")) {
				System.out.println("Drawing line...");
				try {
					drawLine(
							Double.parseDouble(args.get(0)),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							Double.parseDouble(args.get(3)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr(Logger.ERROR_DRAWING, TYPE, component);
					}
				} else if (component.getName().equals("oval")) {
					System.out.println("Drawing oval...");
					try {
						drawOval(
							Double.parseDouble(args.get(0)),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							Double.parseDouble(args.get(3)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr(Logger.ERROR_DRAWING, TYPE, component);
					}
				} else if (component.getName().equals("rect")) {
					System.out.println("Drawing rectangle...");
					try {
						drawRect(
							Double.parseDouble(args.get(0)),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							Double.parseDouble(args.get(3)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr(Logger.ERROR_DRAWING, TYPE, component);
					}
				} else if (component.getName().equals("text")) {
					System.out.println("Drawing text...");
					try {
						drawString(
							args.get(0),
							Double.parseDouble(args.get(1)),
							Double.parseDouble(args.get(2)),
							chosenColor
						);
					} catch (Exception e) {
						Logger.nodeErr(Logger.ERROR_DRAWING, TYPE, component);
					}
				}
			}
		}

		Logger.confirm(
			"Finished drawing viewport %s in %.3f seconds.",
			this.name,
			Logger.getTimeSince(startTime)
		);
	}

	// Getters and setters
	public String getFileFormat() {
		return fileFormat;
	}

	public List<Node> getComponents() {
		return components;
	}
}
