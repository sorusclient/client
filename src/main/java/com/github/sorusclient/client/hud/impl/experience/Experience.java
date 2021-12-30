package com.github.sorusclient.client.hud.impl.experience;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IPlayerEntity;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.util.Color;

public class Experience extends HUDElement {

    public Experience() {
        super("experience");
    }

    @Override
    protected void render(double x, double y, double scale) {
        IPlayerEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();

        double experiencePercent = player.getExperiencePercentUntilNextLevel();
        IExperienceRenderer experienceRenderer = GlassLoader.getInstance().getInterface(IExperienceRenderer.class);
        experienceRenderer.renderExperienceBar(x + 1 * scale, y + 1 * scale, scale, experiencePercent);

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer minecraftFontRenderer = renderer.getFontRenderer("minecraft");

        String experienceLevel = String.valueOf(player.getExperienceLevel());

        this.drawExperienceLevel(
                minecraftFontRenderer,
                experienceLevel,
                x + this.getWidth() / 2 * scale - minecraftFontRenderer.getWidth(experienceLevel) / 2 * scale,
                y - 1 * scale - minecraftFontRenderer.getHeight() / 2 * scale,
                scale
        );
    }

    private void drawExperienceLevel(IFontRenderer fontRenderer, String experienceLevel, double x, double y, double scale) {
        fontRenderer.drawString(experienceLevel, x - 1 * scale, y, scale, Color.BLACK);
        fontRenderer.drawString(experienceLevel, x + 1 * scale, y, scale, Color.BLACK);
        fontRenderer.drawString(experienceLevel, x, y - 1, scale, Color.BLACK);
        fontRenderer.drawString(experienceLevel, x, y + 1, scale, Color.BLACK);
        fontRenderer.drawString(experienceLevel, x, y, scale, Color.fromRGB(128, 255, 32, 255));
    }

    @Override
    public double getWidth() {
        return 185;
    }

    @Override
    public double getHeight() {
        return 7;
    }

}
