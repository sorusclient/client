package com.github.sorusclient.client.module.impl.oldanimations;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;

import java.util.List;

public class OldAnimations extends ModuleDisableable {

    private final Setting<Boolean> oldBlockHit;
    private final Setting<Boolean> showArmorDamage;

    public OldAnimations() {
        super("oldAnimations");

        this.register("oldBlockHit", this.oldBlockHit = new Setting<>(false));
        this.register("showArmorDamage", this.showArmorDamage = new Setting<>(false));
    }

    public boolean isOldBlockHit() {
        return this.oldBlockHit.getValue();
    }

    public boolean showArmorDamage() {
        return this.showArmorDamage.getValue();
    }

    @Override
    public void addSettings(List<ConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new ConfigurableData.Toggle("Old Blockhit", this.oldBlockHit));
        settings.add(new ConfigurableData.Toggle("Show Armor Damage", this.showArmorDamage));
    }

}
