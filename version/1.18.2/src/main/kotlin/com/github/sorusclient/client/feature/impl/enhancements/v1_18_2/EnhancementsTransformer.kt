package com.github.sorusclient.client.feature.impl.enhancements.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import com.github.sorusclient.client.transform.findMethod
import com.github.sorusclient.client.transform.findReturns
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class EnhancementsTransformer : Transformer() {

    init {
        this.setHookClass(EnhancementsHook::class.java)

        register("v1_18_2/net/minecraft/client/gui/hud/InGameOverlayRenderer", this::transformInGameOverlayRenderer)
        register("v1_18_2/net/minecraft/client/option/GameOptions", this::transformGameOptions)
    }

    private fun transformInGameOverlayRenderer(classNode: ClassNode) {
        val renderFireOverlay = "v1_18_2/net/minecraft/client/gui/hud/InGameOverlayRenderer#renderFireOverlay(Lv1_18_2/net/minecraft/client/MinecraftClient;Lv1_18_2/net/minecraft/client/util/math/MatrixStack;)V".toIdentifier()

        classNode.findMethod(renderFireOverlay)
            .apply(Applier.Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(this.getHook("onPreRenderFire"))
            }))

        classNode.findMethod(renderFireOverlay)
            .apply { methodNode ->
                methodNode.findReturns()
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(this.getHook("onPostRenderFire"))
                    }))
            }
    }

    private fun transformGameOptions(classNode: ClassNode) {
        val load = "v1_18_2/net/minecraft/client/option/GameOptions#load()V".toIdentifier()
        val write = "v1_18_2/net/minecraft/client/option/GameOptions#write()V".toIdentifier()

        classNode.findMethod(write)
            .apply { methodNode ->
                methodNode.findReturns()
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onWrite"))
                    }))
            }

        classNode.findMethod(load)
            .apply { methodNode ->
                methodNode.findReturns()
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                        insnList.add(this.getHook("onLoad"))
                    }))
            }
    }

}