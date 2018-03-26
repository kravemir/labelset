package org.kravemir.svg.labels.tool.gui.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class NavigationTabsPane extends HBox {
    private VBox buttonsBox;
    private StackPane contentPane;

    private ObservableList<Tab> tabs = FXCollections.observableArrayList();
    private TabSelectionModel selectionModel = new TabSelectionModel(tabs);

    public NavigationTabsPane() {
        buttonsBox = new VBox();
        buttonsBox.getStyleClass().add("navigationTabsPaneButtonsBox");
        contentPane = new StackPane();
        contentPane.getStyleClass().add("navigationTabsPaneContentPane");

        this.getChildren().addAll(buttonsBox, contentPane);
        HBox.setHgrow(contentPane, Priority.ALWAYS);

        this.tabs.addListener((ListChangeListener<Tab>) c -> {
            while (c.next()) {
                for (Tab tab : c.getRemoved()) {
                    if (tab != null && !getTabs().contains(tab)) {
                        // TODO: on remove
                    }
                }

                for (Tab tab : c.getAddedSubList()) {
                    if (tab != null) {
                        Label label = new Label(tab.getName());

                        Button button = new Button(null, label);
                        button.setMaxWidth(Double.MAX_VALUE);

                        buttonsBox.getChildren().add(button);

                        if (tab.getContent() != null) {
                            contentPane.getChildren().add(tab.getContent());

                            tab.getContent().visibleProperty().bind(tab.selected);
                            tab.getContent().managedProperty().bind(tab.selected);
                        }

                        button.setOnAction(event -> selectionModel.select(tab));
                        tab.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                if(oldValue != null && oldValue) {
                                    // TODO: pseudo states
                                    button.getStyleClass().remove("selected");
                                }
                                if(newValue != null && newValue){
                                    // TODO: pseudo states
                                    button.getStyleClass().add("selected");
                                }
                            }
                        });
                    }
                }

                if (selectionModel.getSelectedIndex() < 0 && tabs.size() > 0) {
                    selectionModel.select(0);
                }
            }
        });
    }

    public VBox getButtonsBox() {
        return buttonsBox;
    }

    public ObservableList<Tab> getTabs() {
        return tabs;
    }

    public TabSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public class TabSelectionModel extends SingleSelectionModel<Tab> {
        private ObservableList<Tab> tabs;

        public TabSelectionModel(ObservableList<Tab> tabs) {
            this.tabs = tabs;
        }

        @Override
        public void select(int index) {
            if (index < 0 || (getItemCount() > 0 && index >= getItemCount()) ||
                    (index == getSelectedIndex() && getModelItem(index).isSelected())) {
                return;
            }

            // Unselect the old tab
            if (getSelectedIndex() >= 0 && getSelectedIndex() < tabs.size()) {
                tabs.get(getSelectedIndex()).selectedProperty().set(false);
            }

            Tab tab = getModelItem(index);
            setSelectedIndex(index);
            setSelectedItem(tab);
            tab.selectedProperty().set(true);
        }

        @Override
        public void select(Tab tab) {
            final int itemCount = getItemCount();

            for (int i = 0; i < itemCount; i++) {
                final Tab value = getModelItem(i);
                if (value != null && value.equals(tab)) {
                    select(i);
                    return;
                }
            }
        }

        @Override
        protected Tab getModelItem(int index) {
            return tabs.get(index);
        }

        @Override
        protected int getItemCount() {
            return tabs.size();
        }
    }

    public static class Tab {

        private String name;

        private ObjectProperty<Node> content;

        private BooleanProperty selected = new ReadOnlyBooleanWrapper(false);

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public final Node getContent() {
            return content == null ? null : content.get();
        }


        public final void setContent(Node value) {
            contentProperty().set(value);
        }


        public final ObjectProperty<Node> contentProperty() {
            if (content == null) {
                content = new SimpleObjectProperty<>(this, "content");
            }
            return content;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

    }
}
