/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import lombok.val;
import v1_8_9.net.minecraft.client.render.Tessellator;
import v1_8_9.net.minecraft.client.render.VertexFormats;

public class RenderUtil {

    public static void drawTexture(int x, int y, double textureX, double textureY, int width, int height) {
        textureX += 0.1;

        val tessellator = Tessellator.getInstance();
        val bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(x, y + height, 0.0).texture(textureX / 256, ((textureY + height) / 256)).next();
        bufferBuilder.vertex(x + width, y + height, 0.0).texture(((textureX + width) / 256), ((textureY + height) / 256)).next();
        bufferBuilder.vertex(x + width, y, 0.0).texture(((textureX + width) / 256), ((textureY + 0) / 256)).next();
        bufferBuilder.vertex(x, y, 0.0).texture(((textureX + 0) / 256), ((textureY + 0) / 256)).next();

        tessellator.draw();
    }

}
