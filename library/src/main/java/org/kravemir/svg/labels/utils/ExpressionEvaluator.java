package org.kravemir.svg.labels.utils;

import org.apache.commons.jexl3.*;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionEvaluator {

    private Pattern p = Pattern.compile(
            "\\$(?:([a-zA-Z]+)|\\{([a-zA-Z]+)})"
    );

    private JexlEngine jexl = new JexlBuilder().create();

    public String evaluateExpression(String expression, Map<String,String> variables) {

        Matcher m = p.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String variableName = m.group(1);
            if(variableName == null)
                variableName = m.group(2);

            String value = variables.get(variableName);
            if(value == null) {
                throw new RuntimeException(String.format("There's no value for %s, in %s", variableName, variables));
            }
            m.appendReplacement(sb, value);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public Object evaluateExpressionWithJEXL(String expression, Map<String,String> variables) {
        // Create an expression
        JexlExpression e = jexl.createExpression( expression );

        // Create a context and add data
        final JexlContext jc = new MapContext();
        jc.set("instance", Collections.unmodifiableMap(variables));

        // Now evaluate the expression, getting the result
        return e.evaluate(jc);
    }
}
