package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.util.Color
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.font.TextRenderer
import kotlin.math.roundToInt

class MinecraftFontRenderer(private val textRenderer: TextRenderer) : IFontRenderer {

    override fun drawString(text: String, x: Double, y: Double, scale: Double, color: Color) {
        GL11.glPushMatrix()
        //GL11.glTranslated(x.roundToInt().toDouble(), y.roundToInt().toDouble(), 0.0)
        //GL11.glScaled(scale, scale, 1.0)
        //GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        //GL11.glEnable(GL11.GL_BLEND)\
        textRenderer.draw(text, (x / scale).toFloat(), (y / scale).toFloat(), color.rgb, false)
        //GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPopMatrix()
    }

    override fun getWidth(text: String): Double {
        return (textRenderer.getStringWidth(text) - 1).toDouble()
    }

    override fun getHeight(): Double {
        return (textRenderer.fontHeight - 1).toDouble()
    }

}