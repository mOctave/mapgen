package moctave.esmapper;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public abstract class DrawnItem {
	public static final int FLIP_NONE = 0;
	public static final int FLIP_HORIZONTAL = 1;
	public static final int FLIP_VERTICAL = 2;
	public static final int FLIP_BOTH = 3;

	/** The name of this drawn item. Modified on a per-object basis. */
	protected String name;

	/** The {@link Graphics} to use to draw this item. Modified on a per-object basis. */
	protected Graphics2D graphics;

	/** An {@link Image} of this item. */
	protected Image image;

	/** The size of this item's canvas. */
	protected RectCoordinate size;

	/** How far to offset game coordinates to match the canvas. */
	protected RectCoordinate offset;

	/**
	 * What type of item this (eg "map" or "viewport").
	 * Should be overriden by any class that extends it.
	 */
	public String getType() {
		return "drawn item";
	}

	/**
	 * Loads all data related with this item.
	 * Should be overriden by any class that extends it.
	 */
	public void load() {
		Logger.warn("No load() method defined for %s %s.", getType(), name);
	};

	/**
	 * Draws this item with its associated {@link Drawer}.
	 * Should be overriden by any class that extends it.
	 */
	public void draw() {
		Logger.warn("No draw() method defined for %s %s.", getType(), name);
	}

	/**
	 * Saves this DrawnItem to a file.
	 * Usually doesn't need to be overriden.
	 */
	public void save(String format) {
		long startTime = System.nanoTime();
		Logger.notify("Saving %s %s to file...", getType(), name);

		try {
			File output = new File(name + "." + format);
			ImageIO.write((RenderedImage) image, format, output);

			Logger.confirm(
				"Finished saving %s %s in %.3f seconds.",
				getType(),
				name,
				Logger.getTimeSince(startTime)
			);
		} catch (IOException e) {
			Logger.err("Failed to save map to file.");
		}
	}

	/**
	 * Draws an image sprite.
	 */
	public boolean drawSprite(Sprite sprite, double x, double y, boolean measureFromImageCenter) {
		BufferedImage img;
		try {
			File f = sprite.resolve();
			
			if (f == null) {
				Logger.err("Invalid image sprite: %s.", sprite);
				return false;
			}

			img = ImageIO.read(f);
			img.getScaledInstance(
				(int) (img.getWidth() * sprite.getScale()),
				(int) (img.getHeight() * sprite.getScale()),
				BufferedImage.SCALE_DEFAULT
			);
		} catch (IOException e) {
			Logger.err("Invalid image sprite: %s.", sprite);
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			Logger.err("Looking for sprite, found null value!");
			return false;
		}

		if (measureFromImageCenter) {
			graphics.drawImage(
				img,
				(int) (relativeX(x) - img.getWidth() / 2),
				(int) (relativeY(y) - img.getHeight() / 2),
				null
			);
		} else {
			graphics.drawImage(
				img,
				relativeX(x),
				relativeY(y),
				null
			);
		}

		return true;
	}

	/**
	 * Draws a specific image.
	 */
	public void drawImage(Image img, double x, double y) {
		graphics.drawImage(
			img,
			relativeX(x),
			relativeY(y),
			null
		);
	}

	/**
	 * Draws a specific image, optionally flipped along an axis.
	 */
	public void drawImage(Image img, double x, double y, int flip) {
		if (flip == FLIP_NONE) {
			graphics.drawImage(
				img,
				relativeX(x),
				relativeY(y),
				null
			);
		} else {
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			if (flip == FLIP_HORIZONTAL) {
				graphics.drawImage(
					img,
					relativeX(x) + width,
					relativeY(y),
					-width,
					height,
					null
				);
			} else if (flip == FLIP_VERTICAL) {
				graphics.drawImage(
					img,
					relativeX(x),
					relativeY(y) + height,
					width,
					-height,
					null
				);
			} else if (flip == FLIP_BOTH) {
				graphics.drawImage(
					img,
					relativeX(x) + width,
					relativeY(y) + height,
					-width,
					-height,
					null
				);
			}
		}
	}

	/**
	 * Draws a straight line.
	 */
	public void drawLine(double x1, double y1, double x2, double y2, Color color) {
		graphics.setColor(color);
		graphics.drawLine(
			relativeX(x1),
			relativeY(y1),
			relativeX(x2),
			relativeY(y2)
		);
	}

	/**
	 * Draws an oval.
	 */
	public void drawOval(double x, double y, double width, double height, Color color) {
		graphics.setColor(color);
		graphics.drawOval(
			relativeX(x),
			relativeY(y),
			(int) width,
			(int) height
		);
	}

	/**
	 * Draws a rectangle.
	 */
	public void drawRect(double x, double y, double width, double height, Color color) {
		graphics.setColor(color);
		graphics.drawRect(
			relativeX(x),
			relativeY(y),
			(int) width,
			(int) height
		);
	}

	/**
	 * Draws a string.
	 */
	public void drawString(String str, double x, double y, Color color) {
		graphics.setColor(color);
		graphics.drawString(
			str,
			relativeX(x),
			relativeY(y)
		);
	}

	/**
	 * Shrinks a line by the desired amount.
	 * Line position is specified with a double array: {x1, x2, y1, y2}.
	 */
	public static double[] offsetLineFromEndpoints(
		double x1,
		double y1,
		double x2,
		double y2,
		double offset
	) {
		double multi = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		multi = 1 / Math.sqrt(multi);
		double ux = (x1 - x2) * multi * offset;
		double uy = (y1 - y2) * multi * offset;

		return new double[]{
			x1 - ux,
			y1 - uy,
			x2 + ux,
			y2 + uy
		};
	}

	/**
	 * Fills the canvas with a solid {@link Color}.
	 */
	public void fillCanvas(Color color) {
		graphics.setColor(color);
		graphics.fillRect(0, 0, (int) size.getX(), (int) size.getY());
	}

	/**
	 * Sets up a new {@link #image} for this item,
	 * then modifies the {@link #graphics} to match it.
	 */
	public void setupGraphics() {
		image = new BufferedImage(
			(int) size.getX(),
			(int) size.getY(),
			BufferedImage.TYPE_INT_ARGB
		);

		graphics = (Graphics2D) image.getGraphics();

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		Map<TextAttribute, Object> attributes = new HashMap<>();

		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
		attributes.put(TextAttribute.SIZE, 14);

		Font ubuntu14 = Main.UBUNTU.deriveFont(attributes);
		graphics.setFont(ubuntu14);
	}
	

	/**
	 * Adds the horizontal offset to an X-coordinate.
	 */
	public int relativeX(double x) {
		return (int) (x + offset.getX());
	}

	/**
	 * Adds the vertical offset to an Y-coordinate.
	 */
	public int relativeY(double y) {
		return (int) (y + offset.getY());
	}

	// Getters and setters
	public RectCoordinate getSize() {
		return size;
	}

	public RectCoordinate getOffset() {
		return offset;
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public Image getImage() {
		return image;
	}

	public void setSize(RectCoordinate size) {
		this.size = size;
	}

	public void setOffset(RectCoordinate offset) {
		this.offset = offset;
	}

	public void setGraphics(Graphics2D graphics) {
		this.graphics = graphics;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
