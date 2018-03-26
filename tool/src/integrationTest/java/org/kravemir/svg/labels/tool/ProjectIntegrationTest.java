package org.kravemir.svg.labels.tool;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kravemir.svg.labels.TemplateResoures;
import org.kravemir.svg.labels.TestResource;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.kravemir.svg.labels.matcher.NodesMatchingXPath.nodesMatchingXPath;

public class ProjectIntegrationTest {

    private static final String LABLIE_BIN = System.getenv("LABLIE_BIN");

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void createProject() throws IOException {
        File srcFolder = tempFolder.newFolder("src");
        File srcProductsFolder = tempFolder.newFolder("src", "products");

        writeResourceToFile("/test-project.lablie-project.json", tempFolder.newFile("test-project"));

        writeResourceToFile(TemplateResoures.DATA_01, new File(srcProductsFolder, "product_a.json"));
        writeResourceToFile(TemplateResoures.DATA_02, new File(srcProductsFolder, "product_b.json"));
        writeResourceToFile(TemplateResoures.INSTANCES_CSV, new File(srcFolder, "series.csv"));

        writeResourceToFile(TemplateResoures.TEMPLATE_01, new File(srcFolder, "product.svg"));
        writeResourceToFile(TemplateResoures.TEMPLATE_01, new File(srcFolder, "series.svg"));
        writeResourceToFile(TemplateResoures.TEMPLATE_01_DESCRIPTOR, new File(srcFolder, "product.lablie.json"));
        writeResourceToFile(TemplateResoures.TEMPLATE_01_DESCRIPTOR, new File(srcFolder, "series.lablie.json"));
    }

    private void writeResourceToFile(String resourceName, File targetFile) throws IOException {
        FileUtils.copyURLToFile(getClass().getResource(resourceName), targetFile);
    }

    private void writeResourceToFile(TestResource resource, File targetFile) throws IOException {
        FileUtils.copyURLToFile(resource.getResourceURL(), targetFile);
    }

    @Test
    public void printEnv() {
        System.out.println(System.getenv("LABLIE_BIN"));
    }

    @Test
    public void makeFileGen1() throws IOException {
        RunResult projectMakeGenResult = runCommand(
                LABLIE_BIN + " project test-project generate-makefile"
        );

        assertThat(projectMakeGenResult.getExitValue(), is(0));

        String makefile = FileUtils.readFileToString(new File(tempFolder.getRoot(), "Makefile"));
        System.out.println(makefile);
    }

    @Test
    public void makeFileGen() throws IOException {
        RunResult projectMakeGenResult = runCommand(
                LABLIE_BIN + " project test-project generate-makefile"
        );
        runMake("information");
        RunResult informationResult = runMake("information");

        System.out.println(
                FileUtils.readFileToString(tempFolder.getRoot().toPath().resolve("tmp/lablie.Makefile").toFile())
        );

        assertThat(projectMakeGenResult.getExitValue(), is(0));
        assertThat(informationResult.getExitValue(), is(0));
        assertThat(informationResult.getLines(), contains(
                "DataSet products items: src/products/product_b.json src/products/product_a.json",
                "DataSet series items: data_01 data_02",
                "Outputs products, pdf items: output/products/product_b.pdf output/products/product_a.pdf",
                "Outputs products_with_svg, pdf items: output/products_with_svg/product_b.pdf output/products_with_svg/product_a.pdf",
                "Outputs products_with_svg, svg items: output/products_with_svg/product_b.svg output/products_with_svg/product_a.svg",
                "Outputs series, pdf items: output/series/data_01.pdf output/series/data_02.pdf",
                "Outputs series, svg items: output/series/data_01.svg output/series/data_02.svg"
        ));
        assertThat(informationResult.getErrorLines(), is(emptyIterable()));
    }

