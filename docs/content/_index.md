---
title: labelset
---

# labelset

_Print labels generation_

## About

{{< include-page-content page="/about" >}}

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

## Guides

See [guides](/guides) for examples how to use the CLI interface.

## Example

Example tiled outputs:

<div class="thumb-gallery">
{{< figure src="guides/01-tile-label/label-tiled.svg" title="Tiled - without instancing" class="guide-thumb" >}}
{{< figure src="guides/02-tile-label-with-instancing/label-tiled-honey.svg" title="Tiled - Honey" class="guide-thumb" >}}
{{< figure src="guides/02-tile-label-with-instancing/label-tiled-oranges.svg" title="Tiled - Oranges" class="guide-thumb" >}}
</div>

## Screenshoots

{{< hint info >}}
GUI version doesn't yet exist.
{{< /hint >}}

## License

The project is licensed under Apache License, which allows proprietary use. See `LICENSE` file for more details.
