package moctave.esmapper;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Legend extends DrawnItem {
	public static final int ALIGN_LEFT = 0;
	public static final int ALIGN_RIGHT = 1;

	public Legend(Node node) {
		// Assign name
		try {
			this.name = node.getArgs().get(0);
		} catch (IndexOutOfBoundsException e) {
			Logger.nodeErr("Unnamed viewport!", node);
		}

		offset = new Point(0, 0);

		for (Node child : node.getChildren()) {
			List<String> args = child.getArgs();

			if (child.getName().equals("align")) {
				if (args.size() < 1) {
					Logger.nodeErr("Incomplete align node in legend definition.",
						child);
				} else if (args.get(0).equals("left")) {
					alignment = ALIGN_LEFT;
				} else if (args.get(0).equals("right")) {
					alignment = ALIGN_RIGHT;
				} else {
					Logger.nodeErr("Invalid legend alignment.", child);
				}
			} else if (child.getName().equals("header")) {
				try {
					texts.add(args.get(0));
					rings.add(null);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete header node in legend definition.",
						child);
				}
			} else if (child.getName().equals("item")) {
				try {
					texts.add(args.get(0));
					args.remove(0);
					rings.add(args);
				} catch (IndexOutOfBoundsException e) {
					Logger.nodeErr("Incomplete item node in legend definition.",
						child);
				}
			}
		}

		size = new Dimension(
			200,
			60 + 20 * texts.size()
		);

		offset = new Point(0, 0);
		setupGraphics();
	}

	private int alignment = ALIGN_LEFT;

	private List<String> texts = new ArrayList<>();
	private List<List<String>> rings = new ArrayList<>();

	@Override
	public String getType() {
		return "legend";
	}

	// Legends don't need any custom data to be loaded.
	@Override
	public void load() {}

	@Override
	public void draw() {
		long startTime = System.nanoTime();
		Logger.notify("Drawing legend %s...", this.name);

		int offset = 0;
		int flipMode = DrawnItem.FLIP_NONE;

		if (alignment == ALIGN_RIGHT) {
			flipMode = DrawnItem.FLIP_HORIZONTAL;
			offset = 50;
		}

		drawImage(Main.LEGEND_BG_LEFT_TOP, 0, 0, flipMode);
		int i;
		for (i = 1; i <= texts.size(); i++) {
			drawImage(Main.LEGEND_BG_LEFT[(int) (Math.random() * 10)], 0, i * 20, flipMode);
		}
		drawImage(Main.LEGEND_BG_LEFT_BOTTOM, 0, i * 20, flipMode);


		for (i = 0; i < texts.size(); i++) {
			
	
			if (rings.get(i) == null) {
				drawString(
					texts.get(i),
					offset + 22,
					i * 20 + 44,
					Main.getColor("message log importance high")
				);
			} else {
				drawString(
					texts.get(i),
					offset + 22,
					i * 20 + 44,
					Main.getColor("map name")
				);
				graphics.setStroke(new BasicStroke(3));
				drawOval(
					offset + 6,
					i * 20 + 34,
					10,
					10,
					Main.getColorFromArgs(rings.get(i))
				);
			}
		}

		Logger.confirm(
			"Finished drawing legend %s in %.3f seconds.",
			this.name,
			Logger.getTimeSince(startTime)
		);
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
}
