# Example 02: tile label with instancing

The example shows creation of instanced label portrait A4 documents with labels with 100 width and 30mm height.

Assumptions:

* `lablie` is installed (if not, see README),
* `inkscape` is installed.

Resulting example consists of following source files:

| File                                             | Description                                                  |
| ------------------------------------------------ | ------------------------------------------------------------ |
| `label.svg`                                      | SVG template for label (usually provided by yours artist, or download [label.svg](label.svg)) |
| `label.lablie.svg`                               | Template descriptor containing information how to fill SVG template |
| `product_honey.json`<br />`product_oranges.json` | Instance data files in JSON                                  |

## Create source files

For SVG template `label.svg` create descriptor `label.lablie.svg`containing:

* definition of attributes, `name` and `extra`, which are passed instantiation of template,
* rules to find elements, whose contents should be replaced.

Contents of the `label.lablie.svg` should look like:

```json
{{#include label.lablie.json}}
```

Create  `product_honey.json` instance data file with information about product :

```json
{{#include product_honey.json}}
```

Similarly `product_oranges.json`:

```json
{{#include product_oranges.json}}
```



## Run build (generation)

With prepared source files, generate outputs using following commands:

```bash
lablie tile --paper-size 210 297 --label-offset 5 13.5 --label-size 100 30 --label-delta 0 0 label.svg --instance-json product_honey.json label-tiled-honey.svg
lablie tile --paper-size 210 297 --label-offset 5 13.5 --label-size 100 30 --label-delta 0 0 label.svg --instance-json product_oranges.json label-tiled-oranges.svg
inkscape --file=label-tiled-honey.svg --without-gui --export-pdf=label-tiled-honey.pdf
inkscape --file=label-tiled-oranges.svg --without-gui --export-pdf=label-tiled-oranges.pdf
```

Following result files should have been created:

* `label-tiled-honey.svg` containing tiled label with `product_honey.json` instance data,
* `label-tiled-oranges.svg` containing tiled label with `product_oranges.json` instance data,
* `label-tiled-honey.pdf` containing `label-tiled-honey.svg` converted to PDF,
* `label-tiled-oranges.pdf` containing `label-tiled-oranges.svg` converted to PDF.
