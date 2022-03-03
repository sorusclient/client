package com.github.sorusclient.client.feature.impl.perspective.v1_18_1

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
        register("v1_18_1/net/minecraft/entity/Entity") { classNode: ClassNode -> transformEntity(classNode) }
        register("v1_18_1/net/minecraft/client/render/Camera") { classNode: ClassNode ->
            transformGameCamera(
                classNode
            )
        }
        /*register("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher") { classNode: ClassNode ->
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
        register("v1_8_9/net/minecraft/client/class_321") { classNode: ClassNode -> transformClass321(classNode) }*/
    }

    private fun transformEntity(classNode: ClassNode) {
        val changeLookDirection = Identifier.parse("v1_18_1/net/minecraft/entity/Entity#changeLookDirection(DD)V")

        findMethod(classNode, changeLookDirection)
            .apply { methodNode ->
                val insnList = InsnList()
                insnList.add(VarInsnNode(Opcodes.DLOAD, 1))
                insnList.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"),
                        "modifyDelta",
                        "(D)D"
                    )
                )
                insnList.add(VarInsnNode(Opcodes.DSTORE, 1))
                insnList.add(VarInsnNode(Opcodes.DLOAD, 3))
                insnList.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"),
                        "modifyDelta",
                        "(D)D"
                    )
                )
                insnList.add(VarInsnNode(Opcodes.DSTORE, 3))
                methodNode.instructions.insert(insnList)
            }
    }

    private fun transformGameCamera(classNode: ClassNode) {
        val update = Identifier.parse("v1_18_1/net/minecraft/client/render/Camera#update(Lv1_18_1/net/minecraft/world/BlockView;Lv1_18_1/net/minecraft/entity/Entity;ZZF)V")
        for (methodNode in classNode.methods) {
            if (methodNode.name == update.methodName && methodNode.desc == update.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformRotationCalls(methodNode: MethodNode) {
        val entity = Identifier.parse("v1_18_1/net/minecraft/entity/Entity")
        val playerEntity = Identifier.parse("v1_18_1/net/minecraft/entity/player/PlayerEntity")
        val getPitch = Identifier.parse("v1_18_1/net/minecraft/entity/Entity#getPitch(F)F")
        val prevPitch = Identifier.parse("v1_18_1/net/minecraft/entity/Entity#prevPitch")
        val getYaw = Identifier.parse("v1_18_1/net/minecraft/entity/Entity#getYaw(F)F")
        val prevYaw = Identifier.parse("v1_18_1/net/minecraft/entity/Entity#prevYaw")
        for (insnNode in methodNode.instructions) {
            if (insnNode is MethodInsnNode && (insnNode.owner == entity.className || insnNode.owner == playerEntity.className) && insnNode.name == getPitch.methodName && insnNode.desc == getPitch.methodDesc) {
                methodNode.instructions.insert(
                    insnNode, MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        PerspectiveHook::class.java.name.replace(".", "/"), "modifyPitch", "(F)F"
                    )
                )
            }
            if (insnNode is MethodInsnNode && (insnNode.owner == entity.className || insnNode.owner == playerEntity.className) && insnNode.name == getYaw.methodName && insnNode.desc == getYaw.methodDesc) {
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