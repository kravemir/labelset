package org.kravemir.svg.labels.tool.common;

import org.kravemir.svg.labels.model.TiledPaper;
import picocli.CommandLine.Option;

public class PaperOptions {

    @Option(
            arity = "2", names = "--paper-size", paramLabel = "mm",
            description = "Width and height of the paper in mm, ie. 210 297 for A4 paper portrait"
    )
    private double[] paperSize;

    @Option(
            arity = "2", names = "--label-offset", paramLabel = "mm",
            description = "X and Y offset of the first label in mm, ie. 5 5"
    )
    private double[] labelOffset;

    @Option(
            arity = "2", names = "--label-delta", paramLabel = "mm",
            description = "X and Y delta between labels in mm, ie. 5 5"
    )
    private double[] labelDelta;

    @Option(
            arity = "2", names = "--label-size", paramLabel = "mm",
            description = "Width and height of label in mm, ie. "
    )
    private double[] labelSize;


    public double[] getPaperSize() {
        return paperSize;
    }

    public double[] getLabelOffset() {
        return labelOffset;
    }

    public double[] getLabelDelta() {
        return labelDelta;
    }

    public double[] getLabelSize() {
        return labelSize;
    }

    public TiledPaper buildPaper() {
        double paperWidth = getPaperSize()[0];
        double paperHeight = getPaperSize()[1];
        double labelOffsetX = getLabelOffset()[0];
        double labelOffsetY = getLabelOffset()[1];
        double labelDeltaX = getLabelDelta()[0];
        double labelDeltaY = getLabelDelta()[1];
        double labelWidth = getLabelSize()[0];
        double labelHeight = getLabelSize()[1];

        return TiledPaper.newBuilder()
                .setWith(paperWidth)
                .setHeight(paperHeight)
                .setTileOffsetX(labelOffsetX)
                .setTileOffsetY(labelOffsetY)
                .setTileWidth(labelWidth)
                .setTileHeight(labelHeight)
                .setTileDeltaX(labelDeltaX)
                .setTileDeltaY(labelDeltaY)
                .build();
    }
}
