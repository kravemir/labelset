---
title: 'Guide: tile label with instancing'
linkTitle: tile label with instancing
summary: The guide shows creation of instanced label portrait A4 documents with labels with 100mm width and 30mm height.
weight: 02
---

# {{< param title >}}

{{< param summary >}}

{{< figure src="label.svg" title="label.svg" class="guide-thumb" >}}

## Prerequisites

Installed software:

* `labelset` is installed (see [install](/install) docs),
* `inkscape` is installed.

## Create source files

In this guide you'll create following resources in current directory:

- `label.svg` - SVG template for label (usually provided by yours artist, or download [label.svg](label.svg)),
- `label.labelset.svg` - template descriptor specifying how to fill SVG template,
- `product_honey.json` - instance data file in JSON,
- `product_oranges.json` - instance data file in JSON.

### SVG template and descriptor

For SVG template `label.svg` create descriptor `label.labelset.svg`containing:

* definition of attributes, `name` and `extra`, which are passed instantiation of template,
* rules to find elements, whose contents should be replaced.

Contents of the `label.labelset.svg` should look like:

{{< code file="label.labelset.json" language="json" >}}

###  Instance data files

Create  `product_honey.json` instance data file with information about product :

{{< code file="product_honey.json" language="json" >}}

Similarly `product_oranges.json`:

{{< code file="product_oranges.json" language="json" >}}

## Run build (generation)

With prepared source files, generate outputs using following commands:

```bash
labelset tile --paper-size "210x297" --label-offset "5,13.5" --label-size "100x30" --label-spacing "0,0" label.svg --instance-json product_oranges.json label-tiled-oranges.svg
labelset tile --paper-size "210x297" --label-offset "5,13.5" --label-size "100x30" --label-spacing "0,0" label.svg --instance-json product_honey.json label-tiled-honey.svg
inkscape "label-tiled-honey.svg" --export-type="pdf" --export-filename="label-tiled-honey.pdf"
inkscape "label-tiled-oranges.svg" --export-type="pdf" --export-filename="label-tiled-oranges.pdf"
```

## Outputs

Following result files should have been created:

* `label-tiled-honey.svg` containing tiled label generated from `product_honey.json` instance data,
* `label-tiled-oranges.svg` containing tiled label generated from `product_oranges.json` instance data,
* `label-tiled-honey.pdf` containing `label-tiled-honey.svg` converted to PDF,
* `label-tiled-oranges.pdf` containing `label-tiled-oranges.svg` converted to PDF.

The tiled documents should look like this:

<div class="thumb-gallery">
{{< figure src="label-tiled-honey.svg" title="label-tiled-honey.svg" class="guide-thumb" >}}
{{< figure src="label-tiled-oranges.svg" title="label-tiled-oranges.svg" class="guide-thumb" >}}
</div>
