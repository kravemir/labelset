package org.kravemir.svg.labels.rendering;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.kravemir.svg.labels.model.DocumentRenderOptions;
import org.kravemir.svg.labels.model.TiledPaper;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LabelDocumentBuilder {
    private final DocumentRenderOptions options;

    private final TilePositionGenerator positionGenerator;
    private final List<SVGDocument> documents;
    private SVGDocument document;

    public LabelDocumentBuilder(TiledPaper p, DocumentRenderOptions options) {
        this.options = options;

        positionGenerator = new TilePositionGenerator(p);
        documents = new ArrayList<>();
    }

    private void startDocument() {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        document = (SVGDocument) impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        Element root = document.getDocumentElement();

        //set page size
        root.setAttributeNS(null, "width", RenderingUtils.length(getPaperWidth()));
        root.setAttributeNS(null, "height", RenderingUtils.length(getPaperHeight()));
        //root.setAttributeNS(null, "viewBox", "0 0 " + lw.getValueAsString() + " " + lh.getValueAsString());

        if (options.getRenderPageBorders())
            root.appendChild(RenderingUtils.createRect(document, 0, 0, getPaperWidth(), getPaperHeight()));

        positionGenerator.start();
    }


    public void placeLabel(LabelTemplate template) {
        if (!isSpaceLeftOnCurrentPage()) {
            startDocument();
            documents.add(document);
        }

        placeLabelClone(template);

        if (options.getRenderTileBorders())
            renderTileBorders();

        if (options.getRenderLabelBorders())
            renderLabelBorders(template);

        positionGenerator.nextPosition();
    }

    private void placeLabelClone(LabelTemplate template) {
        Element label = (Element) template.templateRoot.cloneNode(true);
        document.adoptNode(label);
        label.setAttributeNS(null, "x", RenderingUtils.length(getX() + template.labelOffsetX));
        label.setAttributeNS(null, "y", RenderingUtils.length(getY() + template.labelOffsetY));
        label.setAttributeNS(null, "width", RenderingUtils.length(template.labelW));
        label.setAttributeNS(null, "height", RenderingUtils.length(template.labelH));
        document.getRootElement().appendChild(label);
    }

    private void renderTileBorders() {
        document.getRootElement().appendChild(RenderingUtils.createRect(
                document,
                getX(),
                getY(),
                positionGenerator.getTileWidth(),
                positionGenerator.getTileHeight())
        );
    }

    private void renderLabelBorders(LabelTemplate template) {
        document.getRootElement().appendChild(RenderingUtils.createRect(
                document,
                getX() + template.labelOffsetX,
                getY() + template.labelOffsetY,
                template.labelW,
                template.labelH)
        );
    }

    public Collection<SVGDocument> getDocuments() {
        return documents;
    }

    public int getDocumentsCount() {
        return documents.size();
    }

    public boolean isSpaceLeftOnCurrentPage() {
        return document != null && !positionGenerator.isFull();
    }

    private double getX() {
        return positionGenerator.getX();
    }

    private double getY() {
        return positionGenerator.getY();
    }

    private double getPaperWidth() {
        return positionGenerator.getPaperWidth();
    }

    private double getPaperHeight() {
        return positionGenerator.getPaperHeight();
    }
}
