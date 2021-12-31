package com.github.sorusclient.client.module;

public class ModuleData {

    private final Module module;
    private final String name, description;

    public ModuleData(Module module, String name, String description) {
        this.module = module;
        this.name = name;
        this.description = description;
    }

    public Module getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
