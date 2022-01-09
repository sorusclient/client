package com.github.sorusclient.client.hud.impl.sidebar;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.ui.UserInterface;
import com.github.sorusclient.client.util.Color;

import java.util.ArrayList;
import java.util.List;

public class Sidebar extends HUDElement {

    private final Setting<Boolean> showScores;

    public Sidebar() {
        super("sideBar");

        this.register("showScores", this.showScores = new Setting<>(true));
    }

    @Override
    protected void render(double x, double y, double scale) {
        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        renderer.drawRectangle(x, y, this.getWidth() * scale, this.getHeight() * scale, Color.fromRGB(0, 0, 0, 80));

        IScoreboardObjective sidebarObjective = this.getSidebarObjective();

        if (sidebarObjective == null) return;

        fontRenderer.drawString(sidebarObjective.getName(), x + this.getWidth() * scale / 2 - fontRenderer.getWidth(sidebarObjective.getName()) * scale / 2, y + 1 * scale, scale, Color.WHITE);

        double yOffset = (fontRenderer.getHeight() + 2) * scale;

        for (IScoreboardScore score : sidebarObjective.getScores()) {
            fontRenderer.drawString(score.getName(), x + 2 * scale, y + yOffset, scale, Color.WHITE);
            String scoreString = String.valueOf(score.getScore());
            if (this.showScores.getValue()) {
                fontRenderer.drawString(scoreString, x + this.getWidth() * scale - (fontRenderer.getWidth(scoreString) + 1) * scale, y + yOffset, scale, Color.fromRGB(255, 85, 85, 255));
            }
            yOffset += (fontRenderer.getHeight() + 1) * scale;
        }
    }

    private IScoreboardObjective getSidebarObjective() {
        IScoreboard scoreboard = Sorus.getInstance().get(IAdapter.class).getWorld().getScoreboard();
        IScoreboardObjective sidebarObjective = scoreboard.getObjective(IScoreboard.Slot.SIDEBAR);

        boolean editing = Sorus.getInstance().get(UserInterface.class).isHudEditScreenOpen();

        if (sidebarObjective != null || !editing) {
            return sidebarObjective;
        } else {
            List<IScoreboardScore> fakeScores = new ArrayList<>();
            fakeScores.add(new FakeScoreboardScore("Steve", 0));
            fakeScores.add(new FakeScoreboardScore("Alex", 1));

            return new FakeScoreboardObjective(fakeScores, "Points");
        }
    }

    @Override
    public double getWidth() {
        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        IScoreboardObjective sidebarObjective = this.getSidebarObjective();
        if (sidebarObjective == null) return 0;

        double maxWidth = fontRenderer.getWidth(sidebarObjective.getName());

        for (IScoreboardScore score : sidebarObjective.getScores()) {
            String scoreString = this.showScores.getValue() ? " " + score.getScore() : "";
            maxWidth = Math.max(maxWidth, fontRenderer.getWidth(score.getName() + scoreString));
        }

        return 2 + maxWidth + 6;
    }

    @Override
    public double getHeight() {
        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        IScoreboardObjective sidebarObjective = this.getSidebarObjective();
        if (sidebarObjective == null) return 0;

        return (sidebarObjective.getScores().size() + 1) * (fontRenderer.getHeight() + 1) + 1;
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Show Scores", this.showScores, SettingConfigurableData.ConfigurableType.TOGGLE));
    }

    private static class FakeScoreboardObjective implements IScoreboardObjective {

        private final List<IScoreboardScore> scores;
        private final String name;

        public FakeScoreboardObjective(List<IScoreboardScore> scores, String name) {
            this.scores = scores;
            this.name = name;
        }

        @Override
        public List<IScoreboardScore> getScores() {
            return this.scores;
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    private static class FakeScoreboardScore implements IScoreboardScore {

        private final String name;
        private final int score;

        public FakeScoreboardScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getScore() {
            return this.score;
        }

    }

}
