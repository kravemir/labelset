package org.kravemir.svg.labels.tool.gui.ui.component;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.kravemir.svg.labels.tool.gui.transcoding.ImageConvert;

import static org.apache.commons.io.IOUtils.toInputStream;

public class SVGImageView extends AnchorPane {

    private final ImageView imageView;

    public SVGImageView() {
        imageView = new ImageView();

        this.getChildren().setAll(imageView);
    }

    public void setSVG(String svg) {
        try {
            Image img = SwingFXUtils.toFXImage(ImageConvert.rasterize(toInputStream(svg)), null);
            imageView.setImage(img);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
