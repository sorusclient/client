/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.perspective.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Transformer
import com.github.sorusclient.client.transform.findMethod
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class PerspectiveTransformer : Transformer() {

    init {
        register("v1_18_2/net/minecraft/entity/Entity", this::transformEntity)
        register("v1_18_2/net/minecraft/client/render/Camera", this::transformGameCamera)
    }

    private fun transformEntity(classNode: ClassNode) {
        val changeLookDirection = "v1_18_2/net/minecraft/entity/Entity#changeLookDirection(DD)V".toIdentifier()

        classNode.findMethod(changeLookDirection)
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
        val update = "v1_18_2/net/minecraft/client/render/Camera#update(Lv1_18_2/net/minecraft/world/BlockView;Lv1_18_2/net/minecraft/entity/Entity;ZZF)V".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == update.methodName && methodNode.desc == update.methodDesc) {
                transformRotationCalls(methodNode)
            }
        }
    }

    private fun transformRotationCalls(methodNode: MethodNode) {
        val entity = "v1_18_2/net/minecraft/entity/Entity".toIdentifier()
        val playerEntity = "v1_18_2/net/minecraft/entity/player/PlayerEntity".toIdentifier()
        val getPitch = "v1_18_2/net/minecraft/entity/Entity#getPitch(F)F".toIdentifier()
        val prevPitch = "v1_18_2/net/minecraft/entity/Entity#prevPitch".toIdentifier()
        val getYaw = "v1_18_2/net/minecraft/entity/Entity#getYaw(F)F".toIdentifier()
        val prevYaw = "v1_18_2/net/minecraft/entity/Entity#prevYaw".toIdentifier()
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