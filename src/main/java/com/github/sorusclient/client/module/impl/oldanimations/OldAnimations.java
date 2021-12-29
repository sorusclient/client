package com.github.sorusclient.client.module.impl.oldanimations;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.util.Pair;

import java.util.ArrayList;
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
    public List<Pair<Pair<String, Setting<?>>, Pair<String, Object>>> getSettings() {
        List<Pair<Pair<String, Setting<?>>, Pair<String, Object>>> settings = new ArrayList<>();

        settings.add(new Pair<>(new Pair<>("Old Blockhit", this.oldBlockHit), new Pair<>("TOGGLE", null)));
        settings.add(new Pair<>(new Pair<>("Show Armor Damage", this.showArmorDamage), new Pair<>("TOGGLE", null)));

        return settings;
    }

}
