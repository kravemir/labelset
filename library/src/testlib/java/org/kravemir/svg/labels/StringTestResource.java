package org.kravemir.svg.labels;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

public class StringTestResource extends TestResource<String> {

    public StringTestResource(Class<?> clazz, String name) {
        super(clazz, name);
    }

    protected String convert(URL resource) {
        try {
            return  IOUtils.toString(resource);
        } catch (IOException e) {
            throw new RuntimeException("This should not happen!!!", e);
        }
    }

}
