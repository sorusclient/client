package com.github.sorusclient.client.feature.impl.blockoverlay.v1_18_1

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Point
import com.github.sorusclient.client.adapter.RenderBuffer
import com.github.sorusclient.client.adapter.Vertex
import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.blockoverlay.BlockOverlay
import com.github.sorusclient.client.util.Color
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.util.math.MatrixStack
import v1_18_1.net.minecraft.util.hit.BlockHitResult
import v1_18_1.net.minecraft.util.shape.VoxelShape

object BlockOverlayHook {

    //TODO: Actually get this working in 1.18.1
    @JvmStatic
    @Suppress("Unused")
    fun onBlockOverlayRender(matrixStack: MatrixStack, voxelShape: VoxelShape, x: Double, y: Double, z: Double) {
        val blockOverlay = FeatureManager.get<BlockOverlay>()
        if (!blockOverlay.enabled.value) {
            return
        }

        //val position = matrixStack.peek().positionMatrix
        //val floatBuffer = FloatBuffer.allocate(4)
        //position.read(floatBuffer, true)

        val position = (MinecraftClient.getInstance().crosshairTarget as BlockHitResult).blockPos
        val state = MinecraftClient.getInstance().world!!.getBlockState((MinecraftClient.getInstance().crosshairTarget as BlockHitResult).blockPos)
        val voxelShape = state.getOutlineShape(MinecraftClient.getInstance().world!!, position)

        voxelShape.forEachBox { boxX: Double, boxY: Double, boxZ: Double, boxWidth: Double, boxHeight: Double, boxDepth: Double ->
            val minX = position.x + boxX
            val maxX = position.x + boxX + boxWidth
            val minY = position.y + boxY
            val maxY = position.y + boxY + boxHeight
            val minZ = position.z + boxZ
            val maxZ = position.z + boxZ + boxDepth
            
            val buffer = RenderBuffer()
            buffer.drawMode = RenderBuffer.DrawMode.QUAD
            val fillColor = blockOverlay.fillColor.value
            buffer.push(Vertex().setPoint(Point(0.0, 0.0, 0.0)).setColor(Color.fromRGB(255, 0, 0, 255)))
            buffer.push(Vertex().setPoint(Point(0.0, 100.0, 0.0)).setColor(Color.fromRGB(255, 0, 0, 255)))
            buffer.push(Vertex().setPoint(Point(100.0, 100.0, 0.0)).setColor(Color.fromRGB(255, 0, 0, 255)))
            buffer.push(Vertex().setPoint(Point(100.0, 0.0, 0.0)).setColor(Color.fromRGB(255, 0, 0, 255)))
            /*buffer.push(Vertex().setPoint(Point(minX, minY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, maxY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, maxY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, minY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, minY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, maxY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, maxY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, minY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, minY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, maxY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, maxY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, minY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, minY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, maxY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, maxY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, minY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, maxY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, maxY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, maxY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, maxY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, minY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, minY, maxZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(minX, minY, minZ)).setColor(fillColor))
            buffer.push(Vertex().setPoint(Point(maxX, minY, minZ)).setColor(fillColor))*/
            val renderer = AdapterManager.getAdapter().renderer
            renderer.draw(buffer)
        }

        /*val box = com.github.sorusclient.client.adapter.Box(
            voxelShape.getMin(Direction.Axis.X),
            voxelShape.getMax(Direction.Axis.X),
            voxelShape.getMin(Direction.Axis.Y),
            voxelShape.getMax(Direction.Axis.Y),
            voxelShape.getMin(Direction.Axis.Z),
            voxelShape.getMax(Direction.Axis.Z),
        )

        val buffer = RenderBuffer()
        buffer.drawMode = RenderBuffer.DrawMode.QUAD
        val fillColor = blockOverlay.fillColor.value
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.minZ)).setColor(fillColor))
        val renderer = AdapterManager.getAdapter().renderer
        renderer.draw(buffer)*/

        //GL11.glLineWidth(blockOverlay.borderThickness.value.toFloat())
        //val borderColor = blockOverlay.borderColor.value
        //GL11.glColor4f(borderColor.red.toFloat(), borderColor.green.toFloat(), borderColor.blue.toFloat(), borderColor.alpha.toFloat())
    }

}