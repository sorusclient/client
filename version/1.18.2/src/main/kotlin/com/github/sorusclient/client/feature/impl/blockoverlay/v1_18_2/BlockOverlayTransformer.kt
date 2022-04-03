package com.github.sorusclient.client.feature.impl.blockoverlay.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import com.github.sorusclient.client.transform.findMethod
import com.github.sorusclient.client.transform.findMethodCalls
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class BlockOverlayTransformer: Transformer() {

    init {
        setHookClass(BlockOverlayHook::class.java)
        register("v1_18_2/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer)
        register("v1_18_2/com/mojang/blaze3d/systems/RenderSystem", this::transformRenderSystem)
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val render = "v1_18_2/net/minecraft/client/render/WorldRenderer#render(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;FJZLv1_18_2/net/minecraft/client/render/Camera;Lv1_18_2/net/minecraft/client/render/GameRenderer;Lv1_18_2/net/minecraft/client/render/LightmapTextureManager;Lv1_18_2/net/minecraft/util/math/Matrix4f;)V".toIdentifier()
        val drawBlockOutline = "v1_18_2/net/minecraft/client/render/WorldRenderer#drawBlockOutline(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;Lv1_18_2/net/minecraft/client/render/VertexConsumer;Lv1_18_2/net/minecraft/entity/Entity;DDDLv1_18_2/net/minecraft/util/math/BlockPos;Lv1_18_2/net/minecraft/block/BlockState;)V".toIdentifier()

        classNode.findMethod(render)
            .apply { methodNode ->
                methodNode.findMethodCalls(drawBlockOutline)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 6))
                        insnList.add(this.getHook("render"))
                    }))
            }

        classNode.findMethod(drawBlockOutline)
            .apply { methodNode ->
                var i = 0
                for (node in methodNode.instructions) {
                    if (node.opcode == Opcodes.FCONST_0) {
                        methodNode.instructions.insert(node, this.getHook(when (i) {
                            0 -> "modifyOutlineRed"
                            1 -> "modifyOutlineGreen"
                            2 -> "modifyOutlineBlue"
                            else -> null!!
                        }))
                        i++
                    }

                    if (node is LdcInsnNode && node.cst == 0.4f) {
                        methodNode.instructions.insert(node, this.getHook("modifyOutlineAlpha"))
                    }
                }
            }
    }

    private fun transformRenderSystem(classNode: ClassNode) {
        val lineWidth = "v1_18_2/com/mojang/blaze3d/systems/RenderSystem#lineWidth(F)V".toIdentifier()

        classNode.findMethod(lineWidth)
            .apply(Applier.Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.FLOAD, 0))
                insnList.add(this.getHook("modifyLineWidth"))
                insnList.add(VarInsnNode(Opcodes.FSTORE, 0))
            }))
    }

}