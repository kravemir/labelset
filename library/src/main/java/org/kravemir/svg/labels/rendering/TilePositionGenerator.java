package org.kravemir.svg.labels.rendering;

import org.kravemir.svg.labels.annotations.ToBePublicApi;
import org.kravemir.svg.labels.model.TiledPaper;

public class TilePositionGenerator {
    private final TiledPaper paper;

    private double x, y;
    private boolean full = true;

    public TilePositionGenerator(TiledPaper paper) {
        this.paper = paper;
    }

    public void start(){
        full = false;
        x = paper.getTileOffsetX();
        y = paper.getTileOffsetY();
    }

    public void nextPosition(){
        if(isFull()) return;

        x += paper.getTileWidth() + paper.getTileDeltaX();
        if (x > paper.getWith() - paper.getTileWidth()) {
            x = paper.getTileOffsetX();
            y += paper.getTileHeight() + paper.getTileDeltaY();

            if (y > paper.getHeight() - paper.getTileHeight()) {
                full = true;
            }
        }
    }

    public boolean isFull() {
        return full;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getPaperWidth() {
        return paper.getWith();
    }

    public double getPaperHeight() {
        return paper.getHeight();
    }

    @ToBePublicApi
    public double getTileWidth() {
        return paper.getTileWidth();
    }

    @ToBePublicApi
    public double getTileHeight() {
        return paper.getTileHeight();
    }
}
