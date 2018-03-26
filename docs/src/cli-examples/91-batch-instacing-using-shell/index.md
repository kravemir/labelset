# Example 91: batch instancing using shell script

* `lablie` is installed (if not, see README),
* `inkscape` is installed,
* `make_instances.sh` is present in current working directory (download [make_instances.sh](make_instances.sh)),
* `label.svg` is present in current working directory (download [label.svg](label.svg)),
* `product_honey.json` is present in current working directory (download [product_honey.json](product_honey.json)),
* `product_oranges.json` is present in current working directory (download [product_oranges.json](product_oranges.json)).

Then, by invoking following commands:

```bash
./make_instances.sh
```

Following result files should have been created:

* `label-tiled-honey.svg` containing tiled label with `product_honey.json` instance data,
* `label-tiled-oranges.svg` containing tiled label with `product_oranges.json` instance data,
* `label-tiled-honey.pdf` containing `label-tiled-honey.svg` converted to PDF,
* `label-tiled-oranges.pdf` containing `label-tiled-oranges.svg` converted to PDF.
