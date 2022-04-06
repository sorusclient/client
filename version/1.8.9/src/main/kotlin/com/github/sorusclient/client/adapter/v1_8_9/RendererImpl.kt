/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.adapter.IRenderer
import com.github.sorusclient.client.adapter.RenderBuffer
import com.github.sorusclient.client.adapter.RenderBuffer.DrawMode
import com.github.sorusclient.client.adapter.event.RenderEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.util.Color
import org.apache.commons.io.IOUtils
import v1_8_9.org.lwjgl.BufferUtils
import v1_8_9.org.lwjgl.opengl.GL11
import v1_8_9.org.lwjgl.opengl.GL15
import v1_8_9.org.lwjgl.opengl.GL20
import v1_8_9.org.lwjgl.opengl.GL30
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.render.GameRenderer
import v1_8_9.net.minecraft.client.render.Tessellator
import v1_8_9.net.minecraft.client.render.VertexFormats
import v1_8_9.net.minecraft.client.util.Window
import java.awt.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sin

class RendererImpl : IRenderer {

    private val tasks: MutableList<() -> Unit> = ArrayList()

    init {
        EventManager.register<RenderEvent> {
            for (task in ArrayList(tasks)) {
                task()
            }

            tasks.clear()
        }
    }

    override fun draw(buffer: RenderBuffer) {
        val mode: Int = when (buffer.drawMode) {
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
        GlStateManager.color4f(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
    }

    override fun setLineThickness(thickness: Double) {
        GL11.glLineWidth(thickness.toFloat())
    }

    private var createdPrograms = false

    private var imageProgram = 0
    private var imageVao = 0

    private var roundedRectangleProgram = 0
    private var roundedRectangleVao = 0

    private var roundedRectangleBorderProgram = 0
    private var roundedRectangleBorderVao = 0

    private fun createProgram(vertexShaderPath: String, fragmentShaderPath: String): Int {
        val program = GL20.glCreateProgram()
        val vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        try {
            GL20.glShaderSource(vertexShader, IOUtils.toString(Objects.requireNonNull(RendererImpl::class.java.classLoader.getResourceAsStream(vertexShaderPath)), StandardCharsets.UTF_8))
            GL20.glCompileShader(vertexShader)
            val compiled = GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS)
            if (compiled == 0) {
                println(vertexShaderPath)
                System.err.println(GL20.glGetShaderInfoLog(vertexShader, GL20.glGetShaderi(vertexShader, GL20.GL_INFO_LOG_LENGTH)))
                throw IllegalStateException("Failed to compile shader")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        GL20.glAttachShader(program, vertexShader)
        val fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        try {
            GL20.glShaderSource(fragmentShader, IOUtils.toString(Objects.requireNonNull(RendererImpl::class.java.classLoader.getResourceAsStream(fragmentShaderPath)), StandardCharsets.UTF_8))
            GL20.glCompileShader(fragmentShader)
            val compiled = GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS)
            if (compiled == 0) {
                System.err.println(GL20.glGetShaderInfoLog(fragmentShader, GL20.glGetShaderi(fragmentShader, GL20.GL_INFO_LOG_LENGTH)))
                throw IllegalStateException("Failed to compile shader")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        GL20.glAttachShader(program, fragmentShader)

        GL20.glLinkProgram(program)
        val linked = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS)
        if (linked == 0) {
            System.err.println(GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH)))
            throw IllegalStateException("Shader failed to link")
        }
        return program
    }

    private fun createPrograms() {
        if (createdPrograms) return
        createdPrograms = true
        roundedRectangleProgram = createProgram("rounded_rectangle_vertex.glsl", "rectangle_fragment.glsl")
        run {
            roundedRectangleVao = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(roundedRectangleVao)
            val vertices = floatArrayOf(
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    0f, 1f)

            val verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
            verticesBuffer.put(vertices).flip()
            val vboID = GL15.glGenBuffers()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
            GL30.glBindVertexArray(0)
        }
        roundedRectangleBorderProgram = createProgram("rectangle_border_vertex.glsl", "rounded_rectangle_border_fragment.glsl")
        run {
            roundedRectangleBorderVao = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(roundedRectangleBorderVao)
            val vertices = floatArrayOf(
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    0f, 1f)
            val verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
            verticesBuffer.put(vertices).flip()
            val vboID = GL15.glGenBuffers()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
            GL30.glBindVertexArray(0)
        }
        imageProgram = createProgram("image_vertex.glsl", "image_fragment.glsl")
        run {
            imageVao = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(imageVao)
            val vertices = floatArrayOf(
                1f, 1f,
                1f, 0f,
                0f, 0f,
                0f, 0f,
                0f, 1f,
                1f, 1f)
            val verticesBuffer = BufferUtils.createFloatBuffer(vertices.size)
            verticesBuffer.put(vertices).flip()
            val vboID = GL15.glGenBuffers()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0)
            GL30.glBindVertexArray(0)
        }
    }

