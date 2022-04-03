package com.github.sorusclient.client.feature.impl.blockoverlay.v1_8_9

import com.github.sorusclient.client.Identifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class BlockOverlayTransformer: Transformer() {

    init {
        setHookClass(BlockOverlayHook::class.java)
        register("v1_8_9/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer)
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val method9895 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9895(Lv1_8_9/net/minecraft/util/math/Box;)V")
        findMethod(classNode, method9895)
            .apply(Applier.Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(this.getHook("onBlockOverlayRender"))
            }))
    }

}