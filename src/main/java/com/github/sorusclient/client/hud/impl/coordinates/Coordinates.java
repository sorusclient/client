package com.github.sorusclient.client.hud.impl.coordinates;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.adapter.IEntity;
import com.github.sorusclient.client.adapter.IRenderer;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;
import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.util.Color;
import com.github.sorusclient.client.util.Pair;

import java.util.List;

public class Coordinates extends HUDElement {

    private final Setting<Boolean> showX;
    private final Setting<Boolean> showY;
    private final Setting<Boolean> showZ;

    private final Setting<Color> identifierColor;
    private final Setting<Color> otherColor;
    private final Setting<Color> valueColor;

    private final Setting<Mode> mode;

    public Coordinates() {
        super("coordinates");

        this.register("showX", this.showX = new Setting<>(true));
        this.register("showY", this.showY = new Setting<>(true));
        this.register("showZ", this.showZ = new Setting<>(true));

        this.register("identifierColor", this.identifierColor = new Setting<>(Color.WHITE));
        this.register("otherColor", this.otherColor = new Setting<>(Color.WHITE));
        this.register("valueColor", this.valueColor = new Setting<>(Color.WHITE));

        this.register("mode", this.mode = new Setting<>(Mode.BRACKET));
    }

    @Override
    protected void render(double x, double y, double scale) {
        IEntity player = Sorus.getInstance().get(IAdapter.class).getPlayer();

        IRenderer renderer = Sorus.getInstance().get(IAdapter.class).getRenderer();
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        renderer.drawRectangle(x, y, this.getWidth() * scale, this.getHeight() * scale, Color.fromRGB(0, 0, 0, 100));

        double textY = y + 3 * scale;
        if (showX.getValue()) {
            this.renderText(fontRenderer, this.getText("X", String.format("%.0f", player.getX())), x + 3 * scale, textY, scale);
            textY += (3 + fontRenderer.getHeight()) * scale;
        }
        if (showY.getValue()) {
            this.renderText(fontRenderer, this.getText("Y", String.format("%.0f", player.getY())), x + 3 * scale, textY, scale);
            textY += (3 + fontRenderer.getHeight()) * scale;
        }
        if (showZ.getValue()) {
            this.renderText(fontRenderer, this.getText("Z", String.format("%.0f", player.getZ())), x + 3 * scale, textY, scale);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Pair<String, Color>[] getText(String identifier, String value) {
        switch (this.mode.getValue()) {
            case SEMI_COLON:
                return new Pair[] {new Pair(identifier, this.identifierColor.getValue()), new Pair(": ", this.otherColor.getValue()), new Pair(value, this.valueColor.getValue())};
            case BRACKET:
                return new Pair[] {new Pair("[", this.otherColor.getValue()), new Pair(identifier, this.identifierColor.getValue()), new Pair("] ", this.otherColor.getValue()), new Pair(value, this.valueColor.getValue())};
            default:
                return new Pair[0];
        }
    }

    private void renderText(IFontRenderer fontRenderer, Pair<String, Color>[] text, double x, double y, double scale) {
        double partialX = 0;
        for (Pair<String, Color> pair : text) {
            fontRenderer.drawString(pair.getFirst(), x + partialX, y, scale, pair.getSecond());
            partialX += fontRenderer.getWidth(pair.getFirst()) * scale + 1;
        }
    }

    @Override
    public double getWidth() {
        return 60;
    }

    @Override
    public double getHeight() {
        return 12 * ((showX.getValue() ? 1 : 0) + (showY.getValue() ? 1 : 0) + (showZ.getValue() ? 1 : 0));
    }

    @Override
    public void addSettings(List<ConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new ConfigurableData.Toggle("Show X", this.showX));
        settings.add(new ConfigurableData.Toggle("Show Y", this.showY));
        settings.add(new ConfigurableData.Toggle("Show Z", this.showZ));
        settings.add(new ConfigurableData.ColorPicker("Identifier Color", this.identifierColor));
        settings.add(new ConfigurableData.ColorPicker("Other Color", this.otherColor));
        settings.add(new ConfigurableData.ColorPicker("Value Color", this.valueColor));
        settings.add(new ConfigurableData.ClickThrough("Mode", this.mode));
    }

    public enum Mode {
        SEMI_COLON,
        BRACKET
    }

}
