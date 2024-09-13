# Endless Sky Map Generator

For a little over a year now, I have been maintaining a map of Endless Sky. When I was looking ahead at the upcoming 0.10.9 release and saw a new faction I didn't have room for on my legend, I had the bright idea to (instead of spending half an hour fixing up the legend) go spend 15 to 20 hours making my own mapping tool.  

This mapping tool is not intended to compete with the "consumer market" of easy-to-use map viewers with GUIs, nor is functionality for adding or modifying map data something I plan to add. If you want the former, I recommend Darcy Manoel's [online map viewer](https://darcymanoel.github.io/Endless-Sky-Map-Viewer/). If you want the latter, quyykk's [plugin editor](https://github.com/quyykk/plugin-editor) is probably the way to go, although it's a little buggy and outdated.

_This_ mapping tool is intended to do something very different: take a whole bunch of files as input, apply events and filters, use them to paint customizable maps, and then combine text, maps, legends, and other images into one composite. In short, it's intended to take the work of doing dozens of screen shots, stitching them together, and post processing, and reduce it to a single command.

## Building

I've attached a jar file with the 0.1.0 release, but I can't guarantee that it's going to work for everyone, and it won't get updated nearly as often as the repo as a whole. Therefore, the best way to use this tool is probably to build it yourself. I'll presume that you know how to clone a repo; if not, you can just click the big green button that says "Code," and download the zip.

In general, building your own copy of the code is pretty simple. There are no external dependencies (well, other than umldoclet, but you don't need that to run the program). All you need is a relatively modern [JDK](https://www.oracle.com/ca-en/java/technologies/downloads/) and [Ant](https://ant.apache.org/).

Once you have Ant installed, navigate to the directory that the jar is in, and build with:
```bash
ant jar
```

Congrats! You now have a hopefully—working copy of the map creator.

## Getting Started

Remember what I said about GUIs up above? Well, this program doesn't have one. The jar you've built probably won't open if you double click on it, and if it does, it'll just open a CLI and then start spewing errors.

Instead of a GUI, the map creator works by putting all of the options inside a file (let's call it `example.txt`). If `example.txt` is in the same place as your jar, you run the program from the command line with:
```bash
java -jar "ES Mapper.jar" example.txt
```

## Adding to the Generator File

At this point, just running the command above won't work, because `example.txt` is an empty file. Thankfully, that can be fixed with the aid of any text editor, and a little bit of knowledge about the Endless Sky data file syntax (the syntax for the generator file is the same).

Generator files can contain the following items:
- A list of other config files this file `extends`
- A reference to the `game directory` used to draw the map (all generator files must have one, which should at minimum have color definitions in it)
- One or more `plugin directory`s containing excess content (completely optional)
- One or more `viewport`s containing images that get rendered to files (technically optional, but nothing will happen if you don't have any)
- One or more `map`s that get drawn (like `viewport`s, you don't _need_ them, but if you don't have any you're basically working with a stripped-down, painfully slow version of GIMP)
- One or more `legend`s that are programatically generated guides to a specific color scheme (optional)
- One or more `event list`s (optional)

And that's it! That's everything that can go into a generator file. Simple? Well, maybe, but most of these nodes can have a lot more stuff defined in them, so let's go over each of them in more detail.

### Extending Other Files

```html
extends <path> ...
```

Starting in version 0.2.0, you can optionally specify one or more config files that your file builds off of by using one or more `extends` nodes. Every argument passed to an `extends` node in treated as a filepath relative to the directory that the jar is in, and any file exactly matching that name will be loaded as a config file. Be very careful to avoid circular dependencies, as there is no built-in support to check for such cases yet.

### Directories

```html
"game directory" <path>
"plugin directory" <path>
```

These two lines are the simplest and easiest way to drastically change the contents of your map. Each should point to the root directory of either Endless Sky itself, or a plugin. This directory should contain subdirectories like `data`, `images`, and so forth.

The `game directory` is the basic underlying layer. Even if you choose to only draw plugin content on a map, you **still need** to define a `game directory`. This doesn't necessarily need to be a vanilla copy of ES, or even a working copy at all, but it should still contain the following:
- Any image assets you want to use (such as the Milky Way galaxy background), in `images`
- Color definitions for the colors `map name`, `map link`, `map wormhole`, and `message log importance high`
- A definition for the government `Uninhabited`

On top of the `game directory`, you can define as many `plugin directory`s as you want. Image paths will be resolved in reverse order, so the last plugin you define will be the first place the generator looks for images (and the `game directory` will be the last). I can't guarantee that conflicts between data will be resolved nearly as well, so map conflicting plugins at your own risk.

### Viewports

```html
viewport <name>
	size <width> <height>
	"file format" <extension>
	map <name> <x> <y>
	image <name> <x> <y>
	legend <name> <y>
	line <x1> <y1> <x2> <y2>
		color {color}
	oval <x> <y> <width> <height>
		color {color}
	rect <x> <y> <width> <height>
		color {color}
	text <text> <x> <y>
		color {color}
```

There's a lot that can go into a viewport, and you probably won't use most of it. If you want to, though, you should note that X and Y are measured from the top left of the screen to the top left of any object you're drawing (even an oval). The exception is text, where the Y coordinate is the distance from the top of the screen to the baseline (approximately the bottom of the text).

```html
size <width> <height>
```

The size of the viewport, in pixels. The width and height can be any real numbers greater than 0, but they will naturally be rounded to integers before being exported.

```html
"file format" <extension>
```

The file format that you would like to receive the final images in. Should be one of `png`, `jpeg`, `bmp`, `webmp`, or `gif`.

```html
map <name> <x> <y>
```

Draws a map at the desired coordinates. See the [Maps](#maps) section for more mapping options. Unless you're doing something really weird with the engine, you should always define at least one map for a viewport, but you can define as many as you want. Keep in mind that all maps are drawn with a black background, so they're unlikely to layer well.

```html
image <name> <x> <y>
```

Draws an image at the desired coordinates. The image should be relative to the `images` directory in either your `game directory` or one of your `plugin directory`s. The path should be formatted `dir/myimage`, without an extension. You can include as many images as you want, or none at all.

```html
legend <name> <y>
```

Draws a legend at the desired coordinates. See the [Legends](#legends) section for more legend-building options. Note that, because legends are anchored to either the left or right side of a viewport, you can only define a y-position for them.

```html
line <x1> <y1> <x2> <y2>
	color {color}
```

Draws a line from the point (`x1`, `y1`) to the point (`x2`, `y2`) in the desired color. See the [Color](#color) section for more details about colors. Currently, there is no way to customize the stroke or any other options regarding lines.

```html
oval <x> <y> <width> <height>
	color {color}
```

Draws an outlined oval at the desired location, color, and size. Note that `x` and `y` **do not** specify the origin (center) of the oval, but rather the position of the top left corner. See the [Color](#color) section for more details about colors. Currently, there is neither a way to make filled ovals, nor any other options regarding them.

```html
rect <x> <y> <width> <height>
	color {color}
```

Draws the outline of a rectangle starting from (`x`, `y`), and extending left and down according to `width` and `height`. See the [Color](#color) section for more details about colors. Currently, there is neither a way to make filled rectangles, nor any other options regarding them.

```html
text <text> <x> <y>
	color {color}
```

Draws some text with the baseline positioned at (`x`, `y`). See the [Color](#color) section for more details about colors. Currently, all text is drawn in the Ubuntu font at size 14, and there are no other customization options.

### Maps

```html
map <name>
	size <width> <height>
	center <x> <y>
	paint {painting mode}
	"plugins only"
	"include hidden"
	"include unmappable wormholes"
	"paint uninhabited"
	"event list" <name>
	"event" <event>
```

Maps are the backbone of the map generator, but they tend to actually have shorter definitions than viewports. There's not many options for maps, but the options you do have tend to be extremely powerful.

```html
size <width> <height>
```

The size of the map, in pixels. Usually, this is the same as your viewport size, but you can also use it to make smaller regional maps for insets.

```html
center <x> <y>
```

The Endless Sky game coordinates that correspond to the center of this map. For example, if you want to center your map on Sag A*, you'd use `center 112 22`.

```html
paint {painting mode}
```

What to paint the map according to. For details, see the [Painting Mode](#painting-mode) section.

```html
"plugins only"
```

This standalone keyword takes no arguments. When it is used, only the content from plugin definitions are used—your `game directory` is ignored. Importantly, though, events and colors will still be loaded from the `game directory` as well as any `plugin directory`s. This mode is intended to allow you to highlight a plugin map's content, not to work seamlessly with total overhaul plugins (although that may come later as a feature).

```html
"include hidden"
```

By default, systems marked as `hidden` are not included on the map, nor are their hyperlinks or any wormholes they contain. Using `include hidden` enables them, letting you map fancy spoiler content to your heart's content.

```html
"include unmappable wormholes"
```
By default, two kinds of wormholes are excluded from the map: wormholes that are lacking the `mappable` property (like the Pug wormhole), and wormholes that are not attached to any planet in their system (like the Eye). If you use `include unmappable wormholes`, both these types of wormhole will be mapped.

```html
"paint uninhabited"
```

By default, systems that are uninhabited are painted in the classic Uninhabited Grey (or whatever other color you've assigned the `Uninhabited` government in your `game directory`). If you want to apply the map painting to uninhabited systems as well, you can use `paint uninhabited` to do so.

Systems are considered uninhabited if:
1. There are no named stellar objects in their definition, or
2. Every named stellar object in their definition is either a wormhole or has the `uninhabited` attribute.

Inhabited systems with the `Uninhabited` government are *not* considered uninhabited by the map generator.

```html
"event list" <list>
event <name>
```

Other than painting, the largest way in which maps can be customized is by choosing which events to apply to them. Applying events acts just like in-game, changing systems, adding and removing hyperlinks, and so on. Events can either be applied individually (through `event`), or by using an `event list` (see [Event Lists](#event-lists) for more). All events are applied in the order they are defined.

### Legends

```html
legend <name>
	align (left | right)
	header <text>
	item <text> {color}
```

Legends allow you to provide a key to all the colors on your map. They are probably the biggest reason I built this tool, because the background image is entirely automated: all you have to do is add headers and items, and the legend resizes as necessary.

```html
align (left | right)
```

Which side of a viewport this legend is stuck on. This will determine which side has fancy graphical elements and which side doesn't. If no alignment is specified, the default is `left`.

```html
header <text>
```

Adds a light-grey header to the legend, with no corresponding color assigned.

```html
item <text> {color}
```

Adds a medium-grey entry to the legend, and then draws a ring beside it using the selected color. The ring is practically identical to those drawn on a map.

### Event Lists
```html
"event list" <name>
	<event>
```

Event lists are far simpler than viewports, maps, or legends. All they do is serve as a way to combine a whole bunch of events that you're planning to reuse for multiple maps, so you don't have to type out, say

```html
	"label coalition space"
	"gegno space label"
	"label hai space"
	"label incipias space"
	"graveyard label"
	"label korath space"
	"ember waste label"
	"umbral reach label"
	"label wanderer space"
```

four or five times in a row. By the way, you can copy and paste that code block into an event list, and instantly get all vanilla alien labels drawn on your map!

### Wait a minute, what do all these curly braces mean?

I'm so glad you asked! I've included curly braces in the documentation here because I'm lazy: I don't really want to type out multiple lines of repetitive text, when one line will do as a summary. The next couple of sections contain a guide to what all the different brace-enclosed words actually mean.

#### {Color}

There three different ways colors are defined in ES, and any of the three can be used with this generator. Thus, when you see `color {color}`, it is an abbreviation for:

```html
color <r> <g> <b>
color <r> <g> <b> <a>
color <name>
```

If you include three arguments, your color is matched to an opaque RGB color. Including a fourth lets you define the alpha (transparency). Each argument should be a real number no lower than 0 and no higher than 1.

Alternatively, you can use the name of a predefined color from your copy of Endless Sky. Note that some ES colors, like `bright`, actually have an alpha value of 0, and therefore won't render properly in this generator.

If no color is defined, the default is (usually) pure white.

#### {Sprite}

Sprite definitions are a little bit complicated, and likely to get more so as more features are added. Thus, when you see `sprite {sprite}`, it is an abbreviation for:

```html
sprite <path>
	scale <scale>
```

The path should be a valid path, relative to the `images` directory of either your `game directory` or one of your `plugin directory`s. It should follow standard Endless Sky conventions, not including a file extension or the suffixes used for denoting scale, animations, or blending modes.

`scale` is a completely optional node, which allows you to scale sprites you load. The `scale` of a sprite is a real number relative to its original size. You can try using negative numbers if you wish, but expect weird behaviour.

#### {Painting Mode}

Painting modes are given braces not because they're all that complicated (only one or two arguments on a single line), but because as new ones are added it would be impractical to bloat that line of a definition with all your options.

Right now, the following painting modes are allowed:

- `government` This is the default painting mode. It gives each system a color according to the color of the government that controls it.
- `trade <commodity>` Paints the map according to the price of a selected commodity relative to its game defined minimum and maximum typical values.

## Concluding Note

If you've gotten this far, you've hopefully figured out how to use the map generator. If you haven't figured out, please don't hesitate to open a discussion, ping me on the [ESC Discord](https://discord.gg/ZeuASSx), or spam me with messenger pigeons. The generator's still a pretty heavy work in progress, so any help you can offer (even if it's just telling me about this weird error you got, or asking for a new painting mode) is huge! If you want to do some coding for this project, please contact me before you open a PR, as my coding style is somewhat eclectic, and the codebase for this project is a mess anyways.
