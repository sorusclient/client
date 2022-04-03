package com.github.sorusclient.client.feature.impl.togglesprintsneak.v1_18_2

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
        register("v1_18_2/net/minecraft/client/network/ClientPlayerEntity", this::transformClientPlayerEntity)
        register("v1_18_2/net/minecraft/client/input/KeyboardInput", this::transformKeyboardInput)
    }

    private fun transformClientPlayerEntity(classNode: ClassNode) {
        val tickMovement = "v1_18_2/net/minecraft/client/network/ClientPlayerEntity#tickMovement()V".toIdentifier()
        val isPressed = "v1_18_2/net/minecraft/client/option/KeyBinding#isPressed()Z".toIdentifier()
        classNode.findMethod(tickMovement)
            .apply { methodNode: MethodNode ->
                methodNode.findMethodCalls(isPressed)
                    .apply(InsertAfter(methodNode, this.getHook("modifyIsSprintPressed")))
            }
    }

    private fun transformKeyboardInput(classNode: ClassNode) {
        val tick = "v1_18_2/net/minecraft/client/input/KeyboardInput#tick(Z)V".toIdentifier()
        val isPressed = "v1_18_2/net/minecraft/client/option/KeyBinding#isPressed()Z".toIdentifier()
        classNode.findMethod(tick)
            .apply { methodNode: MethodNode ->
                methodNode.findMethodCalls(isPressed)
                    .nth(5)
                    .apply(InsertAfter(methodNode, this.getHook("modifyIsSneakPressed")))
            }
    }

}