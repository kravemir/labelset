package org.kravemir.svg.labels;

import com.fasterxml.jackson.databind.ObjectMapper;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.kravemir.svg.labels.TemplateResoures.*;
import static org.kravemir.svg.labels.matcher.NodesMatchingXPath.nodesMatchingXPath;

@RunWith(JUnitParamsRunner.class)
public class InstanceRendererImplTest {

    private ObjectMapper mapper;
    private InstanceRendererImpl renderer;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        renderer = new InstanceRendererImpl();
    }

    @Test
    public void testInstanceContentReplacement() throws XPathExpressionException {
        String renderedInstance = renderer.render(
                TEMPLATE_01.get(),
                TEMPLATE_01_DESCRIPTOR.get(),
                DATA_01.get()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstance);

        System.out.println(renderedInstance);

        assertThat(instanceDocument, TEMPLATE_01_DATA_01_MATCHER);
    }

    @Test
    public void testMultilineReplacement() throws IOException, XPathExpressionException {
        String renderedInstance = renderer.render(
                TEMPLATE_01.get(),
                TEMPLATE_01_DESCRIPTOR.get(),
                DATA_02.get()
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstance);

        System.out.println(renderedInstance);

        assertThat(instanceDocument, TEMPLATE_01_DATA_02_MATCHER);
    }

    @Test
    @Parameters
    public void testMultipleReplacements(String size, Matcher<? super Document> matcher) throws XPathExpressionException {
        Map<String,String> values = new HashMap<>();
        values.put("text", "Some multi-line\ntext");
        values.put("text_size", size);

        String renderedInstance = renderer.render(
                TEMPLATE_02.get(),
                TEMPLATE_02_DESCRIPTOR.get(),
                values
        );

        Document instanceDocument = RenderingUtils.parseSVG(renderedInstance);

        System.out.println(renderedInstance);

        assertThat(instanceDocument, matcher);
    }

    private static Object[] parametersForTestMultipleReplacements() {
        return new Object[]{
                new Object[] { "unknown", allOf(
                        nodesMatchingXPath( "//*[@id='text-large']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-large']/*[2][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[2][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[2][not(text())]", hasSize(1))
                )},
                new Object[] { "large", allOf(
                        nodesMatchingXPath( "//*[@id='text-large']/*[1][text()='Some multi-line']", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-large']/*[2][text()='text']", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[2][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[2][not(text())]", hasSize(1))
                )},
                new Object[] { "medium", allOf(
                        nodesMatchingXPath( "//*[@id='text-large']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-large']/*[2][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[1][text()='Some multi-line']", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[2][text()='text']", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[2][not(text())]", hasSize(1))
                )},
                new Object[] { "small", allOf(
                        nodesMatchingXPath( "//*[@id='text-large']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-large']/*[2][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[1][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-medium']/*[2][not(text())]", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[1][text()='Some multi-line']", hasSize(1)),
                        nodesMatchingXPath( "//*[@id='text-small']/*[2][text()='text']", hasSize(1))
                )},
        };
    }
}
