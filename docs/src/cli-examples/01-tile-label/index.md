# Example 01: tile label

The example shows creation of portrait A4 document with labels with 100 width and 30mm height:

Assumptions:

* `lablie` is installed (if not, see README),
* `inkscape` is installed,
* `label.svg` is present in current working directory (download [label.svg](label.svg)).

Then, by invoking following commands:

```bash
lablie tile --paper-size 210 297 \
                --label-offset 5 13.5 \
                --label-size 100 30 \
                --label-delta 0 0 \
                label.svg \
                label-tiled.svg

inkscape --file=label-tiled.svg --without-gui --export-png=label-tiled.png
inkscape --file=label-tiled.svg --without-gui --export-pdf=label-tiled.pdf
```

Following result files should have been created:

* `label-tiled.svg` containing tiled label on specified paper,
* `label-tiled.png` containing `label-tiled.svg` converted to PNG,
* `label-tiled.pdf` containing `label-tiled.svg` converted to PDF.
