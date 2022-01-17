package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.util.Color
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.font.TextRenderer

class MinecraftFontRenderer(private val textRenderer: TextRenderer) : IFontRenderer {
    override fun drawString(text: String?, x: Double, y: Double, scale: Double, color: Color) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        textRenderer.draw(text, 0f, 0f, color.rgb, false)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPopMatrix()
    }

    override fun getWidth(text: String?): Double {
        return (textRenderer.getStringWidth(text) - 1).toDouble()
    }

    override val height: Double
        get() = (textRenderer.fontHeight - 1).toDouble()
}