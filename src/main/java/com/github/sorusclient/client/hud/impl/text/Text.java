package com.github.sorusclient.client.hud.impl.text;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.util.Color;

public class Text extends HUDElement {

    public Text() {
        super("text");
    }

    @Override
    protected void render(double x, double y, double scale) {
        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer minecraft = renderer.getFontRenderer("minecraft");

        String text = "TestText";

        double width = this.getWidth() * scale;
        double height = this.getHeight() * scale;

        renderer.drawRectangle(x, y, width, height, Color.fromRGB(0, 0, 0, 100));
        minecraft.drawString(text, x + width / 2 - minecraft.getWidth(text) / 2 * scale, y + height / 2 - minecraft.getHeight() / 2 * scale, scale, Color.WHITE);
    }

    @Override
    public double getWidth() {
        return 60;
    }

    @Override
    public double getHeight() {
        return 12;
    }

}
