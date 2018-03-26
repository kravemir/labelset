package org.kravemir.svg.labels.tool.gui.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.io.FileUtils;
import org.kravemir.svg.labels.TileRenderer;
import org.kravemir.svg.labels.TileRendererImpl;
import org.kravemir.svg.labels.model.DocumentRenderOptions;
import org.kravemir.svg.labels.model.LabelGroup;
import org.kravemir.svg.labels.model.LabelTemplateDescriptor;
import org.kravemir.svg.labels.tool.gui.event.InstanceSavedEvent;
import org.kravemir.svg.labels.tool.gui.print.PDFPrinter;
import org.kravemir.svg.labels.tool.gui.project.DatasetAccessor;
import org.kravemir.svg.labels.tool.gui.project.ProjectFacade;
import org.kravemir.svg.labels.tool.gui.transcoding.ImageConvert;
import org.kravemir.svg.labels.tool.gui.ui.component.SVGImageView;
import org.kravemir.svg.labels.tool.model.Project;
import org.kravemir.svg.labels.tooling.Conventions;
import org.kravemir.svg.labels.tooling.Loader;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;

public class ExistingLabelPrintPresenter implements Initializable {

    @Inject ProjectFacade projectFacade;

    @FXML ChoiceBox<String> printLabelChoiceBox;
    @FXML ComboBox printDataComboBox;

    @FXML SVGImageView selectedLabelRendered;

    @FXML Button exportAsPDFButton;
    @FXML Button openAsPDFButton;
    @FXML Button printButton;

    private Project.OutputSet selectedOutputSet;
    private DatasetAccessor selectedDatasetAccessor;

    private String currentSVG = null;

    private EventHandler<InstanceSavedEvent> instanceSavedHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: test this reaction\
        this.instanceSavedHandler = event -> {
            int selectedIndex = printDataComboBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedInstanceKey = selectedDatasetAccessor.getInstanceKey(selectedIndex);

                if (event.getInstanceKey().equals(selectedInstanceKey)) {
                    loadDataItem(selectedIndex);
                }
            }
        };
        this.projectFacade.addOnInstanceChangeHandler(new WeakEventHandler<>(instanceSavedHandler));

        this.selectedDatasetAccessor = new DatasetAccessor(projectFacade);

        printLabelChoiceBox.getItems().addAll(
                projectFacade.getOpenProject().getOutputsets().stream()
                        .map(outputSet -> getLabel(projectFacade.getOpenProject(), outputSet))
                        .collect(toList())
        );
        printLabelChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    loadDataItems(newValue.intValue());
                }
        );
        printDataComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    loadDataItem(newValue.intValue());
                }
        );
        printButton.setOnAction(event -> {
            new Thread(() -> {
                try {
                    // TODO: handle label paper and real paper mismatch
                    // TODO: handle margins and alignment of content
                    new PDFPrinter().printPDF(ImageConvert.toPDF(currentSVG));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        selectedDatasetAccessor.loadingInProgressProperty().addListener((observable, old, value) -> {
            if (value) {
                printDataComboBox.setPromptText("loading ...");
                printDataComboBox.setDisable(true);
            } else {
                printDataComboBox.setDisable(false);
                printDataComboBox.setPromptText("Select data to print ...");
            }
        });

        printDataComboBox.itemsProperty().bind(selectedDatasetAccessor.dataSetItemsProperty());

        exportAsPDFButton.setOnAction(this::onExportAsPDFButtonAction);
        openAsPDFButton.setOnAction(this::onOpenAsPDFButtonAction);
    }

    private void onExportAsPDFButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Export PDF");
        fileChooser.setInitialFileName("label.pdf");
        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("PDF", ".pdf"));

        File file = fileChooser.showSaveDialog(printButton.getScene().getWindow());

        if (file != null) {
            try {
                byte[] pdf = ImageConvert.toPDF(currentSVG);
                FileUtils.writeByteArrayToFile(file, pdf);
            } catch (TranscoderException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onOpenAsPDFButtonAction(ActionEvent event) {
        try {
            byte[] pdf = ImageConvert.toPDF(currentSVG);
            File pdfTempFile = File.createTempFile("pdf-document", ".pdf");
            FileUtils.writeByteArrayToFile(pdfTempFile, pdf);
            pdfTempFile.deleteOnExit();

            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(pdfTempFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } catch (TranscoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDataItems(int indexOfSelectedLabel) {
        selectedOutputSet = projectFacade.getOpenProject().getOutputsets().get(indexOfSelectedLabel);
        selectedDatasetAccessor.loadDatasetItems(selectedOutputSet.getDataset());
    }

    private static String getLabel(Project selectedProject, Project.OutputSet outputSet) {
        return String.format(
                "%s (%s)",
                selectedProject.getDatasets().stream()
                        .filter(d -> d.getKey().equals(outputSet.getDataset()))
                        .findFirst()
                        .map(d -> d.getKey())
                        .orElse("N/A"),
                "11mm x 24.5mm"
        );
    }

    private void loadDataItem(int selectedIndex) {
        try {
            HashMap<String, String> selectedInstance = selectedDatasetAccessor.loadInstance(selectedIndex);
            List<LabelGroup> input = makeRendererInput(selectedOutputSet, selectedInstance);

            TileRenderer renderer = new TileRendererImpl();
            List<String> out = renderer.render(selectedOutputSet.getPaper(), input, DocumentRenderOptions.newBuilder().build());
            currentSVG = out.get(0);

            selectedLabelRendered.setSVG(currentSVG);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<LabelGroup> makeRendererInput(Project.OutputSet outputSet, HashMap<String, String> instance) throws IOException {
        String templatePath = outputSet.getTemplate();

        File templateFile = projectFacade.getOpenProjectFile().getParentFile().toPath().resolve(templatePath).toFile();
        File descriptorFile = new Conventions().resolveDescriptorFileForTemplate(templateFile);
        LabelTemplateDescriptor descriptor = new Loader().loadDescriptor(descriptorFile);

        return Collections.singletonList(
                LabelGroup.newBuilder()
                        .setTemplate(FileUtils.readFileToString(templateFile))
                        .setTemplateDescriptor(descriptor)
                        .addInstance(LabelGroup.Instance.newBuilder().setFillPage(true).setInstanceContent(instance).build())
                        .build()
        );
    }

}
