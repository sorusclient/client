package com.github.sorusclient.client.hud.impl.sidebar;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.util.Color;

import java.util.List;

public class Sidebar extends HUDElement {

    private final Setting<Boolean> showRedNumbers;

    //TODO: Dummy value
    public Sidebar() {
        super("sideBar");

        this.register("removeRedNumbers", this.showRedNumbers = new Setting<>(true));
    }

    @Override
    protected void render(double x, double y, double scale) {
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
            if (this.showRedNumbers.getValue()) {
                fontRenderer.drawString(scoreString, x + this.getWidth() * scale - (fontRenderer.getWidth(scoreString) + 1) * scale, y + yOffset, scale, Color.fromRGB(255, 85, 85, 255));
            }
            yOffset += (fontRenderer.getHeight() + 1) * scale;
        }
    }

    @Override
    public double getWidth() {
        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        IScoreboard scoreboard = Sorus.getInstance().get(MinecraftAdapter.class).getWorld().getScoreboard();
        IScoreboardObjective sidebarObjective = scoreboard.getObjective(IScoreboard.Slot.SIDEBAR);
        if (sidebarObjective == null) return 50;

        double maxWidth = 0;

        for (IScoreboardScore score : sidebarObjective.getScores()) {
            String scoreString = this.showRedNumbers.getValue() ? " " + score.getScore() : "";
            maxWidth = Math.max(maxWidth, fontRenderer.getWidth(score.getName() + scoreString));
        }

        return 2 + maxWidth + 6;
    }

    @Override
    public double getHeight() {
        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        IScoreboard scoreboard = Sorus.getInstance().get(MinecraftAdapter.class).getWorld().getScoreboard();
        IScoreboardObjective sidebarObjective = scoreboard.getObjective(IScoreboard.Slot.SIDEBAR);
        if (sidebarObjective == null) return 50;

        return (sidebarObjective.getScores().size() + 1) * (fontRenderer.getHeight() + 1) + 1;
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Show Red Numbers", this.showRedNumbers, SettingConfigurableData.ConfigurableType.TOGGLE));
    }

}
