---
title: 'About'
weight: 10
---

Labelset is a CLI tool and a go library for generating masses of labels for printing, in quick and simple way.

Provided features:

- instance creation from SVG-based templates:
    - [XPath](https://github.com/antchfx/xpath#supported-features) for matching text elements for replacements,
    - standard feature rich [text/template](https://pkg.go.dev/text/template) for replacement contents,
    - JSON file for instance data source.
- label tiling for printing on pre-cut tiled papers:
    - specify paper by paper size, label size, label offset and label spacing options.
