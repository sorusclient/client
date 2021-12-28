package com.github.sorusclient.client.hud.impl.hotbar;

import com.github.sorusclient.client.adapter.IItem;

public interface IHotBarRenderer {
    void renderBackground(double x, double y, double scale);
    void renderItem(double x, double y, double scale, IItem item);
    void renderSelectedSlot(double x, double y, double scale);
}