    override fun drawRectangle(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, topLeftColor: Color, bottomLeftColor: Color, bottomRightColor: Color, topRightColor: Color) {
        if (topLeftColor.rgb == topRightColor.rgb && topRightColor.rgb == bottomRightColor.rgb) {
            this.createPrograms()

            GlStateManager.enableBlend()
            GlStateManager.disableTexture()

            GL20.glUseProgram(roundedRectangleProgram)

            GL30.glBindVertexArray(roundedRectangleVao)
            GL20.glEnableVertexAttribArray(0)

            GlStateManager.color4f(1f, 1f, 1f, 1f)

            val window = Window(MinecraftClient.getInstance())

            GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleProgram, "position1"), x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
            //TODO: Make colors vary for different corners
            GL20.glUniform4f(
                GL20.glGetUniformLocation(roundedRectangleProgram, "colorIn"),
                topLeftColor.red.toFloat(),
                topLeftColor.green.toFloat(),
                topLeftColor.blue.toFloat(),
                topLeftColor.alpha.toFloat()
            )
            GL20.glUniform2f(GL20.glGetUniformLocation(roundedRectangleProgram, "resolutionIn"), window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
            GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleProgram, "cornerRadiusIn"), cornerRadius.toFloat())

            GL11.glDrawArrays(GL11.GL_QUADS, 0, 4)

            GL20.glDisableVertexAttribArray(0)
            GL30.glBindVertexArray(0)
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

            GL20.glUseProgram(0)

