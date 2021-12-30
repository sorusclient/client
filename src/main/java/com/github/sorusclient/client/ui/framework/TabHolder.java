package com.github.sorusclient.client.ui.framework;

import java.util.*;
import java.util.List;

public class TabHolder extends Container {

    private final Map<String, Component> tabs = new HashMap<>();

    private String defaultTab;
    private String stateId;

    public TabHolder() {
        this.runtime = new Runtime();
    }

    public TabHolder setStateId(String stateId) {
        this.stateId = stateId;
        return this;
    }

    public TabHolder setDefaultTab(String defaultTab) {
        this.defaultTab = defaultTab;
        return this;
    }

    public TabHolder addChild(String tab, Component child) {
        this.tabs.put(tab, child);
        super.addChild(child);
        return this;
    }

    @Override
    public Container addChild(Component child) {
        return this;
    }

    public class Runtime extends Container.Runtime {

        private String prevCurrentTab = null;

        @Override
        protected List<Component> getChildren() {
            String currentTab = (String) this.getState(TabHolder.this.stateId);
            if (currentTab == null) {
                currentTab = TabHolder.this.defaultTab;
            }

            if (this.prevCurrentTab != null && !this.prevCurrentTab.equals(currentTab)) {
                TabHolder.this.tabs.get(this.prevCurrentTab).runtime.setHasInit(false);
            }

            this.prevCurrentTab = currentTab;

            return Collections.singletonList(TabHolder.this.tabs.get(currentTab));
        }

    }

}
