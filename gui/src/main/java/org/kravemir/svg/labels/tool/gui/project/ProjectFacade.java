package org.kravemir.svg.labels.tool.gui.project;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import org.apache.commons.io.FileUtils;
import org.kravemir.svg.labels.tool.gui.event.CompositeEventHandler;
import org.kravemir.svg.labels.tool.gui.event.InstanceSavedEvent;
import org.kravemir.svg.labels.tool.model.Project;

import java.io.File;
import java.io.IOException;

public class ProjectFacade {

    private ReadOnlyObjectWrapper<File> openProjectFile = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<Project> openProject = new ReadOnlyObjectWrapper<>();

    private ObjectProperty<CompositeEventHandler<InstanceSavedEvent>> onInstanceChange = new SimpleObjectProperty<>(new CompositeEventHandler<>());

    public void loadProject(File file) throws IOException {
        Project project = getMapper().readValue(
                FileUtils.readFileToString(file),
                Project.class
        );

        setOpenProject(project);
        setOpenProjectFile(file);
    }

    public File getOpenProjectFile() {
        return openProjectFile.get();
    }

    public ReadOnlyObjectProperty<File> openProjectFileProperty() {
        return openProjectFile.getReadOnlyProperty();
    }

    public void setOpenProjectFile(File openProjectFile) {
        this.openProjectFile.set(openProjectFile);
    }

    public Project getOpenProject() {
        return openProject.get();
    }

    public ReadOnlyObjectProperty<Project> openProjectProperty() {
        return openProject.getReadOnlyProperty();
    }

    private void setOpenProject(Project openProject) {
        this.openProject.set(openProject);
    }

    public EventHandler<InstanceSavedEvent> getOnInstanceChange() {
        return onInstanceChange.get();
    }

    public void addOnInstanceChangeHandler(EventHandler<? super InstanceSavedEvent> eventHandler) {
        onInstanceChange.get().addEventHandler(eventHandler);
    }

    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        org.kravemir.svg.labels.model.JacksonMixIns.registerMixIns(mapper);
        org.kravemir.svg.labels.tool.model.JacksonMixIns.registerMixIns(mapper);

        return mapper;
    }
}
