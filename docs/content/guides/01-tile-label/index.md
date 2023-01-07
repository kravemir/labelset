---
title: 'Guide: tile label'
linkTitle: tile label
summary: The guide shows creation of portrait A4 PDF document with labels with 100mm width and 30mm height
weight: 01
---

# {{< param title >}}

{{< param summary >}}

{{< figure src="label.svg" title="label.svg" class="guide-thumb" >}}

## Prerequisites

Installed software:

* `labelset` is installed (see [install](/install) docs),
* `inkscape` is installed,

## SVG template

The current directory should contain:

* `label.svg` - SVG template to be tiled (download [label.svg](label.svg)).

## Execution

Then, by invoking following commands:

```bash
labelset tile --paper-size "210x297" \
              --label-offset "5,13.5" \
              --label-size "100x30" \
              --label-spacing "0,0" \
              label.svg \
              label-tiled.svg

inkscape "label-tiled.svg" --export-type="pdf" --export-filename="label-tiled.pdf"
inkscape "label-tiled.svg" --export-type="png" --export-filename="label-tiled.png"
```

## Output

Following result files should have been created:

* `label-tiled.svg` containing tiled label on specified paper,
* `label-tiled.png` containing `label-tiled.svg` converted to PNG,
* `label-tiled.pdf` containing `label-tiled.svg` converted to PDF.

The tiled document should look like this:

{{< figure src="label-tiled.svg" title="label-tiled.svg" class="guide-thumb" >}}
