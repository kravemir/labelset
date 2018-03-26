# Example 11: project

The example shows creation of project to generate multiple outputs from a products dataset.

Assumptions:

* `lablie` is installed (if not, see README),
* `inkscape` is installed,

The resulting project will contain following source files:

| File                                                         | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `src/templates/product.svg`                                    | SVG template for label (usually provided by yours artist)    |
| `src/templates/product.lablie.json`                            | Template descriptor containing information how to fill the SVG template |
| `src/products/honey.json`<br />`src/products/oranges.json` <br />`src/products/...` | Instance data files in JSON                                  |
| `project.lablie`                                             | Project definition                                           |

## Create project files

Create `project.lablie` file containing project definition:

* datasets **`products`**: JSON collection of instances,
* outputset **`products`**, which generates:
  * from template `"src/templates/product.svg"`,
  * from dataset `"products"` (doesn't need to be same name),
  * for paper with dimensions 210x297 mm (A4 portrait), tiles with dimensions 100x30mm offsetted by  5x13.5mm.

Contents of the `project.lablie` should look like:

```json
{{#include project.lablie}}
```

From [previous example][02-tile-label-with-instancing] are reused following files:

* `src/templates/product.lablie.json` - template descriptor,
* `src/products/*` - instance data files.

## Run build (generation)

Project feature uses `make` for build. The `Makefile` is generated with:

```bash
lablie project project.lablie generate-makefile
```

Now, outputs can be generated using:

```bash
make
```



## See results

The `output /` folder is created within current directory, containing:

| File                                                         | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `output/products/oranges.pdf`<br />`output/products/honey.pdf` | PDF containing tiled label for each instance using specified paper |
| `output/products/oranges.svg`<br />`output/products/honey.svg` | SVG containing tiled label for each instance using specified paper |


[02-tile-label-with-instancing]: ../02-tile-label-with-instancing
