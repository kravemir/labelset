package org.kravemir.svg.labels.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExpressionEvaluatorTest {

    private static final Map<String,String> VALUES = new HashMap<>();
    static {
        VALUES.put("name", "The Name");
        VALUES.put("weight", "80");
    }

    private ExpressionEvaluator expressionEvaluator = null;

    @Before
    public void setUp() throws Exception {
        expressionEvaluator = new ExpressionEvaluator();
    }

    @Test
    public void test() {
        testEvaluation("The Name", "$name");
        testEvaluation("The Name", "${name}");
        testEvaluation("The Name, 80g", "$name, ${weight}g");
        testEvaluation("The Name, 80g", "${name}, ${weight}g");
    }

    private void testEvaluation(String expected, String expression) {
        assertEquals(
                "Evaluation of `" + expression + "`",
                expected,
                expressionEvaluator.evaluateExpression(expression,VALUES)
        );
    }
}
