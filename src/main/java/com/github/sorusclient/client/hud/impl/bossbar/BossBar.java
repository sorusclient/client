package com.github.sorusclient.client.hud.impl.bossbar;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.adapter.IBossBar;
import com.github.sorusclient.client.adapter.IRenderer;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.util.Color;

public class BossBar extends HUDElement {

    public BossBar() {
        super("bossBar");
    }

    @Override
    protected void render(double x, double y, double scale) {
        IBossBar bossBar = Sorus.getInstance().get(IAdapter.class).getWorld().getBossBar();

        if (!bossBar.isBossBar()) return;

        double percent = bossBar.getPercentage();

        IBossBarRenderer bossBarRenderer = GlassLoader.getInstance().getInterface(IBossBarRenderer.class);
        bossBarRenderer.renderBossBar(x + 1 * scale, y + 11 * scale, scale, percent);

        IRenderer renderer = Sorus.getInstance().get(IAdapter.class).getRenderer();
        IFontRenderer minecraftFontRenderer = renderer.getFontRenderer("minecraft");

        String bossBarName = bossBar.getName();

        minecraftFontRenderer.drawString(
                bossBarName,
                x + this.getWidth() / 2 * scale - minecraftFontRenderer.getWidth(bossBarName) / 2 * scale,
                y + 5.5 * scale - minecraftFontRenderer.getHeight() / 2 * scale,
                scale,
                Color.WHITE
        );
    }

    @Override
    public double getWidth() {
        return 184;
    }

    @Override
    public double getHeight() {
        return 18;
    }

}
