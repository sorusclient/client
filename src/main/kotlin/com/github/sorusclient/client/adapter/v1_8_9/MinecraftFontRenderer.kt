package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.util.Color
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.font.TextRenderer
import v1_8_9.net.minecraft.text.BaseText

class MinecraftFontRenderer(private val textRenderer: TextRenderer) : IFontRenderer {

    override fun drawString(text: String, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean) {
        GlStateManager.pushMatrix()
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.disableDepthTest()
        GlStateManager.enableTexture()
        GlStateManager.enableBlend()
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        textRenderer.draw(text, (x / scale).toFloat(), (y / scale).toFloat(), color.rgb, shadow)
        GlStateManager.enableDepthTest()
        GlStateManager.popMatrix()
    }

    override fun drawString(text: IText, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean) {
        drawString(Util.apiTextToText(text).string, x, y, scale, color, shadow)
    }

    override fun getWidth(text: String): Double {
        return (textRenderer.getStringWidth(text) - 1).toDouble()
    }

    override fun getWidth(text: IText): Double {
        return getWidth(Util.apiTextToText(text).string)
    }

    override fun getHeight(): Double {
        return (textRenderer.fontHeight - 1.75)
    }

}