package com.github.sorusclient.client.setting;

import java.util.Map;

public interface SettingContainer {
    String getId();
    void load(Map<String, Object> settings);
    void loadForced(Map<String, Object> settings);
    void removeForced();
    Map<String, Object> save();

    boolean isShared();
    void setShared(boolean isShared);
}
