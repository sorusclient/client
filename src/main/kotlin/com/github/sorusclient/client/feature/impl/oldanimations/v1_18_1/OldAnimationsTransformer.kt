package com.github.sorusclient.client.feature.impl.oldanimations.v1_18_1

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
        /*register("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer") { classNode: ClassNode ->
            transformHeldItemRenderer(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/MinecraftClient") { classNode: ClassNode ->
            transformMinecraftClient(
                classNode
            )
        }*/
        setHookClass(OldAnimationsHook::class.java)
        register("v1_18_1/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer") { classNode: ClassNode ->
            transformArmorFeatureRenderer(
                classNode
            )
        }
    }

    /*private fun transformHeldItemRenderer(classNode: ClassNode) {
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
    }*/

    private fun transformArmorFeatureRenderer(classNode: ClassNode) {
        val renderArmor = Identifier.parse("v1_18_1/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer#renderArmor(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;Lv1_18_1/net/minecraft/client/render/VertexConsumerProvider;Lv1_18_1/net/minecraft/entity/LivingEntity;Lv1_18_1/net/minecraft/entity/EquipmentSlot;ILv1_18_1/net/minecraft/client/render/entity/model/BipedEntityModel;)V")

        findMethod(classNode, renderArmor)
            .apply { methodNode ->
                methodNode.instructions.insert(this.getHook("init"))
                methodNode.instructions.insert(VarInsnNode(Opcodes.ALOAD, 3))
                var i = 0
                for (node in methodNode.instructions) {
                    if (node.opcode == Opcodes.FCONST_1) {
                        if (i % 3 == 0) {
                            methodNode.instructions.insert(node, this.getHook("modifyRedColor"))
                        } else if (i % 3 == 1) {
                            methodNode.instructions.insert(node, this.getHook("modifyGreenColor"))
                        } else if (i % 3 == 2) {
                            methodNode.instructions.insert(node, this.getHook("modifyBlueColor"))
                        }

                        i++
                    }
                }
            }
    }

}