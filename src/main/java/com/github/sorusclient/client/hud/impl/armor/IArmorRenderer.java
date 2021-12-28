package com.github.sorusclient.client.hud.impl.armor;

import com.github.sorusclient.client.adapter.IItem;

public interface IArmorRenderer {
    void render(IItem item, double x, double y, double scale);
    void renderArmorPlateBackground(double x, double y, double scale);
    void renderArmorPlate(double x, double y, double scale, ArmorRenderType armorRenderType);

    enum ArmorRenderType {
        FULL,
        HALF,
        EMPTY
    }

}
