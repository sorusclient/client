package com.github.sorusclient.client.server;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IServer;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.GameJoinEvent;
import com.github.sorusclient.client.event.impl.GameLeaveEvent;
import com.github.sorusclient.client.event.impl.SorusCustomPacketEvent;
import com.github.sorusclient.client.module.Module;
import com.github.sorusclient.client.module.ModuleData;
import com.github.sorusclient.client.module.ModuleManager;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class ServerIntegrationManager {

    private final String BASE_SERVERS_URL = "https://raw.githubusercontent.com/sorusclient/asset/main/server";
    private final String SERVERS_JSON_URL = BASE_SERVERS_URL + "/servers.json";

    public ServerIntegrationManager() {
        EventManager eventManager = Sorus.getInstance().get(EventManager.class);
        eventManager.register(GameJoinEvent.class, this::onGameJoin);
        eventManager.register(GameLeaveEvent.class, this::onGameLeave);
        eventManager.register(SorusCustomPacketEvent.class, this::onCustomPacket);
    }

    private void onGameJoin(GameJoinEvent event) {
        IServer server = Sorus.getInstance().get(MinecraftAdapter.class).getCurrentServer();
        if (server != null) {
            new Thread(() -> {
                String json = this.getJsonForServer(server.getIp());
                if (json != null) {
                    this.applyServerConfiguration(json);
                }
            }).start();
        }
    }

    private void onGameLeave(GameLeaveEvent event) {
        this.removeServerConfiguration();
    }

    private void onCustomPacket(SorusCustomPacketEvent event) {
        if (event.getChannel().equals("integration")) {
            this.applyServerConfiguration(event.getContents());
        }
    }

    private String getJsonForServer(String ip) {
        try {
            InputStream inputStream = new URL(SERVERS_JSON_URL).openStream();
            String jsonString = IOUtils.toString(inputStream);
            inputStream.close();

            Map<String, Object> json = new JSONObject(jsonString).toMap();
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                if (ip.matches((String) entry.getValue())) {
                    InputStream inputStream1 = new URL(BASE_SERVERS_URL + "/" + entry.getKey() + ".json").openStream();
                    String serverJson = IOUtils.toString(inputStream1);
                    inputStream1.close();
                    return serverJson;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void applyServerConfiguration(String json) {
        Map<String, Object> jsonObject = new JSONObject(json).toMap();
        try {
            Map<String, Object> modules = (Map<String, Object>) jsonObject.get("module");

            if (modules != null) {
                for (Map.Entry<String, Object> entry : modules.entrySet()) {
                    Module module = Sorus.getInstance().get(ModuleManager.class).get(entry.getKey());
                    module.loadForced((Map<String, Object>) entry.getValue());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeServerConfiguration() {
        for (ModuleData moduleData : Sorus.getInstance().get(ModuleManager.class).getModules()) {
            Module module = moduleData.getModule();
            module.removeForced();
        }
    }

}
