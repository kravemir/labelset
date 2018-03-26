package org.kravemir.svg.labels.tool.gui.ui.view;

import com.airhacks.afterburner.injection.Injector;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kravemir.svg.labels.tool.VersionProvider;
import org.kravemir.svg.labels.tool.gui.project.ProjectFacade;
import org.kravemir.svg.labels.tool.gui.user.HistoryManager;
import org.kravemir.svg.labels.tool.model.HistoryProjectRecord;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class WelcomeFrameController implements Initializable {

    public ListView<HistoryProjectRecord> list;
    public Label versionLabel;
    public Button openButton;

    private ProjectFacade projectFacade = Injector.instantiateModelOrService(ProjectFacade.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list.setItems(FXCollections.observableArrayList(getProjectsModel()));
        list.setCellFactory(param -> new ProjectListCell());
        list.setOnMouseClicked(event -> {
            if (list.getSelectionModel().getSelectedItem() == null) {
                return;
            }

            openProject(new File(list.getSelectionModel().getSelectedItem().getPath()));
        });

        versionLabel.setText(getVersion());

        openButton.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("JavaFX Projects");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Project Descriptor", "project.json"),
                    new FileChooser.ExtensionFilter("Other JSON", "*.json"),
                    new FileChooser.ExtensionFilter("Other", "*")
            );
            File selectedProject = chooser.showOpenDialog(openButton.getScene().getWindow());
            openProject(selectedProject);
        });
    }

    private void openProject(File selectedProjectFile) {
        try {
            projectFacade.loadProject(selectedProjectFile);

            HistoryManager.saveNewRecord(
                    HistoryProjectRecord.newBuilder()
                            .setName("N/A")
                            .setPath(selectedProjectFile.getAbsolutePath())
                            .build()
            );

            Stage stage = new Stage();

            ProjectWindowView projectWindowView = new ProjectWindowView();

            Scene scene = new Scene(projectWindowView.getView(), 1140, 640);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Welcome to SVG Labels");

            stage.show();

            ((Stage) openButton.getScene().getWindow()).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getVersion() {
        String[] version = new VersionProvider().getVersion();

        return String.format("Version %s", version.length > 0 ? version[0] : "0.0.0");
    }


    private Collection<HistoryProjectRecord> getProjectsModel() {
        return HistoryManager.loadConfig();
    }

    private class ProjectListCell extends ListCell<HistoryProjectRecord> {
        @Override
        protected void updateItem(HistoryProjectRecord item, boolean empty) {
            super.updateItem(item, empty);

            if (!isEmpty()) {
                Label projectNameLabel = new Label(item.getName());
                projectNameLabel.getStyleClass().setAll("projectNameLabel");

                Label projectPathLabel = new Label(item.getPath());
                projectPathLabel.getStyleClass().setAll("projectPathLabel");

                VBox vBox = new VBox(projectNameLabel, projectPathLabel);
                setGraphic(vBox);
            }
        }
    }
}
