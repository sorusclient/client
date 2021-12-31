package com.github.sorusclient.client.hud;

public class HUDData {

    private final Class<? extends HUDElement> hudClass;
    private final String name;
    private final String description;

    public HUDData(Class<? extends HUDElement> hudClass, String name, String description) {
        this.hudClass = hudClass;
        this.name = name;
        this.description = description;
    }

    public Class<? extends HUDElement> getHudClass() {
        return hudClass;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
