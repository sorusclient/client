package com.github.sorusclient.client.module.impl.zoom.v1_8_9

import com.github.glassmc.loader.GlassLoader
import com.github.glassmc.loader.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class ZoomTransformer : Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(ZoomTransformer::class.java)
    }

    init {
        register("v1_8_9/net/minecraft/client/render/GameRenderer") { classNode: ClassNode ->
            transformGameRenderer(
                classNode
            )
        }
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val getFov = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#getFov(FZ)F")
        val render = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#render(FJ)V")
        val tick = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#tick()V")
        val smoothCameraEnabled =
            Identifier.parse("v1_8_9/net/minecraft/client/options/GameOptions#smoothCameraEnabled")
        for (methodNode in classNode.methods) {
            if (methodNode.name == getFov.methodName && methodNode.desc == getFov.methodDesc) {
                for (node in methodNode.instructions.toArray()) {
                    if (node.opcode == Opcodes.FRETURN && node.previous is VarInsnNode) {
                        val insnList = InsnList()
                        insnList.add(VarInsnNode(Opcodes.FLOAD, 4))
                        insnList.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                ZoomHook::class.java.name.replace(".", "/"),
                                "modifyFOV",
                                "(F)F"
                            )
                        )
                        insnList.add(VarInsnNode(Opcodes.FSTORE, 4))
                        methodNode.instructions.insertBefore(node.previous, insnList)
                    }
                }
            }
            if (methodNode.name == render.methodName && methodNode.desc == render.methodDesc) {
                for (node in methodNode.instructions.toArray()) {
                    if (node is VarInsnNode && node.getOpcode() == Opcodes.FSTORE && node.`var` == 5 && node.getPrevious().opcode == Opcodes.FADD) {
                        val insnList = InsnList()
                        insnList.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                ZoomHook::class.java.name.replace(".", "/"),
                                "modifySensitivity",
                                "(F)F"
                            )
                        )
                        methodNode.instructions.insertBefore(node, insnList)
                    }
                    if (node is FieldInsnNode && node.owner == smoothCameraEnabled.className && node.name == smoothCameraEnabled.fieldName) {
                        insertZoomCinematicCheck(methodNode, node)
                    }
                }
            }
            if (methodNode.name == tick.methodName && methodNode.desc == tick.methodDesc) {
                for (node in methodNode.instructions.toArray()) {
                    if (node is FieldInsnNode && node.owner == smoothCameraEnabled.className && node.name == smoothCameraEnabled.fieldName) {
                        insertZoomCinematicCheck(methodNode, node)
                    }
                }
            }
        }
    }

    private fun insertZoomCinematicCheck(methodNode: MethodNode, node: AbstractInsnNode) {
        val insnList = InsnList()
        insnList.add(
            MethodInsnNode(
                Opcodes.INVOKESTATIC,
                ZoomHook::class.java.name.replace(".", "/"),
                "useCinematicCamera",
                "()Z"
            )
        )
        insnList.add(JumpInsnNode(Opcodes.IFNE, node.next.next as LabelNode))
        methodNode.instructions.insertBefore(node.previous.previous.previous, insnList)
    }

}