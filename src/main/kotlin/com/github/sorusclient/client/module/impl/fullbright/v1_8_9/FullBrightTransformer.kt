package com.github.sorusclient.client.module.impl.fullbright.v1_8_9

import com.github.glassmc.loader.GlassLoader
import com.github.glassmc.loader.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode

class FullBrightTransformer : Transformer(), Listener {
    override fun run() {
        GlassLoader.getInstance().registerTransformer(FullBrightTransformer::class.java)
    }

    init {
        register("v1_8_9/net/minecraft/client/render/GameRenderer") { classNode: ClassNode ->
            transformGameRenderer(
                classNode
            )
        }
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val updateLightmap = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#updateLightmap(F)V")

        for (methodNode in classNode.methods) {
            if (methodNode.name == updateLightmap.methodName && methodNode.desc == updateLightmap.methodDesc) {
                for (node in methodNode.instructions.toArray()) {
                    if (node.opcode == Opcodes.FSTORE && (node as VarInsnNode).`var` == 17 && node.getPrevious() is FieldInsnNode) {
                        methodNode.instructions.insertBefore(
                            node,
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                FullBrightHook::class.java.name.replace(".", "/"),
                                "modifyGamma",
                                "(F)F"
                            )
                        )
                    }
                }
            }
        }
    }
}