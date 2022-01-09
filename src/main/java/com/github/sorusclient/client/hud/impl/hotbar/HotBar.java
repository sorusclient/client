package com.github.sorusclient.client.hud.impl.hotbar;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.IPlayerInventory;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.util.Color;

public class HotBar extends HUDElement {

    public HotBar() {
        super("hotBar");
    }

    @Override
    protected void render(double x, double y, double scale) {
        IPlayerInventory inventory = Sorus.getInstance().get(IAdapter.class).getPlayer().getInventory();

        IHotBarRenderer hotBarRenderer = GlassLoader.getInstance().getInterface(IHotBarRenderer.class);
        hotBarRenderer.renderBackground(x + 1 * scale, y + 1 * scale, scale);
        hotBarRenderer.renderSelectedSlot(x + 20 * inventory.getSelectedSlot().ordinal() * scale, y, scale);

        IFontRenderer minecraftFontRenderer = Sorus.getInstance().get(IAdapter.class).getRenderer().getFontRenderer("minecraft");

        for (int i = 0; i < 9; i++) {
            IPlayerInventory.Slot slot = IPlayerInventory.Slot.values()[i];
            IItem item = inventory.getItem(slot);

            if (item != null) {
                hotBarRenderer.renderItem(x + 4 * scale + i * 20 * scale, y + 4 * scale, scale, item);

                if (item.getCount() > 1) {
                    String itemCount = String.valueOf(item.getCount());
                    minecraftFontRenderer.drawStringShadowed(
                            itemCount,
                            x + 20 * scale + i * 20 * scale - minecraftFontRenderer.getWidth(itemCount) * scale,
                            y + 21 * scale - minecraftFontRenderer.getHeight() * scale,
                            scale,
                            Color.WHITE,
                            Color.fromRGB(70, 70, 70, 255));
                }
            }
        }
    }

    @Override
    public double getWidth() {
        return 184;
    }

    @Override
    public double getHeight() {
        return 24;
    }

}
