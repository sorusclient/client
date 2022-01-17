package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.adapter.IRenderer
import com.github.sorusclient.client.adapter.RenderBuffer
import com.github.sorusclient.client.adapter.RenderBuffer.DrawMode
import com.github.sorusclient.client.util.Color
import org.lwjgl.opengl.GL11
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.render.Tessellator
import v1_8_9.net.minecraft.client.render.VertexFormats
import v1_8_9.net.minecraft.client.util.Window
import v1_8_9.net.minecraft.util.Identifier
import kotlin.math.cos
import kotlin.math.sin

class RendererImpl : IRenderer {
    override fun draw(buffer: RenderBuffer) {
        val mode: Int
        mode = when (buffer.drawMode) {
            DrawMode.QUAD -> GL11.GL_QUADS
            else -> -1
        }
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(mode, VertexFormats.POSITION_COLOR)
        for (vertex in buffer.vertices) {
            val point = vertex.point
            val color = vertex.color
            bufferBuilder.vertex(point.x, point.y, point.z)
                .color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        }
        tessellator.draw()
    }

    override fun setColor(color: Color) {
        GL11.glColor4d(color.red, color.green, color.blue, color.alpha)
    }

    override fun setLineThickness(thickness: Double) {
        GL11.glLineWidth(thickness.toFloat())
    }

    override fun drawRectangle(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        cornerRadius: Double,
        topLeftColor: Color,
        bottomLeftColor: Color,
        bottomRightColor: Color,
        topRightColor: Color
    ) {
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(GL11.GL_POLYGON, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(x + cornerRadius, y + height, 0.0).color(
            bottomLeftColor.red.toFloat(),
            bottomLeftColor.green.toFloat(),
            bottomLeftColor.blue.toFloat(),
            bottomLeftColor.alpha.toFloat()
        ).next()
        bufferBuilder.vertex(x + width - cornerRadius, y + height, 0.0).color(
            bottomRightColor.red.toFloat(),
            bottomRightColor.green.toFloat(),
            bottomRightColor.blue.toFloat(),
            bottomRightColor.alpha.toFloat()
        ).next()
        for (i in 0..89) {
            bufferBuilder.vertex(
                x + width - cornerRadius + sin(Math.toRadians(i.toDouble())) * cornerRadius,
                y + height - cornerRadius + cos(
                    Math.toRadians(i.toDouble())
                ) * cornerRadius,
                0.0
            ).color(
                bottomRightColor.red.toFloat(),
                bottomRightColor.green.toFloat(),
                bottomRightColor.blue.toFloat(),
                bottomRightColor.alpha.toFloat()
            ).next()
        }
        bufferBuilder.vertex(x + width, y + height - cornerRadius, 0.0).color(
            bottomRightColor.red.toFloat(),
            bottomRightColor.green.toFloat(),
            bottomRightColor.blue.toFloat(),
            bottomRightColor.alpha.toFloat()
        ).next()
        bufferBuilder.vertex(x + width, y + cornerRadius, 0.0).color(
            topRightColor.red.toFloat(),
            topRightColor.green.toFloat(),
            topRightColor.blue.toFloat(),
            topRightColor.alpha.toFloat()
        ).next()
        for (i in 90..179) {
            bufferBuilder.vertex(
                x + width - cornerRadius + sin(Math.toRadians(i.toDouble())) * cornerRadius,
                y + cornerRadius + cos(
                    Math.toRadians(i.toDouble())
                ) * cornerRadius,
                0.0
            ).color(
                topRightColor.red.toFloat(),
                topRightColor.green.toFloat(),
                topRightColor.blue.toFloat(),
                topRightColor.alpha.toFloat()
            ).next()
        }
        bufferBuilder.vertex(x + width - cornerRadius, y, 0.0).color(
            topRightColor.red.toFloat(),
            topRightColor.green.toFloat(),
            topRightColor.blue.toFloat(),
            topRightColor.alpha.toFloat()
        ).next()
        bufferBuilder.vertex(x + cornerRadius, y, 0.0).color(
            topLeftColor.red.toFloat(),
            topLeftColor.green.toFloat(),
            topLeftColor.blue.toFloat(),
            topLeftColor.alpha.toFloat()
        ).next()
        for (i in 180..269) {
            bufferBuilder.vertex(
                x + cornerRadius + sin(Math.toRadians(i.toDouble())) * cornerRadius, y + cornerRadius + cos(
                    Math.toRadians(i.toDouble())
                ) * cornerRadius, 0.0
            ).color(
                topLeftColor.red.toFloat(),
                topLeftColor.green.toFloat(),
                topLeftColor.blue.toFloat(),
                topLeftColor.alpha.toFloat()
            ).next()
        }
        bufferBuilder.vertex(x, y + cornerRadius, 0.0).color(
            topLeftColor.red.toFloat(),
            topLeftColor.green.toFloat(),
            topLeftColor.blue.toFloat(),
            topLeftColor.alpha.toFloat()
        ).next()
        bufferBuilder.vertex(x, y + height - cornerRadius, 0.0).color(
            bottomLeftColor.red.toFloat(),
            bottomLeftColor.green.toFloat(),
            bottomLeftColor.blue.toFloat(),
            bottomLeftColor.alpha.toFloat()
        ).next()
        for (i in 270..359) {
            bufferBuilder.vertex(
                x + cornerRadius + sin(Math.toRadians(i.toDouble())) * cornerRadius,
                y + height - cornerRadius + cos(
                    Math.toRadians(i.toDouble())
                ) * cornerRadius,
                0.0
            ).color(
                bottomLeftColor.red.toFloat(),
                bottomLeftColor.green.toFloat(),
                bottomLeftColor.blue.toFloat(),
                bottomLeftColor.alpha.toFloat()
            ).next()
        }
        tessellator.draw()
    }

    //TODO: rounded images, probably could be done with just calculating x and y texture positions
    override fun drawImage(imagePath: String?, x: Double, y: Double, width: Double, height: Double, color: Color) {
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier(imagePath))
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glColor4d(color.red, color.green, color.blue, color.alpha)
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(x, y + height, 0.0).texture(0.0, 1.0).next()
        bufferBuilder.vertex(x + width, y + height, 0.0).texture(1.0, 1.0).next()
        bufferBuilder.vertex(x + width, y, 0.0).texture(1.0, 0.0).next()
        bufferBuilder.vertex(x, y, 0.0).texture(0.0, 0.0).next()
        tessellator.draw()
    }

    override fun scissor(x: Double, y: Double, width: Double, height: Double) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        val minecraft = MinecraftClient.getInstance()
        val scaledResolution = Window(minecraft)
        GL11.glScissor(
            (x * minecraft.width / scaledResolution.scaledWidth).toInt(),
            ((scaledResolution.scaledHeight - (y + height)) * minecraft.height / scaledResolution.scaledHeight).toInt(),
            (width * minecraft.width / scaledResolution.scaledWidth).toInt(),
            (height * minecraft.height / scaledResolution.scaledHeight).toInt()
        )
    }

    override fun endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }

    private val fontRenderers: MutableMap<String, IFontRenderer> = HashMap()
    override fun getFontRenderer(id: String): IFontRenderer? {
        var fontRenderer = fontRenderers[id]
        return fontRenderer
            ?: when (id) {
                "minecraft" -> {
                    fontRenderer = MinecraftFontRenderer(MinecraftClient.getInstance().textRenderer)
                    fontRenderers[id] = fontRenderer
                    fontRenderer
                }
                else -> null
            }
    }
}