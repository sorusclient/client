package com.github.sorusclient.client.hud.impl.armor;

import com.github.glassmc.loader.GlassLoader;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Armor extends HUDElement {

    //TODO: Add option for reversing image & durability
    private final Setting<Mode> mode;
    private final Setting<Boolean> showHelmet;
    private final Setting<Boolean> showChestplate;
    private final Setting<Boolean> showLeggings;
    private final Setting<Boolean> showBoots;

    public Armor() {
        super("armor");

        this.register("mode", this.mode = new Setting<>(Mode.TOTAL));
        this.register("showHelmet", this.showHelmet = new Setting<>(true));
        this.register("showChestplate", this.showChestplate = new Setting<>(true));
        this.register("showLeggings", this.showLeggings = new Setting<>(true));
        this.register("showBoots", this.showBoots = new Setting<>(true));
    }

    @Override
    protected void render(double x, double y, double scale) {
        IPlayerEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();

        Renderer renderer = Sorus.getInstance().get(Renderer.class);
        IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");
        IArmorRenderer armorRenderer = GlassLoader.getInstance().getInterface(IArmorRenderer.class);

        switch (this.mode.getValue()) {
            case INDIVIDUAL:
                renderer.drawRectangle(x, y, this.getWidth() * scale, this.getHeight() * scale, Color.fromRGB(0, 0, 0, 100));

                double textY = y + 3 * scale;

                int index = 0;
                for (IItem armor : this.getArmor()) {
                    if (armor != null) {
                        if (this.shouldDisplay(armor, index)) {
                            armorRenderer.render(armor, x + 3 * scale, textY, scale);
                            fontRenderer.drawString(String.format("%.0f", armor.getRemainingDurability()), x + 24 * scale, textY + 5 * scale, scale, Color.WHITE);

                            textY += (5 + fontRenderer.getHeight() * 2) * scale;
                        }
                    }
                    index++;
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

    private boolean shouldDisplay(IItem iItem, int index) {
        if (iItem == null)  return false;

        switch (index) {
            case 0:
                return this.showHelmet.getValue();
            case 1:
                return this.showChestplate.getValue();
            case 2:
                return this.showLeggings.getValue();
            case 3:
                return this.showBoots.getValue();
        }
        return false;
    }

    private List<IItem> getArmor() {
        MinecraftAdapter adapter = Sorus.getInstance().get(MinecraftAdapter.class);
        boolean editing = Sorus.getInstance().get(UserInterface.class).isHudEditScreenOpen();

        List<IItem> realArmor = adapter.getPlayer().getArmor();

        if (!editing || realArmor.stream().filter(Objects::nonNull).count() > 0) {
            return realArmor;
        } else {
            List<IItem> fakeArmor = new ArrayList<>();
            fakeArmor.add(new FakeArmorItem(188, 240, IItem.ItemType.IRON_HELMET));
            fakeArmor.add(new FakeArmorItem(312, 350, IItem.ItemType.DIAMOND_CHESTPLATE));
            fakeArmor.add(new FakeArmorItem(98, 146, IItem.ItemType.GOLD_LEGGINGS));
            fakeArmor.add(new FakeArmorItem(68, 72, IItem.ItemType.LEATHER_BOOTS));

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
        switch (this.mode.getValue()) {
            case INDIVIDUAL:
                Renderer renderer = Sorus.getInstance().get(Renderer.class);
                IFontRenderer fontRenderer = renderer.getFontRenderer("minecraft");

                AtomicInteger index = new AtomicInteger(0);
                List<IItem> armor = this.getArmor().stream().filter(item -> {
                    boolean display = this.shouldDisplay(item, index.get());
                    index.set(index.get() + 1);
                    return display;
                }).collect(Collectors.toList());

                return armor.size() == 0 ? 0 : 3 + armor.size() * (5 + fontRenderer.getHeight() * 2);
            case TOTAL:
                return 11;
            default:
                return 0;
        }
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Mode", this.mode, SettingConfigurableData.ConfigurableType.CLICK_THROUGH));
        settings.add(new SettingConfigurableData("Show Helmet", this.showHelmet, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Show Chestplate", this.showChestplate, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Show Leggings", this.showLeggings, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Show Boots", this.showBoots, SettingConfigurableData.ConfigurableType.TOGGLE));
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

    private enum ArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
    }

}
