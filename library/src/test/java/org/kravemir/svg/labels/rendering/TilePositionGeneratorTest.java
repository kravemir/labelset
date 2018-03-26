package org.kravemir.svg.labels.rendering;

import org.junit.Test;
import org.kravemir.svg.labels.model.TiledPaper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TilePositionGeneratorTest {

    private TilePositionGenerator generator;

    @Test
    public void test1() {
        generator = new TilePositionGenerator(TiledPaper.newBuilder()
                .setWith(14)
                .setHeight(10)
                .setTileWidth(10)
                .setTileHeight(8)
                .setTileOffsetX(2)
                .setTileOffsetY(1)
                .setTileDeltaX(0)
                .setTileDeltaY(0)
                .build()
        );

        assertThat(generator.getPaperWidth(), is(14.0));
        assertThat(generator.getPaperHeight(), is(10.0));
        assertThat(collectAllPositions(), is(new double[][] {{2,1}}));
    }

    @Test
    public void test2() {
        generator = new TilePositionGenerator(TiledPaper.newBuilder()
                .setWith(26)
                .setHeight(22)
                .setTileWidth(10)
                .setTileHeight(8)
                .setTileOffsetX(2)
                .setTileOffsetY(2)
                .setTileDeltaX(2)
                .setTileDeltaY(2)
                .build()
        );

        assertThat(generator.getPaperWidth(), is(26.0));
        assertThat(generator.getPaperHeight(), is(22.0));
        assertThat(collectAllPositions(), is(new double[][] {
                {2,2}, {14,2},
                {2,12}, {14,12},
        }));
    }

    private double[][] collectAllPositions() {
        List<double[]> positions = new ArrayList<>();

        generator.start();
        while (!generator.isFull()) {
            positions.add(new double[] { generator.getX(), generator.getY() });
            generator.nextPosition();
        }

        return positions.toArray(new double[positions.size()][]);
    }
}
