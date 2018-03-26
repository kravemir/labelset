package org.kravemir.svg.labels;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.kravemir.svg.labels.model.JacksonMixIns;
import org.kravemir.svg.labels.model.LabelTemplateDescriptor;
import org.kravemir.svg.labels.util.ListBuilder;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.kravemir.svg.labels.matcher.NodesMatchingXPath.nodesMatchingXPath;

public final class TemplateResoures {

    public static final TestResource<Map<String, String>> DATA_01 = getInstanceResource("/test-instance.json");
    public static final TestResource<Map<String, String>> DATA_02 = getInstanceResource("/test-instance.02.json");

    public static final StringTestResource INSTANCES_CSV = createTestResource("/test-instances.csv");

    public static final StringTestResource INSTANCES_01 = createTestResource("/test-instances.json");

    public static final StringTestResource TEMPLATE_01 = createTestResource("/template01.svg");
    public static final StringTestResource TEMPLATE_02 = createTestResource("/template02.svg");

    public static final TestResource<LabelTemplateDescriptor> TEMPLATE_01_DESCRIPTOR = getDescriptorFromResource("/template01.lablie.json");
    public static final TestResource<LabelTemplateDescriptor> TEMPLATE_02_DESCRIPTOR = getDescriptorFromResource("/template02.lablie.json");

    public static final Matcher<Node> TEMPLATE_01_MATCHER = allOf(
            nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='Multiline']", hasSize(1)),
            nodesMatchingXPath( ".//*[@id='nameText']/*[2][text()='name']", hasSize(1)),
            nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2))
    );
    public static final Matcher<Node> TEMPLATE_01_DATA_01_MATCHER = allOf(
            nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='JUnit test']", hasSize(1)),
            nodesMatchingXPath( ".//*[@id='nameText']/*[2][not(text())]", hasSize(1)),
            nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2)),
            nodesMatchingXPath( ".//*[@id='text4540']/*[text()='Test replacement of texts']", hasSize(1)),
            nodesMatchingXPath( ".//*[@id='text4544']/*[text()='13. 05. 2017']", hasSize(1))
    );
    public static final Matcher<Node> TEMPLATE_01_DATA_02_MATCHER = allOf(
            nodesMatchingXPath(".//*[@id='nameText']/*[1][text()='Line no. 01']", hasSize(1)),
            nodesMatchingXPath(".//*[@id='nameText']/*[2][text()='.. line no 02 ..']", hasSize(1)),
            nodesMatchingXPath(".//*[@id='nameText']/*", hasSize(2)),
            nodesMatchingXPath(".//*[@id='text4540']/*[text()='Test replacement of texts']", hasSize(1)),
            nodesMatchingXPath(".//*[@id='text4544']/*[text()='13. 05. 2017']", hasSize(1))
    );
    public static final Matcher<Node> TEMPLATE_02_MATCHER = allOf(
            nodesMatchingXPath( ".//*[@id='text-large']/*[1][text()='Large font']", hasSize(1)),
            nodesMatchingXPath( ".//*[@id='text-large']/*[2][text()='TEXT']", hasSize(1)),
            nodesMatchingXPath( ".//*[@id='text-large']/*", hasSize(2))
    );


    public static final List<Matcher<? super Node>> TEMPLATE_01_INSTANCES_01_MATCHER_LIST = new ListBuilder<Matcher<? super Node>>()
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
            .add(200, allOf(
                    nodesMatchingXPath( ".//*[@id='nameText']/*[1][text()='Test rest']", hasSize(1)),
                    nodesMatchingXPath( ".//*[@id='nameText']/*[2][not(text())]", hasSize(1)),
                    nodesMatchingXPath( ".//*[@id='nameText']/*", hasSize(2)),
                    nodesMatchingXPath( ".//*[@id='text4540']/*[text()='Fill the rest of the page']", hasSize(1)),
                    nodesMatchingXPath( ".//*[@id='text4544']/*[text()='15. 07. 2018']", hasSize(1))
            ))
            .build();

    private static StringTestResource createTestResource(String resource) {
        return new StringTestResource(TemplateResoures.class, resource);
    }

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<HashMap<String,Object>> HASH_MAP_TYPE_REFERENCE = new TypeReference<HashMap<String,Object>>() {};

    static {
        JacksonMixIns.registerMixIns(OBJECT_MAPPER);
    }

    private static TestResource<LabelTemplateDescriptor> getDescriptorFromResource(String resource) {
        return new TestResource<LabelTemplateDescriptor>(TemplateResoures.class, resource) {
            @Override
            protected LabelTemplateDescriptor convert(URL resource) {
                try {
                    return OBJECT_MAPPER.readValue(resource, LabelTemplateDescriptor.class);
                } catch (IOException e) {
                    throw new RuntimeException("This should not happen!!!", e);
                }
            }
        };
    }

    private static TestResource<Map<String,String>> getInstanceResource(String resource) {
        return new TestResource<Map<String,String>>(TemplateResoures.class, resource) {
            @Override
            protected Map<String,String> convert(URL resource) {
                try {
                    return OBJECT_MAPPER.readValue(resource, HASH_MAP_TYPE_REFERENCE);
                } catch (IOException e) {
                    throw new RuntimeException("This should not happen!!!", e);
                }
            }
        };
    }
}
