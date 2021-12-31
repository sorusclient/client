package com.github.sorusclient.client.plugin;

import java.io.File;

public class Plugin {

    private final String id;
    private final String version;
    private final String name;
    private final String description;
    private final File file;

    public Plugin(String id, String version, String name, String description, File file) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.description = description;
        this.file = file;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public File getFile() {
        return this.file;
    }

}
