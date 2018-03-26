package org.kravemir.svg.labels.tool.gui.ui.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.kravemir.svg.labels.tool.gui.project.DatasetAccessor;
import org.kravemir.svg.labels.tool.gui.project.ProjectFacade;
import org.kravemir.svg.labels.tool.model.Project;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;

public class DataEditorPresenter implements Initializable {

    @Inject ProjectFacade projectFacade;

    @FXML ChoiceBox<String> printLabelChoiceBox;
    @FXML ComboBox printDataComboBox;

    @FXML GridPane contentGridPane;

    @FXML Button cancelButton;
    @FXML Button saveButton;

    private Project.DataSet selectedDataset;
    private DatasetAccessor selectedDatasetAccessor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.selectedDatasetAccessor = new DatasetAccessor(projectFacade);

        printLabelChoiceBox.getItems().addAll(
                projectFacade.getOpenProject().getDatasets().stream()
                        .map(dataset -> dataset.getKey())
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

        selectedDatasetAccessor.loadingInProgressProperty().addListener((observable, old, value) -> {
            if (value) {
                printDataComboBox.setPromptText("loading ...");
                printDataComboBox.setDisable(true);
            } else {
                printDataComboBox.setDisable(false);
                printDataComboBox.setPromptText("Select data to edit ...");
            }
        });

        printDataComboBox.itemsProperty().bind(selectedDatasetAccessor.dataSetItemsProperty());

        cancelButton.setOnAction(event -> {
            loadDataItem(printDataComboBox.getSelectionModel().getSelectedIndex());
        });
        saveButton.setOnAction(event -> {
            try {
                LinkedHashMap<String, String> newValues = new LinkedHashMap<>();

                for (int rowIndex = 0; rowIndex < contentGridPane.getChildren().size()/2; rowIndex++) {
                    Label label = (Label) contentGridPane.getChildren().get(rowIndex * 2);
                    TextArea editArea = (TextArea) contentGridPane.getChildren().get(rowIndex * 2 + 1);

                    newValues.put(label.getText(), editArea.getText());
                }

                selectedDatasetAccessor.saveInstance(printDataComboBox.getSelectionModel().getSelectedIndex(), newValues);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadDataItems(int indexOfSelectedLabel) {
        selectedDataset = projectFacade.getOpenProject().getDatasets().get(indexOfSelectedLabel);
        selectedDatasetAccessor.loadDatasetItems(selectedDataset.getKey());
    }

    private void loadDataItem(int selectedIndex) {
        try {
            LinkedHashMap<String, String> selectedInstance = selectedDatasetAccessor.loadInstance(selectedIndex);

            contentGridPane.getChildren().clear();
            int rowIndex = 0;
            for (String key : selectedInstance.keySet()) {
                Label label = new Label(key);

                TextArea editArea = new TextArea(selectedInstance.get(key));
                editArea.setPrefRowCount(2);

                contentGridPane.addRow(rowIndex, label, editArea);
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
