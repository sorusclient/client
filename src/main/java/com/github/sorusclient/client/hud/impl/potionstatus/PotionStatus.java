package com.github.sorusclient.client.hud.impl.potionstatus;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.ui.UserInterface;
import com.github.sorusclient.client.util.Color;

import java.util.ArrayList;
import java.util.List;

public class PotionStatus extends HUDElement {

    public PotionStatus() {
        super("potionStatus");
    }

    @Override
    protected void render(double x, double y, double scale) {
        IEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return;

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");
        IPotionEffectRenderer potionEffectRenderer = GlassLoader.getInstance().getInterface(IPotionEffectRenderer.class);

        renderer.drawRectangle(x, y, this.getWidth() * scale, this.getHeight() * scale, Color.fromRGB(0, 0, 0, 100));

        double textY = y + 3 * scale;

        for (IPotionEffect effect : this.getEffects()) {
            potionEffectRenderer.render(effect.getType(), x + 3 * scale, textY, scale);
            fontRenderer.drawString(effect.getName() + " " + this.getAmplifierString(effect.getAmplifier()), x + 24 * scale, textY, scale, Color.WHITE);
            fontRenderer.drawString(effect.getDuration(), x + 24 * scale, textY + 2 * scale + fontRenderer.getHeight() * scale, scale, Color.WHITE);

            textY += (5 + fontRenderer.getHeight() * 2) * scale;
        }
    }

    public String getAmplifierString(int amplifier) {
        switch (amplifier) {
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return "";
        }
    }

    private List<IPotionEffect> getEffects() {
        MinecraftAdapter adapter = Sorus.getInstance().get(MinecraftAdapter.class);
        boolean editing = Sorus.getInstance().get(UserInterface.class).isHudEditScreenOpen();

        List<IPotionEffect> realEffects = adapter.getPlayer().getEffects();

        if (!editing || realEffects.size() > 0) {
            return realEffects;
        } else {
            List<IPotionEffect> fakeEffects = new ArrayList<>();
            fakeEffects.add(new FakePotionEffect("1:29", "Fire Resistance", 1, IPotionEffect.PotionType.FIRE_RESISTANCE));

            return fakeEffects;
        }
    }

    @Override
    public double getWidth() {
        IEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return 0;

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        double maxWidth = 0;

        for (IPotionEffect effect : this.getEffects()) {
            double width = fontRenderer.getWidth(effect.getName() + " " + this.getAmplifierString(effect.getAmplifier()));
            maxWidth = Math.max(maxWidth, width);
        }

        return 24 + maxWidth + 3;
    }

    @Override
    public double getHeight() {
        IEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return 0;

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

        List<IPotionEffect> effects = this.getEffects();

        return effects.size() == 0 ? 0 : 3 + effects.size() * (5 + fontRenderer.getHeight() * 2);
    }

    private static class FakePotionEffect implements IPotionEffect {

        private final String duration;
        private final String name;
        private final int amplifier;
        private final PotionType type;

        private FakePotionEffect(String duration, String name, int amplifier, PotionType type) {
            this.duration = duration;
            this.name = name;
            this.amplifier = amplifier;
            this.type = type;
        }

        @Override
        public String getDuration() {
            return this.duration;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int getAmplifier() {
            return this.amplifier;
        }

        @Override
        public PotionType getType() {
            return this.type;
        }

    }

}
