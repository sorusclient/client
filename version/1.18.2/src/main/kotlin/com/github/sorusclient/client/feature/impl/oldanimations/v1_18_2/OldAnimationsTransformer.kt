package com.github.sorusclient.client.feature.impl.oldanimations.v1_18_2

import com.github.sorusclient.client.Identifier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class OldAnimationsTransformer : Transformer() {

    init {
        setHookClass(OldAnimationsHook::class.java)
        register("v1_18_2/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer") { classNode: ClassNode ->
            transformArmorFeatureRenderer(
                classNode
            )
        }
    }

    private fun transformArmorFeatureRenderer(classNode: ClassNode) {
        val renderArmor = Identifier.parse("v1_18_2/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer#renderArmor(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;Lv1_18_2/net/minecraft/client/render/VertexConsumerProvider;Lv1_18_2/net/minecraft/entity/LivingEntity;Lv1_18_2/net/minecraft/entity/EquipmentSlot;ILv1_18_2/net/minecraft/client/render/entity/model/BipedEntityModel;)V")

        findMethod(classNode, renderArmor)
            .apply { methodNode ->
                methodNode.instructions.insert(this.getHook("init"))
                methodNode.instructions.insert(VarInsnNode(Opcodes.ALOAD, 3))
                var i = 0
                for (node in methodNode.instructions) {
                    if (node is VarInsnNode && node.opcode == Opcodes.FLOAD) {
                        when (node.`var`) {
                            12 -> methodNode.instructions.insert(node, this.getHook("modifyRedColor"))
                            13 -> methodNode.instructions.insert(node, this.getHook("modifyGreenColor"))
                            14 -> methodNode.instructions.insert(node, this.getHook("modifyBlueColor"))
                        }
                    }
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