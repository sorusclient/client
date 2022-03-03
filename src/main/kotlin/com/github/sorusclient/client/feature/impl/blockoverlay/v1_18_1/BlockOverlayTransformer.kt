package com.github.sorusclient.client.feature.impl.blockoverlay.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class BlockOverlayTransformer: Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(BlockOverlayTransformer::class.java)
    }

    init {
        setHookClass(BlockOverlayHook::class.java)
        register("v1_18_1/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer)
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val drawShapeOutline = Identifier.parse("v1_18_1/net/minecraft/client/render/WorldRenderer#drawShapeOutline(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;Lv1_18_1/net/minecraft/client/render/VertexConsumer;Lv1_18_1/net/minecraft/util/shape/VoxelShape;DDDFFFF)V")

        findMethod(classNode, drawShapeOutline)
            .apply(Applier.Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 2))
                insnList.add(VarInsnNode(Opcodes.DLOAD, 3))
                insnList.add(VarInsnNode(Opcodes.DLOAD, 5))
                insnList.add(VarInsnNode(Opcodes.DLOAD, 7))
                insnList.add(this.getHook("onBlockOverlayRender"))
            }))
    }

}