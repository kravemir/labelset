import org.apache.batik.swing.JSVGCanvas;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.svg.SVGDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class RenderSVGApplication {


    public static void main(String[] args) {
        new RenderSVGApplication().run();
    }

    static class Wrapper<T>{
        private T object;
        private String text;

        public Wrapper(T object, String text) {
            this.object = object;
            this.text = text;
        }

        public T getObject() {
            return object;
        }

        @Override
        public String toString() {
            return text;
        }

    }

    private void run() {
        JFrame f = new JFrame("SVG TilerF");
        f.setSize(1280,800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.setVisible(true);

        JSVGCanvas c = new JSVGCanvas();
        f.getContentPane().add(c);

        final JComboBox<Wrapper<List<SVGDocument>>> tests = new JComboBox<>();
        final JComboBox<Wrapper<SVGDocument>> pages = new JComboBox<>();

        addTests(tests);

        tests.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Wrapper<?> selected = (Wrapper<?>)tests.getSelectedItem();

                pages.removeAllItems();

                if(selected == null) return;
                int i = 0;
                for(Object d : (List<?>) selected.getObject())
                    pages.addItem(new Wrapper<>((SVGDocument)d, Integer.toString(++i)));

                pages.setSelectedIndex(0);
            }
        });

        pages.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Wrapper<?> selected = (Wrapper<?>)pages.getSelectedItem();
                if(selected == null) return;
                c.setSVGDocument( (SVGDocument) selected.getObject() );
            }
        });

        tests.setSelectedIndex(0);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        bottomPanel.add(new Label("Test"));
        bottomPanel.add(tests);

        bottomPanel.add(new Label("Page"));
        bottomPanel.add(pages);

        bottomPanel.setMaximumSize(new Dimension(100000, 32));

        tests.setPreferredSize(new Dimension(400,32));
        pages.setPreferredSize(new Dimension(400,32));

        tests.setMaximumSize(new Dimension(400,32));
        pages.setMaximumSize(new Dimension(400,32));

        f.getContentPane().add(bottomPanel, BorderLayout.CENTER);

        f.setVisible(true);
    }

    private void addTests(JComboBox<Wrapper<List<SVGDocument>>> tests){
        /* TODO !!!
        TiledPaper paper1 = TiledPaper.builder()
                .paperSize(297,210)
                .labelOffset(5,5)
                .labelSize(85,46)
                .labelDelta(5,5)
                .build();
        TiledPaper paper2 = TiledPaper.builder()
                .paperSize(297,210)
                .labelOffset(5,5)
                .labelSize(85,46.25)
                .labelDelta(5,5)
                .build();
        String svg1 = loadTemplate("/label01.svg");
        String svg2 = loadTemplate("/label02.svg");
        TileRendererImpl tileRenderer = new TileRendererImpl();

        String test = "multiple templates, multiple pages";
        ArrayList<LabelGroup> labels = new ArrayList<>();
        labels.add(LabelGroup.builder().setTemplate(svg1).setCount(14).build());
        labels.add(LabelGroup.builder().setTemplate(svg2).setCount(8).build());
        DocumentRenderOptions options =  DocumentRenderOptions.builder()
                .renderPageBorders(true)
                .renderTileBorders(true)
                .renderLabelBorders(true)
                .build();
        tests.addItem( new Wrapper<>( tileRenderer.renderAsSVGDocument(paper2,labels, options), test) );

        test = "fill one page";
        labels.clear();
        labels.add(LabelGroup.builder().setTemplate(svg2).setCount(7).build());
        labels.add(LabelGroup.builder().setTemplate(svg1).setFillPage().build());
        options = DocumentRenderOptions.builder()
                .renderPageBorders(true)
                .renderLabelBorders(true)
                .build();
        tests.addItem( new Wrapper<>( tileRenderer.renderAsSVGDocument(paper2,labels, options), test) );

        test = "fill pages";
        labels.clear();
        labels.add(LabelGroup.builder().setTemplate(svg2).setFillPage().build());
        labels.add(LabelGroup.builder().setTemplate(svg1).setFillPage().build());
        options = DocumentRenderOptions.builder()
                .renderTileBorders(true)
                .build();
        tests.addItem( new Wrapper<>( tileRenderer.renderAsSVGDocument(paper1,labels, options), test) );

        test = "one template, multiple pages";
        labels.clear();
        labels.add(LabelGroup.builder().setTemplate(svg1).setCount(20).build());
        options = DocumentRenderOptions.builder()
                .renderPageBorders(true)
                .build();
        tests.addItem( new Wrapper<>( tileRenderer.renderAsSVGDocument(paper2,labels, options), test) );

        test = "no template (only positions)";
        labels.clear();
        labels.add(LabelGroup.builder().setFillPage().build());
        options = DocumentRenderOptions.builder()
                .renderPageBorders(true)
                .renderTileBorders(true)
                .build();
        tests.addItem( new Wrapper<>( tileRenderer.renderAsSVGDocument(paper1,labels, options), test) );
        */
    }

    private String loadTemplate(String file) {
        try {
            return IOUtils.toString(this.getClass().getResourceAsStream(file));
        } catch (IOException e) {
            // TODO: do something nicer
            throw new RuntimeException(e);
        }
    }

}
