package org.kravemir.svg.labels.rendering;

import org.kravemir.svg.labels.model.TiledPaper;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.kravemir.svg.labels.utils.RenderingUtils.parseSVG;

public class LabelTemplate {
    Element templateRoot = null;
    double labelW = 0, labelH = 0;
    double labelOffsetX = 0, labelOffsetY = 0;

    public static LabelTemplate create(String svg, TiledPaper paper) {
        LabelTemplate template = new LabelTemplate();
        template.load(svg, paper);
        return template;
    }

    public void load(String svg, TiledPaper paper) {
        Document templateDoc = parseSVG(svg);
        if (templateDoc != null) {
            templateRoot = templateDoc.getDocumentElement();
            labelW = RenderingUtils.length(templateRoot.getAttributeNS(null, "width"));
            labelH = RenderingUtils.length(templateRoot.getAttributeNS(null, "height"));
            labelOffsetX = (paper.getTileWidth() - labelW) / 2;
            labelOffsetY = (paper.getTileHeight() - labelH) / 2;
        }
    }
}
