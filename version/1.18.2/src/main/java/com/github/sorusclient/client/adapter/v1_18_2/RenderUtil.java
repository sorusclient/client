/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import lombok.val;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;
import v1_18_2.net.minecraft.client.render.GameRenderer;
import v1_18_2.net.minecraft.client.render.Tessellator;
import v1_18_2.net.minecraft.client.render.VertexFormat;
import v1_18_2.net.minecraft.client.render.VertexFormats;

public class RenderUtil {

    public static void drawTexture(double x, double y, double textureX, double textureY, double width, double height, double textureWidth, double textureHeight) {
        textureX += 0.1;
        width -= 0.1;

        RenderSystem.setShader(() -> GameRenderer.getPositionTexShader());
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        val tessellator = Tessellator.getInstance();
        val bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(x, y + height, 0.0).texture((float) textureX / 256, (((float) (textureY + textureHeight)) / 256)).next();
        bufferBuilder.vertex(x + width, y + height, 0.0).texture((((float) (textureX + textureWidth)) / 256), (((float) (textureY + textureHeight)) / 256)).next();
        bufferBuilder.vertex(x + width, y, 0.0).texture((((float) (textureX + textureWidth)) / 256), (((float) textureY + 0) / 256)).next();
        bufferBuilder.vertex(x, y, 0.0).texture((((float) textureX + 0) / 256), (((float) textureY + 0) / 256)).next();

        tessellator.draw();
    }

}
