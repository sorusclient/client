package com.github.sorusclient.client.feature.impl.perspective.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class PerspectiveTransformer : Transformer(), Listener {
    override fun run() {
        GlassLoader.getInstance().registerTransformer(PerspectiveTransformer::class.java)
    }

    init {
        register("v1_8_9/net/minecraft/entity/Entity") { classNode: ClassNode -> transformEntity(classNode) }
        register("v1_8_9/net/minecraft/client/render/GameRenderer") { classNode: ClassNode ->
            transformGameRenderer(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher") { classNode: ClassNode ->
            transformEntityRenderDispatcher(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/render/WorldRenderer") { classNode: ClassNode ->
            transformWorldRenderer(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/particle/ParticleManager") { classNode: ClassNode ->
            transformParticleManager(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/class_321") { classNode: ClassNode -> transformClass321(classNode) }
    }

    private fun transformEntity(classNode: ClassNode) {
        val increaseTransforms = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#increaseTransforms(FF)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == increaseTransforms.methodName && methodNode.desc == increaseTransforms.methodDesc) {
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
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val transformCamera = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#transformCamera(F)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == transformCamera.methodName && methodNode.desc == transformCamera.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformEntityRenderDispatcher(classNode: ClassNode) {
        val transformCamera = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher#method_10200(Lv1_8_9/net/minecraft/world/World;Lv1_8_9/net/minecraft/client/font/TextRenderer;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/client/options/GameOptions;F)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == transformCamera.methodName && methodNode.desc == transformCamera.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val method9906 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9906(Lv1_8_9/net/minecraft/entity/Entity;DLv1_8_9/net/minecraft/client/render/debug/CameraView;IZ)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == method9906.methodName && methodNode.desc == method9906.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformParticleManager(classNode: ClassNode) {
        val method1299 = Identifier.parse("v1_8_9/net/minecraft/client/particle/ParticleManager#method_1299(Lv1_8_9/net/minecraft/entity/Entity;F)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == method1299.methodName && methodNode.desc == method1299.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformClass321(classNode: ClassNode) {
        val method1299 = Identifier.parse("v1_8_9/net/minecraft/client/class_321#method_804(Lv1_8_9/net/minecraft/entity/player/PlayerEntity;Z)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == method1299.methodName && methodNode.desc == method1299.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformRotationCalls(methodNode: MethodNode) {
        val entity = Identifier.parse("v1_8_9/net/minecraft/entity/Entity")
        val playerEntity = Identifier.parse("v1_8_9/net/minecraft/entity/player/PlayerEntity")
        val pitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#pitch")
        val prevPitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevPitch")
        val yaw = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#yaw")
        val prevYaw = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevYaw")
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