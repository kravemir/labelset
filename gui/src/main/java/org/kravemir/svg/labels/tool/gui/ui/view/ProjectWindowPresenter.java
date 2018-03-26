package org.kravemir.svg.labels.tool.gui.ui.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.kravemir.svg.labels.tool.gui.ui.component.NavigationTabsPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ProjectWindowPresenter implements Initializable {

    @FXML NavigationTabsPane rootTabs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootTabs.getSelectionModel().select(2);
    }
}
