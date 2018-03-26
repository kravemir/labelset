package org.kravemir.svg.labels.tool;

import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionProvider implements CommandLine.IVersionProvider {

    private static String[] version = null;

    public String[] getVersion() {
        if (version != null) {
            return version;
        }

        try {
            Enumeration<URL> resources = VersionProvider.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try {
                    Manifest manifest = new Manifest(url.openStream());
                    Attributes attributes = manifest.getMainAttributes();

                    if ("lablie-tool".equals(get(attributes, "Implementation-Title"))) {
                        version = new String[]{get(attributes, "Implementation-Version")};
                        return version;
                    }
                } catch (IOException ex) {
                    // TODO: use logger
                    System.err.println("Unable to read from " + url + ": " + ex);
                    ex.printStackTrace(System.err);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Something went very wrong,...", e);
        }

        return new String[0];
    }

    private static String get(Attributes attributes, String key) {
        return attributes.getValue(new Attributes.Name(key));
    }
}
