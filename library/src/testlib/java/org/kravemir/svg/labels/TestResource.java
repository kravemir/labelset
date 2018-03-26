package org.kravemir.svg.labels;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public abstract class TestResource<T> {
    protected final Class<?> clazz;
    protected final String name;

    public TestResource(Class<?> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    public T get() {
        return convert(clazz.getResource(name));
    }

    public File getAsFile(IOThrowingSupplier<File,IOException> fileCreator) {
        try {
            File copyFile = fileCreator.get();
            OutputStream copyFileStream = new FileOutputStream(copyFile);
            IOUtils.copy(
                    getResourceURL().openStream(),
                    copyFileStream
            );
            copyFileStream.close();

            return copyFile;
        } catch (IOException e) {
            throw new RuntimeException("This shouldn't happen!", e);
        }
    }

    public URL getResourceURL() {
        return getClass().getResource(name);
    }

    protected abstract T convert(URL resource);
}
