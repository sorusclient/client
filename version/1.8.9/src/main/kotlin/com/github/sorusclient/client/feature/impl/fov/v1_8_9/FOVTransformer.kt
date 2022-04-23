/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.fov.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class FOVTransformer : Transformer() {

    init {
        this.setHookClass(FOVHook::class.java)
        register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer)
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val updateMovementFovMultiplier = "v1_8_9/net/minecraft/client/render/GameRenderer#updateMovementFovMultiplier()V".toIdentifier()

        classNode.findMethod(updateMovementFovMultiplier)
            .apply { methodNode ->
                methodNode.findVarReferences(1, VarReferenceType.STORE)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.FLOAD, 1))
                        insnList.add(getHook("modifySpeedFov"))
                        insnList.add(VarInsnNode(Opcodes.FSTORE, 1))
                    }))
            }
    }

}