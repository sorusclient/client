package com.github.sorusclient.client.feature.impl.perspective.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class PerspectiveTransformer : Transformer() {

    init {
        register("v1_8_9/net/minecraft/entity/Entity", this::transformEntity)
        register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer)
        register("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher", this::transformEntityRenderDispatcher)
        register("v1_8_9/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer)
        register("v1_8_9/net/minecraft/client/particle/ParticleManager", this::transformParticleManager)
        register("v1_8_9/net/minecraft/client/class_321", this::transformClass321)
    }

    private fun transformEntity(classNode: ClassNode) {
        val increaseTransforms = "v1_8_9/net/minecraft/entity/Entity#increaseTransforms(FF)V".toIdentifier()

        findMethod(classNode, increaseTransforms)
            .apply { methodNode ->
                val insnList = InsnList()
                insnList.add(VarInsnNode(Opcodes.FLOAD, 1))
                insnList.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"),
                        "modifyDelta",
                        "(F)F"
                    )
                )
                insnList.add(VarInsnNode(Opcodes.FSTORE, 1))
                insnList.add(VarInsnNode(Opcodes.FLOAD, 2))
                insnList.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"),
                        "modifyDelta",
                        "(F)F"
                    )
                )
                insnList.add(VarInsnNode(Opcodes.FSTORE, 2))
                methodNode.instructions.insert(insnList)
            }
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val transformCamera = "v1_8_9/net/minecraft/client/render/GameRenderer#transformCamera(F)V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == transformCamera.methodName && methodNode.desc == transformCamera.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformEntityRenderDispatcher(classNode: ClassNode) {
        val transformCamera = "v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher#method_10200(Lv1_8_9/net/minecraft/world/World;Lv1_8_9/net/minecraft/client/font/TextRenderer;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/client/options/GameOptions;F)V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == transformCamera.methodName && methodNode.desc == transformCamera.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val method9906 = "v1_8_9/net/minecraft/client/render/WorldRenderer#method_9906(Lv1_8_9/net/minecraft/entity/Entity;DLv1_8_9/net/minecraft/client/render/debug/CameraView;IZ)V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == method9906.methodName && methodNode.desc == method9906.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformParticleManager(classNode: ClassNode) {
        val method1299 = "v1_8_9/net/minecraft/client/particle/ParticleManager#method_1299(Lv1_8_9/net/minecraft/entity/Entity;F)V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == method1299.methodName && methodNode.desc == method1299.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformClass321(classNode: ClassNode) {
        val method1299 = "v1_8_9/net/minecraft/client/class_321#method_804(Lv1_8_9/net/minecraft/entity/player/PlayerEntity;Z)V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == method1299.methodName && methodNode.desc == method1299.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformRotationCalls(methodNode: MethodNode) {
        val entity = "v1_8_9/net/minecraft/entity/Entity".toIdentifier()
        val playerEntity = "v1_8_9/net/minecraft/entity/player/PlayerEntity".toIdentifier()
        val pitch = "v1_8_9/net/minecraft/entity/Entity#pitch".toIdentifier()
        val prevPitch = "v1_8_9/net/minecraft/entity/Entity#prevPitch".toIdentifier()
        val yaw = "v1_8_9/net/minecraft/entity/Entity#yaw".toIdentifier()
        val prevYaw = "v1_8_9/net/minecraft/entity/Entity#prevYaw".toIdentifier()
        for (insnNode in methodNode.instructions) {
            if (insnNode is FieldInsnNode && (insnNode.owner == entity.className || insnNode.owner == playerEntity.className) && insnNode.name == pitch.fieldName) {
                methodNode.instructions.insert(
                    insnNode, MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"), "modifyPitch", "(F)F"
                    )
                )
            }
            if (insnNode is FieldInsnNode && (insnNode.owner == entity.className || insnNode.owner == playerEntity.className) && insnNode.name == yaw.fieldName) {
                methodNode.instructions.insert(
                    insnNode, MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"), "modifyYaw", "(F)F"
                    )
                )
            }
            if (insnNode is FieldInsnNode && (insnNode.owner == entity.className || insnNode.owner == playerEntity.className) && insnNode.name == prevPitch.fieldName) {
                methodNode.instructions.insert(
                    insnNode, MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"), "modifyPrevPitch", "(F)F"
                    )
                )
            }
            if (insnNode is FieldInsnNode && (insnNode.owner == entity.className || insnNode.owner == playerEntity.className) && insnNode.name == prevYaw.fieldName) {
                methodNode.instructions.insert(
                    insnNode, MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"), "modifyPrevYaw", "(F)F"
                    )
                )
            }
        }
    }
}