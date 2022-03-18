package com.github.sorusclient.client.feature.impl.enhancements.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class EnhancementsTransformer : Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(EnhancementsTransformer::class.java)
    }

    init {
        this.setHookClass(EnhancementsHook::class.java)

        register("v1_18_1/net/minecraft/client/option/GameOptions") { classNode: ClassNode ->
            transformGameOptions(
                classNode
            )
        }
    }

    private fun transformGameOptions(classNode: ClassNode) {
        val load = Identifier.parse("v1_18_1/net/minecraft/client/option/GameOptions#load()V")
        val write = Identifier.parse("v1_18_1/net/minecraft/client/option/GameOptions#write()V")

        findMethod(classNode, write)
            .apply { methodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onWrite"))
                    }))
            }

        findMethod(classNode, load)
            .apply { methodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                        insnList.add(this.getHook("onLoad"))
                    }))
            }
    }

}