package org.kravemir.svg.labels.tool;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kravemir.svg.labels.TemplateResoures;
import org.kravemir.svg.labels.util.ListBuilder;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.kravemir.svg.labels.TemplateResoures.*;
import static org.kravemir.svg.labels.matcher.NodesMatchingXPath.nodesMatchingXPath;

public class ToolRunnerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File outputFile = null;

    @Before
    public void setUp() throws Exception {
        outputFile = folder.newFile("testOutput.svg");
    }

    @Test
    public void testRenderWithoutInstance() throws IOException {
        ToolRunner.main( new String[]{
                "tile",
                "--paper-size", "210", "297",
                "--label-offset", "0", "0",
                "--label-size", "65", "26.5",
                "--label-delta", "0", "0",
                TemplateResoures.TEMPLATE_01.getAsFile(folder::newFile).getAbsolutePath(),
                outputFile.getAbsolutePath()
        });
        System.out.println(FileUtils.readFileToString(outputFile));

        Document instanceDocument = RenderingUtils.parseSVG(FileUtils.readFileToString(outputFile));
        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(33),
                everyItem(allOf(TEMPLATE_01_MATCHER, not(TEMPLATE_01_DATA_01_MATCHER), not(TEMPLATE_02_MATCHER)))
        )));
    }

    @Test
    public void testRenderWithInstance() throws IOException {
        ToolRunner.main( new String[]{
                "tile",
                "--paper-size", "210", "297",
                "--label-offset", "0", "0",
                "--label-size", "65", "26.5",
                "--label-delta", "0", "0",
                "--instance-json",
                TemplateResoures.DATA_01.getAsFile(folder::newFile).getAbsolutePath(),
                "--template-descriptor",
                TemplateResoures.TEMPLATE_01_DESCRIPTOR.getAsFile(folder::newFile).getAbsolutePath(),
                TemplateResoures.TEMPLATE_01.getAsFile(folder::newFile).getAbsolutePath(),
                outputFile.getAbsolutePath()
        });
        System.out.println(FileUtils.readFileToString(outputFile));

        Document instanceDocument = RenderingUtils.parseSVG(FileUtils.readFileToString(outputFile));
        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(33),
                everyItem(allOf(TEMPLATE_01_DATA_01_MATCHER, not(TEMPLATE_01_MATCHER), not(TEMPLATE_02_MATCHER)))
        )));
    }

    @Test
    public void testRenderData01FromCSVDataSet() throws IOException {
        ToolRunner.main( new String[]{
                "tile",
                "--paper-size", "210", "297",
                "--label-offset", "0", "0",
                "--label-size", "65", "26.5",
                "--label-delta", "0", "0",
                "--dataset-csv",
                TemplateResoures.INSTANCES_CSV.getAsFile(folder::newFile).getAbsolutePath(),
                "--instance",
                "data_01",
                "--template-descriptor",
                TemplateResoures.TEMPLATE_01_DESCRIPTOR.getAsFile(folder::newFile).getAbsolutePath(),
                TemplateResoures.TEMPLATE_01.getAsFile(folder::newFile).getAbsolutePath(),
                outputFile.getAbsolutePath()
        });
        System.out.println(FileUtils.readFileToString(outputFile));

        Document instanceDocument = RenderingUtils.parseSVG(FileUtils.readFileToString(outputFile));
        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(33),
                everyItem(allOf(TEMPLATE_01_DATA_01_MATCHER, not(TEMPLATE_01_MATCHER), not(TEMPLATE_02_MATCHER)))
        )));
    }

    @Test
    public void testRenderData02FromCSVDataSet() throws IOException {
        ToolRunner.main( new String[]{
                "tile",
                "--paper-size", "210", "297",
                "--label-offset", "0", "0",
                "--label-size", "65", "26.5",
                "--label-delta", "0", "0",
                "--dataset-csv",
                TemplateResoures.INSTANCES_CSV.getAsFile(folder::newFile).getAbsolutePath(),
                "--instance",
                "data_02",
                "--template-descriptor",
                TemplateResoures.TEMPLATE_01_DESCRIPTOR.getAsFile(folder::newFile).getAbsolutePath(),
                TemplateResoures.TEMPLATE_01.getAsFile(folder::newFile).getAbsolutePath(),
                outputFile.getAbsolutePath()
        });
        System.out.println(FileUtils.readFileToString(outputFile));

        Document instanceDocument = RenderingUtils.parseSVG(FileUtils.readFileToString(outputFile));
        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(33),
                everyItem(allOf(TEMPLATE_01_DATA_02_MATCHER, not(TEMPLATE_01_MATCHER), not(TEMPLATE_02_MATCHER)))
        )));
    }

    @Test
    public void testRenderWithInstances() throws IOException {
        ToolRunner.main( new String[]{
                "tile",
                "--paper-size", "210", "297",
                "--label-offset", "0", "0",
                "--label-size", "65", "26.5",
                "--label-delta", "0", "0",
                "--instances-json",
                TemplateResoures.INSTANCES_01.getAsFile(folder::newFile).getAbsolutePath(),
                "--template-descriptor",
                TemplateResoures.TEMPLATE_01_DESCRIPTOR.getAsFile(folder::newFile).getAbsolutePath(),
                TemplateResoures.TEMPLATE_01.getAsFile(folder::newFile).getAbsolutePath(),
                outputFile.getAbsolutePath()
        });
        System.out.println(FileUtils.readFileToString(outputFile));

        Document instanceDocument = RenderingUtils.parseSVG(FileUtils.readFileToString(outputFile));
        assertThat(instanceDocument, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(33),
                Matchers.contains(
                        TEMPLATE_01_INSTANCES_01_MATCHER_LIST.subList(0, 33)
                )
        )));
    }

    @Test
    public void testRenderMultiplePagesWithInstances() throws IOException {
        ToolRunner.main( new String[]{
                "tile",
                "--paper-size", "210", "297",
                "--label-offset", "0", "0",
                "--label-size", "200", "26.5",
                "--label-delta", "0", "0",
                "--instances-json",
                TemplateResoures.INSTANCES_01.getAsFile(folder::newFile).getAbsolutePath(),
                "--template-descriptor",
                TemplateResoures.TEMPLATE_01_DESCRIPTOR.getAsFile(folder::newFile).getAbsolutePath(),
                TemplateResoures.TEMPLATE_01.getAsFile(folder::newFile).getAbsolutePath(),
                outputFile.getAbsolutePath()
        });

        Document instanceDocument0 = RenderingUtils.parseSVG(
                FileUtils.readFileToString(Paths.get(outputFile.getAbsoluteFile().getParent(), "testOutput.0.svg").toFile())
        );
        Document instanceDocument1 = RenderingUtils.parseSVG(
                FileUtils.readFileToString(Paths.get(outputFile.getAbsoluteFile().getParent(), "testOutput.1.svg").toFile())
        );
        Document instanceDocument2 = RenderingUtils.parseSVG(
                FileUtils.readFileToString(Paths.get(outputFile.getAbsoluteFile().getParent(), "testOutput.2.svg").toFile())
        );

        assertThat(instanceDocument0, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(11),
                Matchers.contains( new ListBuilder<Matcher<? super Node>>()
                        // TODO: refactor these matchers
                        .add(1, allOf(
                                nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='Test name']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*[2][not(text())]", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2)),
                                nodesMatchingXPath( ".//*[@id='text4540']/*[text()='Test description']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='text4544']/*[text()='19. 05. 2018']", hasSize(1))
                        ))
                        .add(1, allOf(
                                nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='Test name 2']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*[2][not(text())]", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2)),
                                nodesMatchingXPath( ".//*[@id='text4540']/*[text()='Test description 2']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='text4544']/*[text()='15. 07. 2018']", hasSize(1))
                        ))
                        .add(9, allOf(
                                nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='Test rest']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*[2][not(text())]", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2)),
                                nodesMatchingXPath( ".//*[@id='text4540']/*[text()='Fill the rest of the page']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='text4544']/*[text()='15. 07. 2018']", hasSize(1))
                        ))
                        .build()
                )
        )));

        assertThat(instanceDocument1, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(11),
                Matchers.contains( new ListBuilder<Matcher<? super Node>>()
                        .add(11, allOf(
                                nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='Test rest']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*[2][not(text())]", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2)),
                                nodesMatchingXPath( ".//*[@id='text4540']/*[text()='Fill the rest of the page']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='text4544']/*[text()='15. 07. 2018']", hasSize(1))
                        ))
                        .build()
                )
        )));

        assertThat(instanceDocument2, nodesMatchingXPath("/*/*", Matchers.<Collection<Node>>allOf(
                hasSize(11),
                Matchers.contains( new ListBuilder<Matcher<? super Node>>()
                        .add(11, allOf(
                                nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='Test rest']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*[2][not(text())]", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2)),
                                nodesMatchingXPath( ".//*[@id='text4540']/*[text()='Fill the rest of the page']", hasSize(1)),
                                nodesMatchingXPath( ".//*[@id='text4544']/*[text()='15. 07. 2018']", hasSize(1))
                        ))
                        .build()
                )
        )));
    }
}
