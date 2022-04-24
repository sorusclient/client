/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.adapter.IRenderer
import com.github.sorusclient.client.adapter.RenderBuffer
import com.github.sorusclient.client.adapter.v1_18_2.event.EventHook
import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.util.Color
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.render.GameRenderer
import v1_18_2.net.minecraft.client.render.Tessellator
import v1_18_2.net.minecraft.client.render.VertexFormat
import v1_18_2.net.minecraft.client.render.VertexFormats
import v1_18_2.org.apache.commons.io.IOUtils
import v1_18_2.org.lwjgl.opengl.GL11
import v1_18_2.org.lwjgl.opengl.GL15
import v1_18_2.org.lwjgl.opengl.GL20
import v1_18_2.org.lwjgl.opengl.GL30
import java.awt.Font
import java.awt.FontFormatException
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import javax.imageio.ImageIO

class RendererImpl: IRenderer {

    override fun draw(buffer: RenderBuffer) {
        val mode = when (buffer.drawMode) {
            RenderBuffer.DrawMode.QUAD -> VertexFormat.DrawMode.QUADS
            else -> null!!
        }
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(mode, VertexFormats.POSITION_COLOR)
        for (vertex in buffer.vertices) {
            val point = vertex.point
            val color = vertex.color
            bufferBuilder.vertex(point.x, point.y, point.z).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        }
        tessellator.draw()
    }

