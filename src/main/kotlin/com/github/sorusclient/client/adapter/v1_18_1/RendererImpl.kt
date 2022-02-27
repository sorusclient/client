package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.adapter.IRenderer
import com.github.sorusclient.client.adapter.RenderBuffer
import com.github.sorusclient.client.adapter.v1_8_9.RendererImpl
import com.github.sorusclient.client.util.Color
import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.*
import v1_18_1.com.mojang.blaze3d.systems.RenderSystem
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.render.Tessellator
import v1_18_1.net.minecraft.client.render.VertexFormat
import v1_18_1.net.minecraft.client.render.VertexFormats
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class RendererImpl: IRenderer {

    override fun draw(buffer: RenderBuffer) {
        TODO("Not yet implemented")
    }

    override fun setColor(color: Color) {
        TODO("Not yet implemented")
    }

    override fun setLineThickness(thickness: Double) {
        TODO("Not yet implemented")
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
                0f, 1f)

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
                0f, 1f)
            val verticesBuffer = ByteBuffer.allocateDirect(vertices.size shl 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
            verticesBuffer.put(vertices).flip()

            val vboID = GL15.glGenBuffers()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0)
            GL30.glBindVertexArray(0)
            GL30.glBindVertexArray(1)
        }
    }

    override fun drawRectangle(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, topLeftColor: Color, bottomLeftColor: Color, bottomRightColor: Color, topRightColor: Color) {
        if (topLeftColor.rgb == topRightColor.rgb && topRightColor.rgb == bottomRightColor.rgb) {
            this.createPrograms()

            RenderSystem.enableBlend()
            RenderSystem.disableTexture()

            GL20.glUseProgram(roundedRectangleProgram)

            GL30.glBindVertexArray(roundedRectangleVao)
            GL20.glEnableVertexAttribArray(0)

            //GlStateManager.color4f(1f, 1f, 1f, 1f)

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
            GL20.glUniform2f(3, window.scaledWidth.toFloat(), window.scaledHeight.toFloat())
            GL20.glUniform1f(4, cornerRadius.toFloat())

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6)

            GL20.glDisableVertexAttribArray(0)
            GL30.glBindVertexArray(0)
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

            GL20.glUseProgram(0)

            RenderSystem.disableBlend()
            RenderSystem.enableTexture()
        } else {
            RenderSystem.disableTexture()
            RenderSystem.enableBlend()
            //GlStateManager.shadeModel(GL11.GL_SMOOTH)
            val tessellator = Tessellator.getInstance()
            val bufferBuilder = tessellator.buffer
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
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

            //GlStateManager.color4f(1f, 1f, 1f, 1f)
        }
    }

    override fun drawRectangleBorder(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        cornerRadius: Double,
        thickness: Double,
        color: Color
    ) {
        TODO("Not yet implemented")
    }

    override fun drawImage(
        id: String,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        cornerRadius: Double,
        antialias: Boolean,
        color: Color
    ) {
        TODO("Not yet implemented")
    }

    override fun createTexture(id: String, bytes: ByteArray, antialias: Boolean) {
        TODO("Not yet implemented")
    }

    override fun scissor(x: Double, y: Double, width: Double, height: Double) {
        TODO("Not yet implemented")
    }

    override fun endScissor() {
        TODO("Not yet implemented")
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

    override fun drawText(id: String, text: String, x: Double, y: Double, scale: Double, color: Color) {
        TODO("Not yet implemented")
    }

    override fun getTextWidth(fontId: String, text: String): Double {
        TODO("Not yet implemented")
    }

    override fun getTextHeight(fontId: String): Double {
        TODO("Not yet implemented")
    }

    override fun createFont(id: String, inputStream: InputStream) {
        TODO("Not yet implemented")
    }

    override fun loadBlur() {
        TODO("Not yet implemented")
    }

    override fun unloadBlur() {
        TODO("Not yet implemented")
    }

}