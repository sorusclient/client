package com.github.sorusclient.client.module.impl.togglesprintsneak;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IKeyBind;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.adapter.event.KeyEvent;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;

import java.util.List;

public class ToggleSprintSneak extends ModuleDisableable {

    private final Setting<Boolean> toggleSprint;
    private final Setting<Boolean> useCustomSprintKey;
    private final Setting<Key> customSprintKey;
    private final Setting<Boolean> toggleSneak;
    private final Setting<Boolean> useCustomSneakKey;
    private final Setting<Key> customSneakKey;

    private boolean sprintToggled;
    private boolean sneakToggled;

    public ToggleSprintSneak() {
        super("toggleSprintSneak");

        this.register("toggleSprint", this.toggleSprint = new Setting<>(false));
        this.register("useCustomSprintKey", this.useCustomSprintKey = new Setting<>(false));
        this.register("customSprintKey", this.customSprintKey = new Setting<>(Key.SHIFT_LEFT));
        this.register("toggleSneak", this.toggleSneak = new Setting<>(false));
        this.register("useCustomSneakKey", this.useCustomSneakKey = new Setting<>(false));
        this.register("customSneakKey", this.customSneakKey = new Setting<>(Key.CONTROL_LEFT));

        Sorus.getInstance().get(EventManager.class).register(KeyEvent.class, this::onKey);
    }

    private void onKey(KeyEvent event) {
        if (event.isPressed() && !event.isRepeat()) {
            if (event.getKey() == this.getSprintKey()) {
                this.sprintToggled = !this.sprintToggled;
            }

            if (event.getKey() == this.getSneakKey()) {
                this.sneakToggled = !this.sneakToggled;
            }
        }
    }

    private Key getSprintKey() {
        MinecraftAdapter minecraftAdapter = Sorus.getInstance().get(MinecraftAdapter.class);
        return this.useCustomSprintKey.getValue() ? this.customSprintKey.getValue() : minecraftAdapter.getKeyBind(IKeyBind.KeyBindType.SPRINT).getKey();
    }

    private Key getSneakKey() {
        MinecraftAdapter minecraftAdapter = Sorus.getInstance().get(MinecraftAdapter.class);
        return this.useCustomSneakKey.getValue() ? this.customSneakKey.getValue() : minecraftAdapter.getKeyBind(IKeyBind.KeyBindType.SNEAK).getKey();
    }

    public boolean isSprintToggled() {
        return this.isEnabled() && this.toggleSprint.getValue() && this.sprintToggled;
    }

    public boolean isSneakToggled() {
        return this.isEnabled() && this.toggleSneak.getValue() && this.sneakToggled;
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Toggle Sprint", this.toggleSprint, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Use Custom Sprint Key", this.useCustomSprintKey, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Custom Sprint Key", this.customSprintKey, SettingConfigurableData.ConfigurableType.KEYBIND));
        settings.add(new SettingConfigurableData("Toggle Sneak", this.toggleSneak, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Use Custom Sneak Key", this.useCustomSneakKey, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Custom Sneak Key", this.customSneakKey, SettingConfigurableData.ConfigurableType.KEYBIND));
    }

}
