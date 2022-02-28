package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.util.Color
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import v1_18_1.com.mojang.blaze3d.systems.RenderSystem
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.font.TextRenderer
import v1_18_1.net.minecraft.client.util.math.MatrixStack

class MinecraftFontRenderer(private val textRenderer: TextRenderer) : IFontRenderer {

    override fun drawString(text: String, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean) {
        val matrixStack = MatrixStack()
        matrixStack.scale(scale.toFloat(), scale.toFloat(), 1f)
        RenderSystem.disableDepthTest()
        RenderSystem.enableTexture()
        RenderSystem.enableBlend()

        if (shadow) {
            textRenderer.drawWithShadow(matrixStack, text, (x / scale).toFloat(), (y / scale).toFloat(), color.rgb)
        } else {
            textRenderer.draw(matrixStack, text, (x / scale).toFloat(), (y / scale).toFloat(), color.rgb)
        }

        RenderSystem.enableDepthTest()
    }

    override fun drawString(text: IText, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean) {
        val matrixStack = MatrixStack()
        matrixStack.scale(scale.toFloat(), scale.toFloat(), 1f)
        RenderSystem.disableDepthTest()
        RenderSystem.enableTexture()
        RenderSystem.enableBlend()

        if (shadow) {
            textRenderer.drawWithShadow(matrixStack, Util.apiTextToText(text), (x / scale).toFloat(), (y / scale).toFloat(), color.rgb)
        } else {
            textRenderer.draw(matrixStack, Util.apiTextToText(text), (x / scale).toFloat(), (y / scale).toFloat(), color.rgb)
        }

        RenderSystem.enableDepthTest()
    }

    override fun getWidth(text: String): Double {
        return (textRenderer.getWidth(text) - 1).toDouble()
    }

    override fun getWidth(text: IText): Double {
        return (textRenderer.getWidth(Util.apiTextToText(text)) - 1).toDouble()
    }

    override fun getHeight(): Double {
        return (textRenderer.fontHeight - 1.75)
    }

}