package org.kravemir.svg.labels.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NodesMatchingXPath extends TypeSafeMatcher<Node> {
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    public static NodesMatchingXPath nodesMatchingXPath(String xpath, Matcher<? super List<Node>> matcher) {
        return new NodesMatchingXPath(xpath, matcher);
    }

    private final String rule;
    private final Matcher<? super List<Node>> subMatcher;

    public NodesMatchingXPath(String rule, Matcher<? super List<Node>> subMatcher) {
        this.rule = rule;
        this.subMatcher = subMatcher;
    }

    @Override
    protected boolean matchesSafely(Node node) {
        List<Node> nodes = getNodes(node, rule);
        return subMatcher.matches(nodes);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("TODO");
    }

    private List<Node> getNodes(Node node, String expression) {
        try {
            NodeList nodes = (NodeList) XPATH.evaluate(expression, node, XPathConstants.NODESET);
            return IntStream.range(0, nodes.getLength()).mapToObj(nodes::item).collect(Collectors.toList());
        } catch (XPathExpressionException e) {
            throw new RuntimeException("This shouldn't happen", e);
        }
    }
}
