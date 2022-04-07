/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.togglesprintsneak.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Transformer
import com.github.sorusclient.client.transform.findMethod
import com.github.sorusclient.client.transform.findMethodCalls
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

@Suppress("UNUSED")
class ToggleSprintSneakTransformer : Transformer() {

    init {
        setHookClass(ToggleSprintSneakHook::class.java)
        register("v1_8_9/net/minecraft/entity/player/ClientPlayerEntity", this::transformClientPlayerEntity)
        register("v1_8_9/net/minecraft/client/input/KeyboardInput", this::transformKeyboardInput)
    }

    private fun transformClientPlayerEntity(classNode: ClassNode) {
        val tickMovement = "v1_8_9/net/minecraft/entity/player/ClientPlayerEntity#tickMovement()V".toIdentifier()
        val isPressed = "v1_8_9/net/minecraft/client/options/KeyBinding#isPressed()Z".toIdentifier()
        classNode.findMethod(tickMovement)
            .apply { methodNode: MethodNode ->
                methodNode.findMethodCalls(isPressed)
                    .apply(InsertAfter(methodNode, this.getHook("modifyIsSprintPressed")))
            }
    }

    private fun transformKeyboardInput(classNode: ClassNode) {
        val method1302 = "v1_8_9/net/minecraft/client/input/KeyboardInput#method_1302()V".toIdentifier()
        val isPressed = "v1_8_9/net/minecraft/client/options/KeyBinding#isPressed()Z".toIdentifier()
        classNode.findMethod(method1302)
            .apply { methodNode: MethodNode ->
                methodNode.findMethodCalls(isPressed)
                    .nth(5)
                    .apply(InsertAfter(methodNode, this.getHook("modifyIsSneakPressed")))
            }
    }

}