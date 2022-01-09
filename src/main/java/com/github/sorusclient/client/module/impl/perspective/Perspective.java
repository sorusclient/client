package com.github.sorusclient.client.module.impl.perspective;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.adapter.event.KeyEvent;
import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;

import java.util.List;

public class Perspective extends ModuleDisableable {

    private final Setting<Key> key;

    private boolean toggled;
    private PerspectiveMode previousPerspective = null;

    public Perspective() {
        super("perspective");

        this.register("key", this.key = new Setting<>(Key.F));

        Sorus.getInstance().get(EventManager.class).register(KeyEvent.class, this::onKey);
    }

    private void onKey(KeyEvent event) {
        IAdapter adapter = Sorus.getInstance().get(IAdapter.class);
        if (this.isEnabled() && adapter.getOpenScreen() == ScreenType.IN_GAME) {
            if (event.getKey() == this.key.getValue() && !event.isRepeat()) {
                this.toggled = event.isPressed();

                if (this.toggled) {
                    this.previousPerspective = adapter.getPerspective();

                    GlassLoader.getInstance().getInterface(IPerspectiveHelper.class).onToggle();

                    adapter.setPerspective(PerspectiveMode.THIRD_PERSON_BACK);
                } else {
                    adapter.setPerspective(this.previousPerspective);
                }
            }
        }
    }

    public boolean isToggled() {
        return toggled;
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Key", this.key, SettingConfigurableData.ConfigurableType.KEYBIND));
    }

}
