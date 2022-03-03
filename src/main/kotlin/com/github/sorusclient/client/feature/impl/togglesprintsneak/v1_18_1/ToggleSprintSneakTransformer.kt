package com.github.sorusclient.client.feature.impl.togglesprintsneak.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class ToggleSprintSneakTransformer : Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(ToggleSprintSneakTransformer::class.java)
    }

    init {
        setHookClass(ToggleSprintSneakHook::class.java)
        register("v1_18_1/net/minecraft/client/network/ClientPlayerEntity") { classNode: ClassNode ->
            transformClientPlayerEntity(
                classNode
            )
        }
        register("v1_18_1/net/minecraft/client/input/KeyboardInput") { classNode: ClassNode ->
            transformKeyboardInput(
                classNode
            )
        }
    }

    private fun transformClientPlayerEntity(classNode: ClassNode) {
        val tickMovement = Identifier.parse("v1_18_1/net/minecraft/client/network/ClientPlayerEntity#tickMovement()V")
        val isPressed = Identifier.parse("v1_18_1/net/minecraft/client/option/KeyBinding#isPressed()Z")
        findMethod(classNode, tickMovement)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, isPressed)
                    .apply(InsertAfter(methodNode, this.getHook("modifyIsSprintPressed")))
            }
    }

    private fun transformKeyboardInput(classNode: ClassNode) {
        val tick = Identifier.parse("v1_18_1/net/minecraft/client/input/KeyboardInput#tick(Z)V")
        val isPressed = Identifier.parse("v1_18_1/net/minecraft/client/option/KeyBinding#isPressed()Z")
        findMethod(classNode, tick)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, isPressed)
                    .nth(5)
                    .apply(InsertAfter(methodNode, this.getHook("modifyIsSneakPressed")))
            }
    }

}