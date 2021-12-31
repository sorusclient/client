package com.github.sorusclient.client.plugin;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PluginManager {

    private final List<Plugin> plugins = new ArrayList<>();

    public PluginManager() {
        try {
            Enumeration<URL> resources = PluginManager.class.getClassLoader().getResources("plugin.json");
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String contents = IOUtils.toString(resource.openStream());
                JSONObject json = new JSONObject(contents);

                String id = json.getString("id");
                String version = json.getString("version");
                String name = json.has("name") ? json.getString("name") : id;
                String description = json.has("description") ? json.getString("description") : "";

                String filePath = resource.toString();
                filePath = filePath.replace("jar:file:", "");
                filePath = filePath.substring(0, filePath.indexOf("!"));

                this.plugins.add(new Plugin(id, version, name, description, new File(filePath)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove(Plugin plugin) {
        plugin.getFile().delete();
        this.plugins.remove(plugin);
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

}
