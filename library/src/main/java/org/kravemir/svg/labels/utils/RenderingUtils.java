package org.kravemir.svg.labels.utils;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLength;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class RenderingUtils {
    private static final short LENGTH_TYPE = SVGLength.SVG_LENGTHTYPE_MM;
    private static final String LENGTH_STRING = "mm";

    public static SVGDocument parseSVG(String s) {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        try {
            return factory.createSVGDocument("", new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException("This should have never had happened!", e);
        }
    }

    public static String documentToString(Document doc) {
        try {
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static SVGDocument createSVG() {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        return (SVGOMDocument)impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
    }

    public static Element createRect(Document doc, double x, double y, double w, double h){
        Element r = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
        r.setAttributeNS(null, "style", "fill:none;stroke:black;stroke-width:0.5");
        r.setAttributeNS(null, "x", x+"mm");
        r.setAttributeNS(null, "y", y+"mm");
        r.setAttributeNS(null, "width", w+"mm");
        r.setAttributeNS(null, "height", h+"mm");
        return r;
    }

    public static String length(double d){
        return Double.toString(d) + LENGTH_STRING;
    }

    //todo find better solution
    public static double length(String s){
        SVGOMDocument doc = (SVGOMDocument) createSVG();
        SVGOMSVGElement root = (SVGOMSVGElement)doc.getRootElement();

        //fix for SVGLength support
        doc.setSVGContext(new DummySVGContext());
        root.setSVGContext(doc.getSVGContext());

        SVGLength lw = root.createSVGLength();
        lw.setValueAsString(s);
        lw.convertToSpecifiedUnits(LENGTH_TYPE);
        return lw.getValueInSpecifiedUnits();
    }

    // helper class for length()
    public static class DummySVGContext implements SVGContext {
        @Override
        public float getPixelUnitToMillimeter() { return 1; }

        @Override
        public float getPixelToMM() { return 1; }

        @Override
        public Rectangle2D getBBox() { return null; }

        @Override
        public AffineTransform getScreenTransform() { return null; }

        @Override
        public void setScreenTransform(AffineTransform at) { }

        @Override
        public AffineTransform getCTM() { return null;
        }

        @Override
        public AffineTransform getGlobalTransform() { return null; }

        @Override
        public float getViewportWidth() { return 0; }

        @Override
        public float getViewportHeight() { return 0;}

        @Override
        public float getFontSize() { return 0; }
    }
}
