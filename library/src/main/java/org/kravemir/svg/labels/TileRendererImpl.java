package org.kravemir.svg.labels;

import org.kravemir.svg.labels.model.DocumentRenderOptions;
import org.kravemir.svg.labels.model.LabelGroup;
import org.kravemir.svg.labels.model.TiledPaper;
import org.kravemir.svg.labels.rendering.LabelDocumentBuilder;
import org.kravemir.svg.labels.rendering.LabelTemplate;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.svg.SVGDocument;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TileRenderer}
 */
public class TileRendererImpl implements TileRenderer {

    /*
        TODO: scaling, exceptions
     */

    @Override
    public List<String> render(TiledPaper paper, List<LabelGroup> labels, DocumentRenderOptions options) {
        return renderAsSVGDocument(paper, labels, options)
                .stream()
                .map(RenderingUtils::documentToString)
                .collect(Collectors.toList());
    }

    private Collection<SVGDocument> renderAsSVGDocument(TiledPaper paper, List<LabelGroup> labels, DocumentRenderOptions options) {

        InstanceRenderer instanceRenderer = new InstanceRendererImpl();
        LabelDocumentBuilder builder = new LabelDocumentBuilder(paper, options);

        for (LabelGroup l : labels) {
            for (LabelGroup.Instance instance : l.getInstances()) {
                String templateSVG = l.getTemplate();
                if (l.getTemplateDescriptor() != null) {
                    try {
                        templateSVG = instanceRenderer.render(templateSVG, l.getTemplateDescriptor(), instance.getInstanceContent());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                LabelTemplate template = LabelTemplate.create(templateSVG, paper);

                for (int n = 0; n < instance.getCount(); n++) {
                    builder.placeLabel(template);
                }

                if (instance.getFillPage()) {
                    if (builder.getDocumentsCount() == 0) {
                        builder.placeLabel(template);
                    }
                    while (builder.isSpaceLeftOnCurrentPage()) {
                        builder.placeLabel(template);
                    }
                }
            }
        }

        return builder.getDocuments();
    }

    @Override
    public String renderSinglePageWithLabel(TiledPaper paper, String SVG) {
        LabelGroup l = LabelGroup.newBuilder()
                .setTemplate(SVG)
                .addAllInstances(Collections.singletonList(LabelGroup.Instance.newBuilder().setFillPage(true).build()))
                .build();
        return render(paper, Collections.singletonList(l), DocumentRenderOptions.newBuilder().build()).get(0);
    }
}