            GlStateManager.disableBlend()
            GlStateManager.enableTexture()
        } else {
            GlStateManager.disableTexture()
            GlStateManager.enableBlend()
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

            GlStateManager.color4f(1f, 1f, 1f, 1f)
        }
    }

    override fun drawRectangleBorder(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, thickness: Double, color: Color) {
        // Used to make sure the background rectangle is not showing in front of border
        var x = x
        var y = y
        var width = width
        var height = height

        //x -= 0.05
        //y -= 0.05
        //width += 0.1
        //height += 0.1
        createPrograms()
        GlStateManager.enableBlend()
        GL20.glUseProgram(roundedRectangleBorderProgram)
        GL30.glBindVertexArray(roundedRectangleBorderVao)
        GL20.glEnableVertexAttribArray(0)
        val window = Window(MinecraftClient.getInstance())
        GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "position1"), x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
        GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "colorIn"), color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
        GL20.glUniform2f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "resolutionIn"), window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
        GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "cornerRadiusIn"), cornerRadius.toFloat())
        GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "thicknessIn"), thickness.toFloat())
        GL11.glDrawArrays(GL11.GL_QUADS, 0, 4)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL20.glUseProgram(0)
    }

    private val textures: MutableMap<String, Int> = HashMap()

    private fun getTexture(id: String): Int {
        var texture = textures[id] ?: -1
        if (texture == -1) {
            createTexture(id)
            texture = textures.getOrDefault(id, -1)
        }
        return texture
    }

    private fun setupTextureData(bytes: ByteArray): Array<Any> {
        try {
            val bufferedImage = ImageIO.read(ByteArrayInputStream(bytes))
            val buffer = ByteBuffer.allocateDirect(bufferedImage.width * bufferedImage.height * 4)
            val rgba = IntArray(bufferedImage.width * bufferedImage.height)
            bufferedImage.getRGB(0, 0, bufferedImage.width, bufferedImage.height, rgba, 0, bufferedImage.width)
            for (pixelY in 0 until bufferedImage.height) {
                for (pixelX in 0 until bufferedImage.width) {
                    val rgb = rgba[bufferedImage.width * pixelY + pixelX]
                    var red = rgb shr 16 and 0xFF
                    var green = rgb shr 8 and 0xFF
                    var blue = rgb and 0xFF
                    val alpha = rgb shr 24 and 0xFF
                    if (red == 0 && green == 0 && blue == 0 && alpha == 0) {
                        red = 255
                        green = 255
                        blue = 255
                    }
                    buffer.put(red.toByte())
                    buffer.put(green.toByte())
                    buffer.put(blue.toByte())
                    buffer.put(alpha.toByte())
                }
            }
            buffer.flip()

            return arrayOf(buffer, bufferedImage.width, bufferedImage.height)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null!!
    }

    private fun setupTextureOpenGl(buffer: ByteBuffer, width: Int, height: Int, antialias: Boolean): Int {
        var glId = -1
        try {
            glId = GL11.glGenTextures()
            GlStateManager.bindTexture(glId)
            val filter1: Int
            val filter2: Int
            if (antialias) {
                filter1 = GL11.GL_LINEAR
                filter2 = GL11.GL_LINEAR_MIPMAP_LINEAR
            } else {
                filter1 = GL11.GL_NEAREST
                filter2 = GL11.GL_NEAREST_MIPMAP_NEAREST
            }

            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter1.toFloat())
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter2.toFloat())
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return glId
    }

    override fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, textureX: Double, textureY: Double, textureWidth: Double, textureHeight: Double, antialias: Boolean, color: Color) {
        val glId: Int = getTexture(id)
        if (glId == -1) return

        GlStateManager.bindTexture(glId)
        createPrograms()
        GlStateManager.enableBlend()
        GL20.glUseProgram(imageProgram)
        GL30.glBindVertexArray(imageVao)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        val window = Window(MinecraftClient.getInstance())
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "position1"), x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "colorIn"), color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
        GL20.glUniform2f(GL20.glGetUniformLocation(imageProgram, "resolutionIn"), window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
        GL20.glUniform1f(GL20.glGetUniformLocation(imageProgram, "cornerRadiusIn"), cornerRadius.toFloat())
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "imagePositionIn"), textureX.toFloat(), textureY.toFloat(), textureWidth.toFloat(), textureHeight.toFloat())
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6)
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL20.glUseProgram(0)
        GlStateManager.bindTexture(0)
    }

    override fun createTexture(id: String, bytes: ByteArray, antialias: Boolean) {
        if (this.textures.getOrDefault(id, -1) != -1) return
        Thread {
            val data = setupTextureData(bytes)

            tasks += {
                val texture = setupTextureOpenGl(data[0] as ByteBuffer, data[1] as Int, data[2] as Int, antialias)
                this.textures[id] = texture
            }
        }.start()
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
                    fontRenderer =
                        com.github.sorusclient.client.adapter.v1_8_9.MinecraftFontRenderer(MinecraftClient.getInstance().textRenderer)
                    fontRenderers[id] = fontRenderer
                    fontRenderer
                }
                else -> null
            }
    }

    private val fonts: MutableMap<String, com.github.sorusclient.client.adapter.v1_8_9.RendererImpl.FontData?> = HashMap()

    class FontData {
        var glId = 0
        lateinit var characterData: Array<com.github.sorusclient.client.adapter.v1_8_9.RendererImpl.FontData.CharacterData?>
        var ascent = 0.0

        class CharacterData {
            var textureX = 0.0
            var textureY = 0.0
            var textureWidth = 0.0
            var textureHeight = 0.0
        }
    }

    private fun getFont(id: String): com.github.sorusclient.client.adapter.v1_8_9.RendererImpl.FontData? {
        var fontData = fonts[id]
        if (fontData == null) {
            createFont(id)
            fontData = fonts[id]
        }
        return fontData
    }

    override fun drawText(id: String, text: String, x: Double, y: Double, scale: Double, color: Color) {
        val fontData = getFont(id)
        GlStateManager.bindTexture(fontData!!.glId)
        GlStateManager.disableTexture()
        GlStateManager.enableBlend()
        createPrograms()
        GL20.glUseProgram(imageProgram)
        GL30.glBindVertexArray(imageVao)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        val window = Window(MinecraftClient.getInstance())
        val factor = 200 * scale.toFloat()
        var xOffset = 0.0
        for (character in text.toCharArray()) {
            GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "position1"), (xOffset + x).toFloat(), y.toFloat(), fontData.characterData[character.code]!!.textureWidth.toFloat() * factor, fontData.characterData[character.code]!!.textureHeight.toFloat() * factor)
            GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "colorIn"), color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
            GL20.glUniform2f(GL20.glGetUniformLocation(imageProgram, "resolutionIn"), window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
            GL20.glUniform1f(GL20.glGetUniformLocation(imageProgram, "cornerRadiusIn"), 0f)
            GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "imagePositionIn"), fontData.characterData[character.code]!!.textureX.toFloat(), fontData.characterData[character.code]!!.textureY.toFloat(), fontData.characterData[character.code]!!.textureWidth.toFloat(), fontData.characterData[character.code]!!.textureHeight.toFloat())
            xOffset += fontData.characterData[character.code]!!.textureWidth * factor
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6)
        }
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL20.glUseProgram(0)
    }

    override fun getTextWidth(fontId: String, text: String): Double {
        val fontData = fonts[fontId]
        var width = 0.0
        val factor = 200.0
        for (character in text.toCharArray()) {
            width += fontData!!.characterData[character.code]!!.textureWidth * factor
        }
        return width
    }

    override fun getTextHeight(fontId: String): Double {
        val fontData = fonts[fontId]
        val factor = 200.0
        return fontData!!.ascent * factor
    }

    private fun setupFont(inputStream: InputStream): com.github.sorusclient.client.adapter.v1_8_9.RendererImpl.FontData {
        val fontData = com.github.sorusclient.client.adapter.v1_8_9.RendererImpl.FontData()
        try {
            var font = Font.createFont(Font.TRUETYPE_FONT, inputStream)
            font = font.deriveFont(200f)

            val bufferedImage = BufferedImage(4096, 4096, BufferedImage.TYPE_INT_ARGB)
            val graphics = bufferedImage.graphics as Graphics2D
            val rh = RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

            graphics.setRenderingHints(rh)
            graphics.color = Color(255, 255, 255, 0)
            graphics.drawRect(0, 0, bufferedImage.width, bufferedImage.height)
            graphics.font = font

            val fontMetrics = graphics.fontMetrics
            graphics.color = Color(255, 255, 255, 255)

            var textX = 0
            var textY = 0
            var maxHeight = 0

            fontData.characterData = arrayOfNulls(255)

            fontData.ascent = fontMetrics.ascent.toDouble() / bufferedImage.height * 9 / 12

            for (i in 0..254) {
                val character = i.toChar()
                val weirdAscent = (fontMetrics.ascent.toDouble() * 9 / 12).toInt()
                val bounds = fontMetrics.getStringBounds(character.toString(), graphics)

                graphics.drawString(character.toString(), textX, textY + weirdAscent)

                val characterData = com.github.sorusclient.client.adapter.v1_8_9.RendererImpl.FontData.CharacterData()
                characterData.textureX = textX.toDouble() / bufferedImage.width
                characterData.textureY = textY.toDouble() / bufferedImage.height
                characterData.textureWidth = bounds.width / bufferedImage.width
                characterData.textureHeight = bounds.height / bufferedImage.height

                fontData.characterData[i] = characterData

                textX += (bounds.width + 15).toInt()
                maxHeight = maxHeight.toDouble().coerceAtLeast(bounds.height * 6 / 5).toInt()
                if (textX > 4096 - 300) {
                    textX = 15
                    textY += maxHeight
                    maxHeight = 0
                }
            }

            fontData.glId = GL11.glGenTextures()

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontData.glId)
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR.toFloat())
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST.toFloat())

            val buffer = ByteBuffer.allocateDirect(bufferedImage.width * bufferedImage.height * 4)
            val rgba = IntArray(bufferedImage.width * bufferedImage.height)
            bufferedImage.getRGB(0, 0, bufferedImage.width, bufferedImage.height, rgba, 0, bufferedImage.width)

            for (pixelY in 0 until bufferedImage.height) {
                for (pixelX in 0 until bufferedImage.width) {
                    val rgb = rgba[bufferedImage.width * pixelY + pixelX]
                    var red = rgb shr 16 and 0xFF
                    var green = rgb shr 8 and 0xFF
                    var blue = rgb and 0xFF
                    val alpha = rgb shr 24 and 0xFF
                    if (red == 0 && green == 0 && blue == 0 && alpha == 0) {
                        red = 255
                        green = 255
                        blue = 255
                    }
                    buffer.put(red.toByte())
                    buffer.put(green.toByte())
                    buffer.put(blue.toByte())
                    buffer.put(alpha.toByte())
                }
            }
            buffer.flip()

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, bufferedImage.width, bufferedImage.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        } catch (e: FontFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fontData
    }

    override fun createFont(id: String, inputStream: InputStream) {
        if (fonts[id] != null) return
        val fontData = setupFont(inputStream)
        fonts[id] = fontData
    }

    private val loadShader: Method

    init {
        val loadShader = "v1_8_9/net/minecraft/client/render/GameRenderer#loadShader(Lv1_8_9/net/minecraft/util/Identifier;)V".toIdentifier()
        this.loadShader = GameRenderer::class.java.getDeclaredMethod(loadShader.methodName, v1_8_9.net.minecraft.util.Identifier::class.java)
        this.loadShader.isAccessible = true
    }

    override fun loadBlur() {
        loadShader.invoke(MinecraftClient.getInstance().gameRenderer, v1_8_9.net.minecraft.util.Identifier("sorus/shaders/blur.json"))
    }

    override fun unloadBlur() {
        MinecraftClient.getInstance().gameRenderer.disableShader()
    }

}