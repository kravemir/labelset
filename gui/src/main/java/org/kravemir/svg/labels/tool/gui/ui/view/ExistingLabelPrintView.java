package org.kravemir.svg.labels.tool.gui.ui.view;

import com.airhacks.afterburner.views.FXMLView;

public class ExistingLabelPrintView extends FXMLView {

    public ExistingLabelPrintView() {
        this.getChildren().setAll(getView());
    }
}
