# Image Kernel Shaders vFabric
Image Kernel shader mod for Minecraft.

## Installation

Download and install the [fabric mod loader](https://fabricmc.net/use/) and the [fabric api](https://www.curseforge.com/minecraft/mc-mods/fabric-api).

Put the fabric-api `.jar` and [this mod `.jar`](https://github.com/55c3/kernel-shaders/tree/fabric) file into your minecraft mods folder.

## Usage
Use the `/kernel` command.
 * `/kernel set [none|emboss|sharpen|sobel|outline]` to use a specific kernel
 * `/kernel contrast [int]` to set the contrast level (between 0 and 255).
 * `/kernel brightness [int]` to set the brightness level (between 0 and 255).

## Examples

Best seen in game but here is a small sample.

| Source | Sharpen | Emboss | Outline |
|-------|--------|---------|------|
|![source](examples/source.png)|![Sharpen](examples/sharpen.png)|![Emboss](examples/emboss.png)|![Outline](examples/outline.png)|

* Sharpen - less blur between pixels
* Emboss - 3D effect on textures
* Outline - change of color
* Sobel - edge detection
