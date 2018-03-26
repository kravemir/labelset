package org.kravemir.svg.labels.tool.gui.print;

import org.apache.commons.io.IOUtils;

import java.awt.print.PrinterException;
import java.io.IOException;

public class PDFPrinterManualTest {

    public static void main(String[] args) throws IOException, PrinterException {
        byte[] document = IOUtils.toByteArray(PDFPrinterManualTest.class.getResourceAsStream("/testVertical.pdf"));
        new PDFPrinter().printPDF(document);
    }
}
