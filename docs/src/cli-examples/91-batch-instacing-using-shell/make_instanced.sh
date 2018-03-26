#!/usr/bin/env bash

for i in product_*.json;
do
    KEY=$(echo "$i" | sed -r 's/[^_]*_([^.]*)\..*/\1/')
    lablie tile --paper-size 210 297 --label-offset 5 13.5 --label-size 100 30 --label-delta 0 0 label.svg --instance-json $i "label-tiled-${KEY}.svg"
    inkscape --file="label-tiled-${KEY}.svg" --without-gui --export-pdf="label-tiled-${KEY}.pdf"
done
