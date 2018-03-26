package org.kravemir.svg.labels.tool;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.kravemir.svg.labels.tool.ProjectIntegrationTest.runCommand;

public class DependencyCheck {

    @Test
    public void csvToolIsInstalled() throws IOException {
        ProjectIntegrationTest.RunResult result = runCommand("csvtool --help", null, null);

        assertThat(result.getExitValue(), is(0));
    }

    @Test
    public void inkscapeIsInstalled() throws IOException {
        ProjectIntegrationTest.RunResult result = runCommand("inkscape --help", null, null);

        assertThat(result.getExitValue(), is(0));
    }
}
