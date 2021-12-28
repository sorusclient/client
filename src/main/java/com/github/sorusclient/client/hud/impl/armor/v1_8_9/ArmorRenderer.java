package com.github.sorusclient.client.hud.impl.armor.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.v1_8_9.Util;
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer;
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.item.Item;
import v1_8_9.net.minecraft.item.ItemStack;
import v1_8_9.net.minecraft.util.Identifier;

public class ArmorRenderer implements Listener, IArmorRenderer {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IArmorRenderer.class, this);
    }

    @Override
    public void render(IItem item, double x, double y, double scale) {
        ItemStack itemStack = (ItemStack) item.getInner();

        if (itemStack == null) {
            int id = Util.getIdByItemType(item.getType());
            if (id != -1) {
                itemStack = new ItemStack(Item.byRawId(id));
            }
        }

        if (itemStack == null) {
            System.out.println("unknown hotbar item");
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale * 18 / 16, scale * 18 / 16, 0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, 0 ,0);
        GL11.glPopMatrix();
    }

    @Override
    public void renderArmorPlateBackground(double x, double y, double scale) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        drawableHelper.drawTexture(0, 0, 16, 9, 9, 9);

        GL11.glPopMatrix();
    }

    @Override
    public void renderArmorPlate(double x, double y, double scale, ArmorRenderType armorRenderType) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        if (armorRenderType == ArmorRenderType.FULL) {
            drawableHelper.drawTexture(0, 0, 43, 9, 9, 9);
        } else if (armorRenderType == ArmorRenderType.HALF) {
            drawableHelper.drawTexture(0, 0, 25, 9, 9, 9);
        }

        GL11.glPopMatrix();
    }

}
