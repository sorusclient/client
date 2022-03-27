package com.github.sorusclient.client.feature.impl.oldanimations.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class OldAnimationsTransformer : Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(this.javaClass)
    }

    init {
        register("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer") { classNode: ClassNode ->
            transformHeldItemRenderer(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/MinecraftClient") { classNode: ClassNode ->
            transformMinecraftClient(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer") { classNode: ClassNode ->
            transformArmorFeatureRenderer(
                classNode
            )
        }
    }

    private fun transformHeldItemRenderer(classNode: ClassNode) {
        val method1354 = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_1354(F)V")
        val method9873 = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_9873(FF)V")
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
        val tick = Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient#tick()V")
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
            Identifier.parse("v1_8_9/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer#combineTextures()Z")
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