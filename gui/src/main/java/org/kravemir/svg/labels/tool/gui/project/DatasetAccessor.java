package org.kravemir.svg.labels.tool.gui.project;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.kravemir.svg.labels.tool.gui.event.InstanceSavedEvent;
import org.kravemir.svg.labels.tooling.Loader;
import org.kravemir.svg.labels.tool.model.Project;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class DatasetAccessor {

    private final ExecutorService exec = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private ProjectFacade projectFacade;

    private Task<File[]> loadDatasetFilesTask = null;

    private ReadOnlyBooleanWrapper loadingInProgress = new ReadOnlyBooleanWrapper();

    private String key;
    private File[] selectedDatasetFiles;
    private ReadOnlyListWrapper<String> dataSetItems;

    public DatasetAccessor(ProjectFacade projectFacade) {
        this.projectFacade = projectFacade;

        dataSetItems = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    }

    public void loadDatasetItems(String key) {
        this.key = key;

        Project.DataSet dataSet = projectFacade.getOpenProject().getDatasets().stream()
                .filter(d -> d.getKey().equals(key))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        if (loadDatasetFilesTask != null) {
            loadDatasetFilesTask.cancel(true);
        }

        loadDatasetFilesTask = new Task<File[]>() {
            @Override
            protected File[] call() {
                if (dataSet.getJsonCollectionStorage() != null) {
                    Pattern pattern = Pattern.compile(".*\\.json");

                    String location = dataSet.getJsonCollectionStorage().getLocation();
                    File jsonDataSetFolder = projectFacade.getOpenProjectFile().getParentFile().toPath().resolve(location).toFile();

                    if (!jsonDataSetFolder.exists()) {
                        throw new RuntimeException("Data folder doesn't exist: " + jsonDataSetFolder.getPath());
                    }

                    return jsonDataSetFolder.listFiles(
                            (file, filename) -> pattern.matcher(filename.toLowerCase()).matches()
                    );
                } else {
                    throw new RuntimeException();
                }
            }

            @Override
            protected void succeeded() {
                selectedDatasetFiles = getValue();
                dataSetItems.setAll(stream(selectedDatasetFiles).map(File::getName).collect(toList()));
                loadingInProgress.set(false);
            }
        };

        exec.execute(loadDatasetFilesTask);
        loadingInProgress.set(true);
    }

    public LinkedHashMap<String, String> loadInstance(int selectedIndex) throws IOException {
        return new Loader().loadInstance(selectedDatasetFiles[selectedIndex]);
    }

    // TODO: do not use filename as key, json is not pretty nice
    // TODO: the method not specific to this class
    public String getInstanceKey(int selectedIndex) {
        if (selectedIndex < 0 || selectedIndex >= selectedDatasetFiles.length) {
            throw new IllegalArgumentException("Selected index is out of range");
        }

        return selectedDatasetFiles[selectedIndex].getName();
    }

    public void saveInstance(int selectedIndex, LinkedHashMap<String, String> content) throws IOException {
        new Loader().saveInstance(selectedDatasetFiles[selectedIndex], content);
        if (projectFacade.getOnInstanceChange() != null) {
            projectFacade.getOnInstanceChange().handle(new InstanceSavedEvent(key, getInstanceKey(selectedIndex)));
        }
    }

    public boolean isLoadingInProgress() {
        return loadingInProgress.get();
    }

    public ReadOnlyBooleanProperty loadingInProgressProperty() {
        return loadingInProgress.getReadOnlyProperty();
    }

    public ObservableList<String> getDataSetItems() {
        return dataSetItems.get();
    }

    public ReadOnlyListProperty<String> dataSetItemsProperty() {
        return dataSetItems.getReadOnlyProperty();
    }
}
