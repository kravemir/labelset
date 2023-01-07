---
title: labelset
---

# labelset

_Print labels generation_

## About

Labelset is a CLI tool and a go library for generating masses of labels for printing, in quick and simple way.

Provided features:

- instance creation from SVG-based templates:
    - [XPath](https://github.com/antchfx/xpath#supported-features) for matching text elements for replacements,
    - standard feature rich [text/template](https://pkg.go.dev/text/template) for replacement contents,
    - JSON file for instance data source.
- label tiling for printing on pre-cut tiled papers:
    - specify paper by paper size, label size, label offset and label spacing options.

## Install

See [install](install) docs.

## Usage

Example command:

```shell
labelset tile \
  --paper-size "210x297" \
  --label-offset "8,21.2" \
  --label-size "48.5x25.4" \
  --label-spacing "0,0" \
  "single-label.svg" \
  "tiled-output.svg"
```

See [guides](/guides) for more usage examples.

## Examples

{{< hint info >}}
Example output to be prepared
{{< /hint >}}

## Screenshoots

{{< hint info >}}
GUI version doesn't yet exist.
{{< /hint >}}

## License

The project is licensed under Apache License, which allows proprietary use. See `LICENSE` file for more details.