    override fun setColor(color: Color) {
        GL11.glColor4f(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
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

    private var rectangleColoredProgram = 0
    private var rectangleColoredVao = 0

    private fun createProgram(vertexShaderPath: String, fragmentShaderPath: String): Int {
        val program = GL20.glCreateProgram()
        val vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        try {
            GL20.glShaderSource(vertexShader, IOUtils.toString(Objects.requireNonNull(RendererImpl::class.java.classLoader.getResourceAsStream(vertexShaderPath))))
            GL20.glCompileShader(vertexShader)
            val compiled = GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS)
            if (compiled == 0) {
                System.err.println(GL20.glGetShaderInfoLog(vertexShader, GL20.glGetShaderi(vertexShader, GL20.GL_INFO_LOG_LENGTH)))
                throw IllegalStateException("Failed to compile shader")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        GL20.glAttachShader(program, vertexShader)
        val fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        try {
            GL20.glShaderSource(fragmentShader, IOUtils.toString(Objects.requireNonNull(RendererImpl::class.java.classLoader.getResourceAsStream(fragmentShaderPath))))
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
                0f, 0f,
                0f, 1f,
                1f, 1f
            )

            val verticesBuffer = ByteBuffer.allocateDirect(vertices.size shl 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
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
                0f, 0f,
                0f, 1f,
                1f, 1f
            )

            val verticesBuffer = ByteBuffer.allocateDirect(vertices.size shl 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
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
                1f, 1f
            )
            val verticesBuffer = ByteBuffer.allocateDirect(vertices.size shl 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
            verticesBuffer.put(vertices).flip()

            val vboID = GL15.glGenBuffers()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0)
            GL30.glBindVertexArray(0)
        }
        rectangleColoredProgram = createProgram("colored_rectangle_vertex.glsl", "colored_rectangle_fragment.glsl")
        run {
            rectangleColoredVao = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(rectangleColoredVao)
            val vertices = floatArrayOf(
                1f, 1f,
                1f, 0f,
                0f, 0f,
                0f, 0f,
                0f, 1f,
                1f, 1f
            )

            val verticesBuffer = ByteBuffer.allocateDirect(vertices.size shl 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
            verticesBuffer.put(vertices).flip()

            val vboID = GL15.glGenBuffers()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
            GL30.glBindVertexArray(0)
        }
    }

    override fun drawRectangle(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, topLeftColor: Color, bottomLeftColor: Color, bottomRightColor: Color, topRightColor: Color) {
        val prevProgram = EventHook.lastBoundProgram
        val prevBoundVertexArray = EventHook.lastBoundArray
        val prevBoundBuffer = EventHook.lastBoundBuffer
        val prevBoundBufferTarget = EventHook.lastBoundBufferTarget

        if (topLeftColor.rgb == topRightColor.rgb && topRightColor.rgb == bottomRightColor.rgb) {
            this.createPrograms()

            RenderSystem.enableBlend()
            RenderSystem.disableTexture()

            GL20.glUseProgram(roundedRectangleProgram)

            GL30.glBindVertexArray(roundedRectangleVao)
            GL20.glEnableVertexAttribArray(0)

            val window = MinecraftClient.getInstance().window

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

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6)

            GL20.glDisableVertexAttribArray(0)

            GL20.glUseProgram(0)

            RenderSystem.disableBlend()
            RenderSystem.enableTexture()
        } else {
            this.createPrograms()

            RenderSystem.enableBlend()
            RenderSystem.disableTexture()

            GL20.glUseProgram(rectangleColoredProgram)

            GL30.glBindVertexArray(rectangleColoredVao)
            GL20.glEnableVertexAttribArray(0)

            val window = MinecraftClient.getInstance().window

            GL20.glUniform4f(1, x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
            //TODO: Make colors vary for different corners
            GL20.glUniform4f(
                2,
                topLeftColor.red.toFloat(),
                topLeftColor.green.toFloat(),
                topLeftColor.blue.toFloat(),
                topLeftColor.alpha.toFloat()
            )
            GL20.glUniform4f(
                3,
                topRightColor.red.toFloat(),
                topRightColor.green.toFloat(),
                topRightColor.blue.toFloat(),
                topRightColor.alpha.toFloat()
            )
            GL20.glUniform4f(
                4,
                bottomRightColor.red.toFloat(),
                bottomRightColor.green.toFloat(),
                bottomRightColor.blue.toFloat(),
                bottomRightColor.alpha.toFloat()
            )
            GL20.glUniform4f(
                5,
                bottomLeftColor.red.toFloat(),
                bottomLeftColor.green.toFloat(),
                bottomLeftColor.blue.toFloat(),
                bottomLeftColor.alpha.toFloat()
            )
            GL20.glUniform2f(6, window.scaledWidth.toFloat(), window.scaledHeight.toFloat())

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6)

            GL20.glDisableVertexAttribArray(0)

            RenderSystem.disableBlend()
            RenderSystem.enableTexture()
        }

        GL30.glBindVertexArray(prevBoundVertexArray)
        GL15.glBindBuffer(prevBoundBufferTarget, prevBoundBuffer)
        GL20.glUseProgram(prevProgram)
    }

    override fun drawRectangleBorder(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, thickness: Double, color: Color) {
        var x = x
        var y = y
        var width = width
        var height = height

        x -= 0.1
        y -= 0.1
        width += 0.2
        height += 0.2
        createPrograms()
        RenderSystem.enableBlend()
        GL20.glUseProgram(roundedRectangleBorderProgram)
        GL30.glBindVertexArray(roundedRectangleBorderVao)
        GL20.glEnableVertexAttribArray(0)
        val window = MinecraftClient.getInstance().window
        GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "position1"), x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
        GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "colorIn"), color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
        GL20.glUniform2f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "resolutionIn"), window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
        GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "cornerRadiusIn"), cornerRadius.toFloat())
        GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "thicknessIn"), thickness.toFloat())
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL20.glUseProgram(0)
    }

    override fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double, width: Double, color: Color) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        RenderSystem.lineWidth(width.toFloat())
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR)
        buffer.vertex(x1, y1, 0.0).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(x2, y2, 0.0).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()

        tessellator.draw()
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

    private fun setupTexture(bytes: ByteArray, antialias: Boolean): Int {
        var glId = -1
        try {
            val bufferedImage = ImageIO.read(ByteArrayInputStream(bytes))
            glId = GL11.glGenTextures()
            RenderSystem.bindTexture(glId)

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
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return glId
    }

    override fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, textureX: Double, textureY: Double, textureWidth: Double, textureHeight: Double, antialias: Boolean, color: Color) {
        val glId: Int = getTexture(id)
        if (glId == -1) return

        RenderSystem.bindTexture(glId)
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()

        createPrograms()

        GL20.glUseProgram(imageProgram)
        GL30.glBindVertexArray(imageVao)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        val window = MinecraftClient.getInstance().window
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "position1"), x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "colorIn"), color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
        GL20.glUniform2f(GL20.glGetUniformLocation(imageProgram, "resolutionIn"), window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
        GL20.glUniform1f(GL20.glGetUniformLocation(imageProgram, "cornerRadiusIn"), cornerRadius.toFloat())
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "imagePositionIn"), textureX.toFloat(), textureY.toFloat(), textureWidth.toFloat(), textureHeight.toFloat())
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL20.glUseProgram(0)

        RenderSystem.bindTexture(0)
    }

    override fun createTexture(id: String, bytes: ByteArray, antialias: Boolean) {
        if (this.textures.getOrDefault(id, -1) != -1) return
        val texture = setupTexture(bytes, antialias)
        this.textures[id] = texture
    }

    override fun scissor(x: Double, y: Double, width: Double, height: Double) {
        val window = MinecraftClient.getInstance().window

        RenderSystem.enableScissor(
            (x * window.width / window.scaledWidth).toInt(),
            ((window.scaledHeight - (y + height)) * window.height / window.scaledHeight).toInt(),
            (width * window.width / window.scaledWidth).toInt(),
            (height * window.height / window.scaledHeight).toInt()
        )
    }

    override fun endScissor() {
        RenderSystem.disableScissor()
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

    private val fonts: MutableMap<String, FontData?> = HashMap()

    class FontData {
        var glId = 0
        lateinit var characterData: Array<CharacterData?>
        var ascent = 0.0

        class CharacterData {
            var textureX = 0.0
            var textureY = 0.0
            var textureWidth = 0.0
            var textureHeight = 0.0
        }
    }

    private fun getFont(id: String): FontData? {
        var fontData = fonts[id]
        if (fontData == null) {
            createFont(id)
            fontData = fonts[id]
        }
        return fontData
    }

    override fun drawText(id: String, text: String, x: Double, y: Double, scale: Double, color: Color) {
        val fontData = getFont(id)
        RenderSystem.bindTexture(fontData!!.glId)
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
        createPrograms()
        GL20.glUseProgram(imageProgram)
        GL30.glBindVertexArray(imageVao)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        val window = MinecraftClient.getInstance().window
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

    override fun createFont(id: String, inputStream: InputStream) {
        if (fonts[id] != null) return
        val fontData = setupFont(inputStream)
        fonts[id] = fontData
    }

    private fun setupFont(inputStream: InputStream): FontData {
        val fontData = FontData()
        try {
            var font = Font.createFont(Font.TRUETYPE_FONT, inputStream)
            font = font.deriveFont(200f)

            val bufferedImage = BufferedImage(4096, 4096, BufferedImage.TYPE_INT_ARGB)
            val graphics = bufferedImage.graphics as Graphics2D
            val rh = RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

            graphics.setRenderingHints(rh)
            graphics.color = java.awt.Color(255, 255, 255, 0)
            graphics.drawRect(0, 0, bufferedImage.width, bufferedImage.height)
            graphics.font = font

            val fontMetrics = graphics.fontMetrics
            graphics.color = java.awt.Color(255, 255, 255, 255)

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

                val characterData = FontData.CharacterData()
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

    private val loadShader: Method

    init {
        val loadShader = "v1_18_2/net/minecraft/client/render/GameRenderer#loadShader(Lv1_18_2/net/minecraft/util/Identifier;)V".toIdentifier()
        this.loadShader = GameRenderer::class.java.getDeclaredMethod(loadShader.methodName, v1_18_2.net.minecraft.util.Identifier::class.java)
        this.loadShader.isAccessible = true
    }

    override fun loadBlur() {
        loadShader.invoke(MinecraftClient.getInstance().gameRenderer, v1_18_2.net.minecraft.util.Identifier("sorus/shaders/blur.json"))
    }

    override fun unloadBlur() {
        MinecraftClient.getInstance().gameRenderer.disableShader()
    }

}