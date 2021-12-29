package com.github.sorusclient.client.module.impl.oldanimations;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;

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
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Old Blockhit", this.oldBlockHit, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Show Armor Damage", this.showArmorDamage, SettingConfigurableData.ConfigurableType.TOGGLE));
    }

}
