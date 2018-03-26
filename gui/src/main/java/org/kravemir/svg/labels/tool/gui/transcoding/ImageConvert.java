package org.kravemir.svg.labels.tool.gui.transcoding;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;
import org.apache.fop.svg.PDFTranscoder;

import java.awt.image.BufferedImage;
import java.io.*;

public class ImageConvert {

    public static BufferedImage rasterize(File svgFile) throws IOException {
        return rasterize(new FileInputStream(svgFile));
    }

    public static BufferedImage rasterize(InputStream contentStream) throws IOException {
        final BufferedImage[] imagePointer = new BufferedImage[1];
        String css = "svg {" +
                "shape-rendering: geometricPrecision;" +
                "text-rendering:  geometricPrecision;" +
                "color-rendering: optimizeQuality;" +
                "image-rendering: optimizeQuality;" +
                "}";

        File cssFile = File.createTempFile("batik-default-override-", ".css");
        try {
            FileUtils.writeStringToFile(cssFile, css);

            TranscodingHints transcoderHints = new TranscodingHints();
            transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
            transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
            transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
            transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
            transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());

            TranscoderInput input = new TranscoderInput(contentStream);
            ImageTranscoder t = new ImageTranscoder() {

                @Override
                public BufferedImage createImage(int w, int h) {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                }

                @Override
                public void writeImage(BufferedImage image, TranscoderOutput out) {
                    imagePointer[0] = image;
                }
            };
            t.setTranscodingHints(transcoderHints);
            t.transcode(input, null);
        } catch (TranscoderException ex) {
            ex.printStackTrace();
            throw new IOException("Couldn't convert " + "TODO");
        } finally {
            cssFile.delete();
        }

        return imagePointer[0];
    }

    public static byte[] toPDF(String svg) throws TranscoderException {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        TranscoderInput transcoderInput = new TranscoderInput(new ByteArrayInputStream(svg.getBytes()));
        TranscoderOutput transcoderOutput = new TranscoderOutput(outBytes);
        PDFTranscoder transcoder = new PDFTranscoder();
        transcoder.transcode(transcoderInput, transcoderOutput);
        return outBytes.toByteArray();
    }
}
