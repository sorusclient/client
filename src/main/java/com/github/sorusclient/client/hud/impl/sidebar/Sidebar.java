package com.github.sorusclient.client.hud.impl.sidebar;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.util.Color;

public class Sidebar extends HUDElement {

    public Sidebar() {
        super("sideBar");
    }

    @Override
    protected void render(double x, double y, double scale) {
        IEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return;

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        renderer.drawRectangle(x, y, this.getWidth() * scale, this.getHeight() * scale, Color.fromRGB(0, 0, 0, 80));

        IScoreboard scoreboard = Sorus.getInstance().get(MinecraftAdapter.class).getWorld().getScoreboard();
        IScoreboardObjective sidebarObjective = scoreboard.getObjective(IScoreboard.Slot.SIDEBAR);

        if (sidebarObjective == null) return;

        fontRenderer.drawString(sidebarObjective.getName(), x + this.getWidth() * scale / 2 - fontRenderer.getWidth(sidebarObjective.getName()) * scale / 2, y + 1 * scale, scale, Color.WHITE);

        double yOffset = (fontRenderer.getHeight() + 2) * scale;

        for (IScoreboardScore score : sidebarObjective.getScores()) {
            fontRenderer.drawString(score.getName(), x + 2 * scale, y + yOffset, scale, Color.WHITE);
            String scoreString = String.valueOf(score.getScore());
            fontRenderer.drawString(scoreString, x + this.getWidth() * scale - (fontRenderer.getWidth(scoreString) + 1) * scale, y + yOffset, scale, Color.fromRGB(255, 85, 85, 255));
            yOffset += (fontRenderer.getHeight() + 1) * scale;
        }
    }

    @Override
    public double getWidth() {
        IEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return 0;

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        IScoreboard scoreboard = Sorus.getInstance().get(MinecraftAdapter.class).getWorld().getScoreboard();
        IScoreboardObjective sidebarObjective = scoreboard.getObjective(IScoreboard.Slot.SIDEBAR);
        if (sidebarObjective == null) return 0;

        double maxWidth = 0;

        for (IScoreboardScore score : sidebarObjective.getScores()) {
            maxWidth = Math.max(maxWidth, fontRenderer.getWidth(score.getName() + " " + score.getScore()));
        }

        return 2 + maxWidth + 6;
    }

    @Override
    public double getHeight() {
        IEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return 0;

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        IScoreboard scoreboard = Sorus.getInstance().get(MinecraftAdapter.class).getWorld().getScoreboard();
        IScoreboardObjective sidebarObjective = scoreboard.getObjective(IScoreboard.Slot.SIDEBAR);
        if (sidebarObjective == null) return 0;

        return (sidebarObjective.getScores().size() + 1) * (fontRenderer.getHeight() + 1) + 1;
    }

}
