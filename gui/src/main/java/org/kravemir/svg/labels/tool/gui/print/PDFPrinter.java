package org.kravemir.svg.labels.tool.gui.print;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;

public class PDFPrinter {
    public void printPDF(byte[] pdf) throws IOException, PrinterException {
        PDDocument document = PDDocument.load(pdf);

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));

        if (job.printDialog()) {
            job.print();
        }
    }
}