    @Test
    public void testRunMake() throws IOException {
        RunResult projectMakeGenResult = runCommand(
                LABLIE_BIN + " project test-project generate-makefile"
        );

        RunResult result = runMake();

        assertThat(projectMakeGenResult.getExitValue(), is(0));
        assertThat(result.getExitValue(), is(0));
        assertThat(
                fileContentsAsSVGDocument("output/products_with_svg/product_a.svg"),
                nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                        hasSize(28),
                        Matchers.everyItem(TemplateResoures.TEMPLATE_01_DATA_01_MATCHER)
                ))
        );
        assertThat(
                fileContentsAsSVGDocument("output/products_with_svg/product_b.svg"),
                nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                        hasSize(28),
                        Matchers.everyItem(TemplateResoures.TEMPLATE_01_DATA_02_MATCHER)
                ))
        );
        assertThat(
                fileContentsAsSVGDocument("output/series/data_01.svg"),
                nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                        hasSize(28)
                        // TODO: csv data
                ))
        );
        assertThat(
                fileContentsAsSVGDocument("output/series/data_02.svg"),
                nodesMatchingXPath("/*/*", Matchers.<List<Node>>allOf(
                        hasSize(28)
                        // TODO: csv data
                ))
        );
        assertIsValidPDF("output/products/product_a.pdf");
        assertIsValidPDF("output/products/product_b.pdf");
        assertIsValidPDF("output/products_with_svg/product_a.pdf");
        assertIsValidPDF("output/products_with_svg/product_b.pdf");
        assertIsValidPDF("output/series/data_01.pdf");
        assertIsValidPDF("output/series/data_02.pdf");
    }

    private void assertIsValidPDF(String filename) throws IOException {
        RandomAccessFile file = new RandomAccessFile(resolveTempFile(filename), "r");
        PDFParser parser = new PDFParser(file);
        parser.setLenient(false);
        parser.parse();

        PDDocument document = parser.getPDDocument();
        assertThat(document.getNumberOfPages(), is(1));
    }

    private Document fileContentsAsSVGDocument(String filename) throws IOException {
        return RenderingUtils.parseSVG(fileContents(filename));
    }

    private String fileContents(String filename) throws IOException {
        return FileUtils.readFileToString(
                resolveTempFile(filename),
                Charset.defaultCharset()
        );
    }

    private byte[] fileContentsAsByte(String filename) throws IOException {
        return FileUtils.readFileToByteArray(resolveTempFile(filename));
    }

    private File resolveTempFile(String filename) {
        return tempFolder.getRoot().toPath().resolve(filename).toFile();
    }

    public static class RunResult {
        private final int exitValue;
        private final List<String> lines;
        private List<String> errorLines;

        public RunResult(int exitValue, List<String> lines, List<String> errorLines) {
            this.exitValue = exitValue;
            this.lines = lines;
            this.errorLines = errorLines;
        }

        public int getExitValue() {
            return exitValue;
        }

        public List<String> getLines() {
            return lines;
        }

        public List<String> getErrorLines() {
            return errorLines;
        }
    }

    private RunResult runMake() throws IOException {
        return runMake(null);
    }

    private RunResult runMake(String target) throws IOException {
        return runCommand(target == null ? "make" : "make " + target);
    }

    private RunResult runCommand(String command) throws IOException {
        return runCommand(command, null, tempFolder.getRoot());
    }

    public static RunResult runCommand(String command, String[] envp, File dir) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec(command, envp, dir);
            process.waitFor();

            Reader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> lines = IOUtils.readLines(reader);

            Reader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            List<String> errorLines = IOUtils.readLines(errorReader);

            if (process.exitValue() != 0) {
                System.out.println(String.format("Command failed with %d: %s", process.exitValue(), command));
            } else {
                System.out.println(String.format("Command succeeded: %s", command));
            }

            for (String line : lines) {
                System.out.println(line);
            }

            for (String line : errorLines) {
                System.out.println(line);
            }

            return new RunResult(process.exitValue(), lines, errorLines);
        } catch (InterruptedException e) {
            throw new RuntimeException("This shouldn't have had happened!", e);
        }
    }
}
