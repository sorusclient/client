package com.github.sorusclient.client.module.impl.perspective;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.adapter.ScreenType;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;

import java.util.List;

public class Perspective extends ModuleDisableable {

    //TODO: update particles and chunks and everything else
    private final Setting<Key> key;

    private boolean toggled;
    private IAdapter.PerspectiveMode previousPerspective = null;

    public Perspective() {
        super("perspective");

        this.register("key", this.key = new Setting<>(Key.F));

        Sorus.getInstance().get(EventManager.class).register(KeyEvent.class, this::onKey);
    }

    private void onKey(KeyEvent event) {
        MinecraftAdapter minecraftAdapter = Sorus.getInstance().get(MinecraftAdapter.class);
        if (this.isEnabled() && minecraftAdapter.getOpenScreen() == ScreenType.IN_GAME) {
            if (event.getKey() == this.key.getValue() && !event.isRepeat()) {
                this.toggled = event.isPressed();

                if (this.toggled) {
                    this.previousPerspective = minecraftAdapter.getPerspective();

                    GlassLoader.getInstance().getInterface(IPerspectiveHelper.class).onToggle();

                    minecraftAdapter.setPerspective(IAdapter.PerspectiveMode.THIRD_PERSON_BACK);
                } else {
                    minecraftAdapter.setPerspective(this.previousPerspective);
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
