package org.kravemir.svg.labels.tool;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kravemir.svg.labels.TemplateResoures;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.kravemir.svg.labels.TemplateResoures.*;
import static org.kravemir.svg.labels.matcher.NodesMatchingXPath.nodesMatchingXPath;

public class ToolRunnerRefferingTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File outputFile = null;

    @Before
    public void setUp() throws Exception {
        outputFile = folder.newFile("testOutput");
    }

    @Test
    public void testRenderWithReferrerInstances() throws IOException {
        ToolRunner.main( new String[]{
                "tile",
                "--paper-size", "210", "297",
                "--label-offset", "0", "0",
                "--label-size", "65", "26.5",
                "--label-delta", "0", "0",
                "--dataset-json",
                getClass().getResource("/instances/").getFile(),
                "--instances-json",
                getClass().getResource("/test-referring-instances.json").getFile(),
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
}
