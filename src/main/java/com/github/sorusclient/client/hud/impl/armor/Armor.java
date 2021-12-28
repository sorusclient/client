package com.github.sorusclient.client.hud.impl.armor;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.ui.UserInterface;
import com.github.sorusclient.client.util.Color;

import java.util.ArrayList;
import java.util.List;

public class Armor extends HUDElement {

    private final Setting<Mode> mode;

    public Armor() {
        super("armor");

        this.register("mode", this.mode = new Setting<>(Mode.TOTAL));
    }

    @Override
    protected void render(double x, double y, double scale) {
        IPlayerEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return;

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");
        IArmorRenderer armorRenderer = GlassLoader.getInstance().getInterface(IArmorRenderer.class);

        switch (this.mode.getValue()) {
            case INDIVIDUAL:
                renderer.drawRectangle(x, y, this.getWidth() * scale, this.getHeight() * scale, Color.fromRGB(0, 0, 0, 100));

                double textY = y + 3 * scale;

                for (IItem armor : this.getArmor()) {
                    armorRenderer.render(armor, x + 3 * scale, textY, scale);
                    fontRenderer.drawString(String.format("%.0f", armor.getRemainingDurability()), x + 24 * scale, textY + 5 * scale, scale, Color.WHITE);

                    textY += (5 + fontRenderer.getHeight() * 2) * scale;
                }
                break;
            case TOTAL:
                int armor = (int) player.getArmorProtection();

                for (int i = 0; i < 10; i++) {
                    double heartX = x + (1 + i * 8) * scale;

                    armorRenderer.renderArmorPlateBackground(heartX, y + 1 * scale, scale);

                    if ((i * 2) + 1 < armor) {
                        armorRenderer.renderArmorPlate(heartX, y + 1 * scale, scale, IArmorRenderer.ArmorRenderType.FULL);
                    } else if ((i * 2) < armor) {
                        armorRenderer.renderArmorPlate(heartX, y + 1 * scale, scale, IArmorRenderer.ArmorRenderType.HALF);
                    } else {
                        armorRenderer.renderArmorPlate(heartX, y + 1 * scale, scale, IArmorRenderer.ArmorRenderType.EMPTY);
                    }
                }
                break;
        }
    }

    private List<IItem> getArmor() {
        MinecraftAdapter adapter = Sorus.getInstance().get(MinecraftAdapter.class);
        boolean editing = Sorus.getInstance().get(UserInterface.class).isHudEditScreenOpen();

        List<IItem> realArmor = adapter.getPlayer().getArmor();

        if (!editing || realArmor.size() > 0) {
            return realArmor;
        } else {
            List<IItem> fakeArmor = new ArrayList<>();
            fakeArmor.add(new FakeArmorItem(312, 350, IItem.ItemType.DIAMOND_CHESTPLATE));
            fakeArmor.add(new FakeArmorItem(98, 146, IItem.ItemType.GOLD_LEGGINGS));

            return fakeArmor;
        }
    }

    @Override
    public double getWidth() {
        switch (this.mode.getValue()) {
            case INDIVIDUAL:
                return 60;
            case TOTAL:
                return 1 + 8 * 10 + 1 + 1;
            default:
                return 0;
        }
    }

    @Override
    public double getHeight() {
        IEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return 0;

        switch (this.mode.getValue()) {
            case INDIVIDUAL:
                Renderer renderer = Sorus.getInstance().get(Renderer.class);
                IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

                List<IItem> armor = this.getArmor();

                return armor.size() == 0 ? 0 : 3 + armor.size() * (5 + fontRenderer.getHeight() * 2);
            case TOTAL:
                return 11;
            default:
                return 0;
        }
    }

    private static class FakeArmorItem implements IItem {

        private final double remainingDurability;
        private final double maxDurability;
        private final ItemType type;

        public FakeArmorItem(double remainingDurability, double maxDurability, ItemType type) {
            this.remainingDurability = remainingDurability;
            this.maxDurability = maxDurability;
            this.type = type;
        }

        @Override
        public double getRemainingDurability() {
            return this.remainingDurability;
        }

        @Override
        public double getMaxDurability() {
            return this.maxDurability;
        }

        @Override
        public ItemType getType() {
            return this.type;
        }

        @Override
        public Object getInner() {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }

    }

    public enum Mode {
        INDIVIDUAL,
        TOTAL,
    }

}
