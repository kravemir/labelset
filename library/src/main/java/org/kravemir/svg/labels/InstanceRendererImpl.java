package org.kravemir.svg.labels;

import org.kravemir.svg.labels.model.LabelTemplateDescriptor;
import org.kravemir.svg.labels.utils.ExpressionEvaluator;
import org.kravemir.svg.labels.utils.RenderingUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import javax.xml.xpath.*;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InstanceRendererImpl implements InstanceRenderer {

    private final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
    private final XPath xpath = XPathFactory.newInstance().newXPath();
    private final XPathExpression tspanXPath;

    public InstanceRendererImpl() {
        try {
            tspanXPath = xpath.compile("*[local-name()='tspan']");
        } catch (XPathException e) {
            throw new RuntimeException("This shouldn't have happened!", e);
        }
    }

    @Override
    public String render(String svgTemplate,
                         LabelTemplateDescriptor templateDescriptor,
                         Map<String, String> instanceContent) {

        final SVGDocument document = RenderingUtils.parseSVG(svgTemplate);

        for (LabelTemplateDescriptor.ContentReplaceRule rule : templateDescriptor.getContentReplaceRules()) {
            XPathExpression elementXPath;
            try {
                elementXPath = xpath.compile(rule.getElementXPath());
            } catch (XPathExpressionException e) {
                // TODO: clean-up code
                throw new RuntimeException("This should not happen!", e);
            }

            String value = expressionEvaluator.evaluateExpression(rule.getValue(), instanceContent);
            String[] valueLines = value.split("\n");

            if (!shouldEvaluate(instanceContent, rule))
                continue;

            getNodeStream(document.getRootElement(), elementXPath).forEach(node -> {
                replaceNodeTextContents(node, valueLines);
            });
        }

        return RenderingUtils.documentToString(document);
    }

    private void replaceNodeTextContents(Node node, String[] valueLines) {
        NodeList spanNodes = getNodes(node, tspanXPath);
        int line = 0;
        for(; line < Math.min(spanNodes.getLength(), valueLines.length); line++ ) {
            Node tspanNode = spanNodes.item(line);
            tspanNode.setTextContent(valueLines[line]);
        }
        for(; line < spanNodes.getLength(); line++ ) {
            Node tspanNode = spanNodes.item(line);
            tspanNode.setTextContent("");
        }
    }

    private boolean shouldEvaluate(Map<String, String> instanceContent, LabelTemplateDescriptor.ContentReplaceRule rule) {
        if(rule.getIf() == null || "".equals(rule.getIf().trim()))
            return true;

        return (boolean) expressionEvaluator.evaluateExpressionWithJEXL(rule.getIf(), instanceContent);
    }

    private Stream<Node> getNodeStream(Node root, XPathExpression expression){
        NodeList nodes = getNodes(root, expression);
        return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item);
    }

    private NodeList getNodes(Node root, XPathExpression expression){
        try {
            return (NodeList) expression.evaluate(root, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

}
