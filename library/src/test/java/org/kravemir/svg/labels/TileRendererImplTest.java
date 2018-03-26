package org.kravemir.svg.labels;

import com.fasterxml.jackson.databind.ObjectMapper;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kravemir.svg.labels.model.DocumentRenderOptions;
import org.kravemir.svg.labels.model.LabelGroup;
import org.kravemir.svg.labels.model.TiledPaper;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.kravemir.svg.labels.TemplateResoures.*;
import static org.kravemir.svg.labels.matcher.NodesMatchingXPath.nodesMatchingXPath;

@RunWith(JUnitParamsRunner.class)
public class TileRendererImplTest {

    private ObjectMapper mapper;
    private TileRendererImpl renderer;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        renderer = new TileRendererImpl();
    }

    @Test
    @Parameters
    public void testSimpleFullPageInstancing(int rows, int columns) {
        int expectedCount = rows * columns;

        TiledPaper paper = createPaper(rows, columns);

        String renderedInstance = renderer.renderSinglePageWithLabel(
                paper,
                TEMPLATE_01.get()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstance);

        System.out.println(renderedInstance);

        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                hasSize(expectedCount),
                everyItem(TEMPLATE_01_MATCHER)
        )));
    }

    private Object[] parametersForTestSimpleFullPageInstancing() {
        return new Object[][]{
                {1, 1},
                {2, 1},
                {1, 2},
                {9, 9}
        };
    }

    @Test
    @Parameters
    public void testFillPage(int rows, int columns) {
        int expectedCount = rows * columns;

        TiledPaper paper = createPaper(rows, columns);

        List<String> renderedInstances = renderer.render(
                paper,
                Collections.singletonList(
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .addInstance(
                                        LabelGroup.Instance.newBuilder()
                                                .setCount(0)
                                                .setFillPage(true)
                                                .build()
                                )
                                .build()
                ),
                DocumentRenderOptions.newBuilder().build()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstances.get(0));

        assertThat(renderedInstances, hasSize(1));
        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                hasSize(expectedCount),
                everyItem(TEMPLATE_01_MATCHER)
        )));
    }

    private Object[] parametersForTestFillPage() {
        return new Object[][]{
                {1, 1},
                {2, 1},
                {1, 2},
                {9, 9}
        };
    }

    @Test
    @Parameters
    public void testVariousPageFill(int rows, int columns, int count) {
        TiledPaper paper = createPaper(rows, columns);

        List<String> renderResult = renderer.render(
                paper,
                Collections.singletonList(
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setCount(count).build())
                                .build()
                ),
                DocumentRenderOptions.newBuilder().build()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderResult.get(0));

        System.out.println(renderResult);

        assertThat(renderResult, hasSize(1));
        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                hasSize(count),
                everyItem(TEMPLATE_01_MATCHER)
        )));
    }

    private Object[] parametersForTestVariousPageFill() {
        return new Object[][]{
                {1, 1, 1},
                {2, 1, 1},
                {2, 1, 2},
                {1, 2, 1},
                {1, 2, 2},
                {9, 9, 1},
                {9, 9, 2},
                {9, 9, 3},
                {9, 9, 9},
                {9, 9, 79},
                {9, 9, 80},
                {9, 9, 81}
        };
    }

    // TODO: add test for rendering of multiple pages

    @Test
    public void testMultipleLabels() {
        int rows = 2, columns = 4;
        int expectedCount = rows * columns;

        TiledPaper paper = createPaper(rows, columns);

        List<String> renderedInstance = renderer.render(
                paper,
                Arrays.asList(
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setCount(1).build())
                                .build(),
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_02.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setFillPage(true).build())
                                .build()

                ),
                DocumentRenderOptions.newBuilder().build()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstance.get(0));

        System.out.println(renderedInstance);

        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                hasSize(expectedCount),
                containsConcat(
                        repeat(1, allOf(TEMPLATE_01_MATCHER, not(TEMPLATE_02_MATCHER))),
                        repeat(expectedCount - 1, allOf(TEMPLATE_02_MATCHER, not(TEMPLATE_01_MATCHER)))
                )
        )));
    }

    @Test
    public void testMultipleLabelsWithReplacement() {
        int rows = 2, columns = 4;
        int expectedCount = rows * columns;

        TiledPaper paper = createPaper(rows, columns);

        List<String> renderedInstance = renderer.render(
                paper,
                Arrays.asList(
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setCount(1).build())
                                .build(),
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .setTemplateDescriptor(TEMPLATE_01_DESCRIPTOR.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setInstanceContent(DATA_01.get()).setCount(1).build())
                                .build(),
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .setTemplateDescriptor(TEMPLATE_01_DESCRIPTOR.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setInstanceContent(DATA_02.get()).setCount(1).build())
                                .build(),
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_02.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setFillPage(true).build())
                                .build()

                ),
                DocumentRenderOptions.newBuilder().build()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstance.get(0));

        System.out.println(renderedInstance);

        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                hasSize(expectedCount),
                containsConcat(
                        repeat(1, allOf(TEMPLATE_01_MATCHER, not(TEMPLATE_02_MATCHER))),
                        repeat(1, allOf(TEMPLATE_01_DATA_01_MATCHER, not(TEMPLATE_01_MATCHER), not(TEMPLATE_02_MATCHER))),
                        repeat(1, allOf(TEMPLATE_01_DATA_02_MATCHER, not(TEMPLATE_01_MATCHER), not(TEMPLATE_02_MATCHER))),
                        repeat(expectedCount - 3, allOf(TEMPLATE_02_MATCHER, not(TEMPLATE_01_MATCHER)))
                )
        )));
    }

    @Test
    public void testMultipleLabelsWithReplacement2() {
        int rows = 2, columns = 4;
        int expectedCount = rows * columns;

        TiledPaper paper = createPaper(rows, columns);

        List<String> renderedInstance = renderer.render(
                paper,
                Arrays.asList(
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setCount(1).build())
                                .build(),
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_01.get())
                                .setTemplateDescriptor(TEMPLATE_01_DESCRIPTOR.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setInstanceContent(DATA_01.get()).setCount(1).build())
                                .addInstance(LabelGroup.Instance.newBuilder().setInstanceContent(DATA_02.get()).setCount(1).build())
                                .build(),
                        LabelGroup.newBuilder()
                                .setTemplate(TEMPLATE_02.get())
                                .addInstance(LabelGroup.Instance.newBuilder().setFillPage(true).build())
                                .build()

                ),
                DocumentRenderOptions.newBuilder().build()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstance.get(0));

        System.out.println(renderedInstance);

        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                hasSize(expectedCount),
                containsConcat(
                        repeat(1, allOf(TEMPLATE_01_MATCHER, not(TEMPLATE_02_MATCHER))),
                        repeat(1, allOf(TEMPLATE_01_DATA_01_MATCHER, not(TEMPLATE_01_MATCHER), not(TEMPLATE_02_MATCHER))),
                        repeat(1, allOf(TEMPLATE_01_DATA_02_MATCHER, not(TEMPLATE_01_MATCHER), not(TEMPLATE_02_MATCHER))),
                        repeat(expectedCount - 3, allOf(TEMPLATE_02_MATCHER, not(TEMPLATE_01_MATCHER)))
                )
        )));
    }

    private <T> Matcher<Iterable<? extends T>> containsConcat(Stream<Matcher<? super T>>... matchers) {
        List<Matcher<? super T>> matcherList = Stream
                .of(matchers)
                .flatMap(Function.identity())
                .collect(Collectors.toList());
        return contains(matcherList);
    }

    private <T> Stream<Matcher<? super T>> repeat(int times, Matcher<? super T> element) {
        return IntStream.range(0, times).mapToObj(i -> element);
    }

    private TiledPaper createPaper(int rows, int column) {
        final int instanceWidth = 80;
        final int instanceHeight = 80;

        return TiledPaper.newBuilder()
                .setWith(20 + instanceWidth * column)
                .setHeight(20 + instanceHeight * rows)
                .setTileWidth(instanceWidth)
                .setTileHeight(80)
                .setTileOffsetX(10)
                .setTileOffsetY(10)
                .setTileDeltaX(0)
                .setTileDeltaY(0)
                .build();
    }

}
