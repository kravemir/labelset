package org.kravemir.svg.labels;

import org.kravemir.svg.labels.annotations.ToBePublicApi;
import org.kravemir.svg.labels.model.LabelTemplateDescriptor;

import java.util.Map;

@ToBePublicApi
public interface InstanceRenderer {

    /**
     * Renders instance as SVG from provided SVG template, template descriptor, and instance content
     *
     * @param svgTemplate template to use for rendering
     * @param templateDescriptor descriptor for {@code svgTemplate} input argument
     * @param instanceContent map containing instance values for attributes in template
     * @return rendered SVG
     */
    @ToBePublicApi
    String render(String svgTemplate,
                  LabelTemplateDescriptor templateDescriptor,
                  Map<String, String> instanceContent);
}
