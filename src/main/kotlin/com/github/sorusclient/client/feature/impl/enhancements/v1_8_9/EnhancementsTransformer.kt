package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import org.objectweb.asm.tree.VarInsnNode

class EnhancementsTransformer : Transformer(), Listener {
    override fun run() {
        GlassLoader.getInstance().registerTransformer(EnhancementsTransformer::class.java)
    }

    init {
        register("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer") { classNode: ClassNode ->
            transformHeldItemRenderer(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen") { classNode: ClassNode ->
            transformInventoryScreen(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher") { classNode: ClassNode ->
            transformEntityRenderDispatcher(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/entity/LivingEntity") { classNode: ClassNode ->
            transformLivingEntity(
                classNode
            )
        }
    }

    private fun transformHeldItemRenderer(classNode: ClassNode) {
        val method1362 = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_1362(F)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == method1362.methodName && methodNode.desc == method1362.methodDesc) {
                methodNode.instructions.insert(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        EnhancementsHook::class.java.name.replace(".", "/"),
                        "preRenderFireFirstPerson",
                        "()V"
                    )
                )
                for (node in methodNode.instructions) {
                    if (node.opcode == Opcodes.RETURN) {
                        methodNode.instructions.insertBefore(
                            node,
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                EnhancementsHook::class.java.name.replace(".", "/"),
                                "postRenderFireFirstPerson",
                                "()V"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun transformInventoryScreen(classNode: ClassNode) {
        val applyStatusEffectOffset =
            Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen#applyStatusEffectOffset()V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == applyStatusEffectOffset.methodName && methodNode.desc == applyStatusEffectOffset.methodDesc) {
                for (node in methodNode.instructions) {
                    if (node is IntInsnNode) {
                        methodNode.instructions.insert(
                            node,
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                EnhancementsHook::class.java.name.replace(".", "/"),
                                "modifyPotionOffset",
                                "(I)I"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun transformEntityRenderDispatcher(classNode: ClassNode) {
        val transformCamera =
            Identifier.parse("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher#method_10200(Lv1_8_9/net/minecraft/world/World;Lv1_8_9/net/minecraft/client/font/TextRenderer;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/client/options/GameOptions;F)V")
        val pitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#pitch")
        val prevPitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevPitch")
        for (methodNode in classNode.methods) {
            if (methodNode.name == transformCamera.methodName && methodNode.desc == transformCamera.methodDesc) {
                for (insnNode in methodNode.instructions) {
                    if (insnNode is FieldInsnNode && insnNode.owner == pitch.className && insnNode.name == pitch.fieldName) {
                        methodNode.instructions.insert(
                            insnNode, MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                EnhancementsHook::class.java.name.replace(".", "/"), "modifyPitch", "(F)F"
                            )
                        )
                    }
                    if (insnNode is FieldInsnNode && insnNode.owner == prevPitch.className && insnNode.name == prevPitch.fieldName) {
                        methodNode.instructions.insert(
                            insnNode, MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                EnhancementsHook::class.java.name.replace(".", "/"), "modifyPrevPitch", "(F)F"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun transformLivingEntity(classNode: ClassNode) {
        val getRotationVector = Identifier.parse("v1_8_9/net/minecraft/entity/LivingEntity#getRotationVector(F)Lv1_8_9/net/minecraft/util/math/Vec3d;")
        val clientPlayerEntity = Identifier.parse("v1_8_9/net/minecraft/client/network/ClientPlayerEntity")
        val entity = Identifier.parse("v1_8_9/net/minecraft/entity/Entity")

        findMethod(classNode, getRotationVector)
            .apply(Applier.Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(TypeInsnNode(Opcodes.INSTANCEOF, clientPlayerEntity.className))
                val labelNode = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(VarInsnNode(Opcodes.FLOAD, 1))
                insnList.add(MethodInsnNode(Opcodes.INVOKESPECIAL, entity.className, getRotationVector.methodName, getRotationVector.methodDesc))
                insnList.add(InsnNode(Opcodes.ARETURN))
                insnList.add(labelNode)
            }))
    }

}