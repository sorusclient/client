package com.github.sorusclient.client.server;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IServer;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.GameJoinEvent;
import com.github.sorusclient.client.event.impl.GameLeaveEvent;
import com.github.sorusclient.client.module.Module;
import com.github.sorusclient.client.module.ModuleData;
import com.github.sorusclient.client.module.ModuleManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ServerIntegrationManager {

    public ServerIntegrationManager() {
        Sorus.getInstance().get(EventManager.class).register(GameJoinEvent.class, this::onGameJoin);
        Sorus.getInstance().get(EventManager.class).register(GameLeaveEvent.class, this::onGameLeave);
    }

    private void onGameJoin(GameJoinEvent event) {
        IServer server = Sorus.getInstance().get(MinecraftAdapter.class).getCurrentServer();
        if (server != null) {
            String json = this.getJsonForServer(server.getIp());
            if (json != null) {
                this.applyServerConfiguration(json);
            }
        }
    }

    private void onGameLeave(GameLeaveEvent event) {
        this.removeServerConfiguration();
    }

    private String getJsonForServer(String ip) {
        if (ip.equals("mc.herobrine.org")) {
            return "{\n" +
                    "    \"module\": {\n" +
                    "        \"environmentChanger\": {\n" +
                    "            \"enabled\": false\n" +
                    "        },\n" +
                    "        \"fullBright\": {\n" +
                    "            \"enabled\": true\n" +
                    "        },\n" +
                    "        \"oldAnimations\": {\n" +
                    "            \"showArmorDamage\": false\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"hud\": {\n" +
                    "        \"sideBar\": {\n" +
                    "            \"showScores\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n";
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
