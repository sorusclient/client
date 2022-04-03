package com.github.sorusclient.client.feature.impl.oldanimations.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class OldAnimationsTransformer : Transformer() {

    init {
        register("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer", this::transformHeldItemRenderer)
        register("v1_8_9/net/minecraft/client/MinecraftClient", this::transformMinecraftClient)
        register("v1_8_9/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer", this::transformArmorFeatureRenderer)
    }

    private fun transformHeldItemRenderer(classNode: ClassNode) {
        val method1354 = "v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_1354(F)V".toIdentifier()
        val method9873 = "v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_9873(FF)V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == method1354.methodName && methodNode.desc == method1354.methodDesc) {
                for (node in methodNode.instructions) {
                    if (node is MethodInsnNode && node.name == method9873.methodName && node.desc == method9873.methodDesc && node.getPrevious().opcode == Opcodes.T_LONG) {
                        methodNode.instructions.remove(node.getPrevious())
                        val insnList = InsnList()
                        insnList.add(VarInsnNode(Opcodes.FLOAD, 4))
                        insnList.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                OldAnimationsHook::class.java.name.replace(".", "/"),
                                "getPartialTicks",
                                "(F)F"
                            )
                        )
                        methodNode.instructions.insertBefore(node, insnList)
                    }
                }
            }
        }
    }

    private fun transformMinecraftClient(classNode: ClassNode) {
        val tick = "v1_8_9/net/minecraft/client/MinecraftClient#tick()V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == tick.methodName && methodNode.desc == tick.methodDesc) {
                methodNode.instructions.insert(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        OldAnimationsHook::class.java.name.replace(".", "/"),
                        "updateSwing",
                        "()V"
                    )
                )
            }
        }
    }

    private fun transformArmorFeatureRenderer(classNode: ClassNode) {
        val combineTextures =
            "v1_8_9/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer#combineTextures()Z".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == combineTextures.methodName && methodNode.desc == combineTextures.methodDesc) {
                val insnList = InsnList()
                insnList.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        OldAnimationsHook::class.java.name.replace(".", "/"),
                        "showArmorDamage",
                        "()Z"
                    )
                )
                insnList.add(InsnNode(Opcodes.IRETURN))
                methodNode.instructions.insert(insnList)
            }
        }
    }

}