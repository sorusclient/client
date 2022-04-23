/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import v1_8_9.net.minecraft.client.MinecraftClient

@Suppress("UNUSED")
class EnhancementsTransformer : Transformer() {

    init {
        this.setHookClass(EnhancementsHook::class.java)

        register("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer", this::transformHeldItemRenderer)
        register("v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen", this::transformInventoryScreen)
        register("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher", this::transformEntityRenderDispatcher)
        register("v1_8_9/net/minecraft/entity/LivingEntity", this::transformLivingEntity)
        register("org/lwjgl/opengl/LinuxKeyboard", this::transformLinuxKeyboard)
        register("v1_8_9/net/minecraft/client/font/TextRenderer", this::transformTextRenderer)
        register("v1_8_9/net/minecraft/client/options/GameOptions", this::transformGameOptions)
        register("v1_8_9/net/minecraft/client/MinecraftClient", this::transformMinecraftClient)
        register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer)
        register("v1_8_9/net/minecraft/client/gui/screen/ingame/HandledScreen", this::transformHandledScreen)
    }

    private fun transformHeldItemRenderer(classNode: ClassNode) {
        val method1362 = "v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_1362(F)V".toIdentifier()
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
            "v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen#applyStatusEffectOffset()V".toIdentifier()
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
            "v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher#method_10200(Lv1_8_9/net/minecraft/world/World;Lv1_8_9/net/minecraft/client/font/TextRenderer;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/client/options/GameOptions;F)V".toIdentifier()
        val pitch = "v1_8_9/net/minecraft/entity/Entity#pitch".toIdentifier()
        val prevPitch = "v1_8_9/net/minecraft/entity/Entity#prevPitch".toIdentifier()
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
        val getRotationVector = "v1_8_9/net/minecraft/entity/LivingEntity#getRotationVector(F)Lv1_8_9/net/minecraft/util/math/Vec3d;".toIdentifier()
        val clientPlayerEntity = "v1_8_9/net/minecraft/entity/player/ClientPlayerEntity".toIdentifier()
        val entity = "v1_8_9/net/minecraft/entity/Entity".toIdentifier()

        classNode.findMethod(getRotationVector)
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

    private fun transformLinuxKeyboard(classNode: ClassNode) {
        val getKeyCode = "org/lwjgl/opengl/LinuxKeyboard#getKeycode(JI)I".toIdentifier()

        classNode.findMethod(getKeyCode)
            .apply { methodNode ->
                methodNode.instructions.clear()
            }

        classNode.findMethod(getKeyCode)
            .apply(Applier.Insert(createList { insnList ->
                val label0 = LabelNode()
                insnList.add(label0)
                insnList.add(VarInsnNode(Opcodes.ILOAD, 3))
                insnList.add(InsnNode(Opcodes.I2L))
                insnList.add(InsnNode(Opcodes.LCONST_1))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/opengl/LinuxKeyboard", "shift_lock_mask", "I"))
                insnList.add(InsnNode(Opcodes.I2L))
                insnList.add(InsnNode(Opcodes.LOR))
                insnList.add(InsnNode(Opcodes.LAND))
                insnList.add(InsnNode(Opcodes.LCONST_0))
                insnList.add(InsnNode(Opcodes.LCMP))
                val label1 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.IFEQ, label1))
                insnList.add(InsnNode(Opcodes.ICONST_1))
                val label2 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.GOTO, label2))
                insnList.add(label1)
                insnList.add(InsnNode(Opcodes.ICONST_0))
                insnList.add(label2)
                insnList.add(VarInsnNode(Opcodes.ISTORE, 4))
                val label3 = LabelNode()
                insnList.add(label3)
                insnList.add(VarInsnNode(Opcodes.ILOAD, 3))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/opengl/LinuxKeyboard", "modeswitch_mask", "I"))
                insnList.add(InsnNode(Opcodes.IAND))
                val label4 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.IFEQ, label4))
                insnList.add(InsnNode(Opcodes.ICONST_1))
                val label5 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.GOTO, label5))
                insnList.add(label4)
                insnList.add(InsnNode(Opcodes.ICONST_0))
                insnList.add(label5)
                insnList.add(VarInsnNode(Opcodes.ISTORE, 5))
                val label6 = LabelNode()
                insnList.add(label6)
                insnList.add(VarInsnNode(Opcodes.ILOAD, 3))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/opengl/LinuxKeyboard", "numlock_mask", "I"))
                insnList.add(InsnNode(Opcodes.IAND))
                val label7 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.IFEQ, label7))
                insnList.add(VarInsnNode(Opcodes.LLOAD, 1))
                insnList.add(VarInsnNode(Opcodes.ILOAD, 5))
                insnList.add(InsnNode(Opcodes.ICONST_1))
                insnList.add(MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/LinuxKeyboard", "getKeySym", "(JII)J", false))
                insnList.add(InsnNode(Opcodes.DUP2))
                insnList.add(VarInsnNode(Opcodes.LSTORE, 6))
                val label8 = LabelNode()
                insnList.add(label8)
                insnList.add(MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/LinuxKeyboard", "isKeypadKeysym", "(J)Z", false))
                insnList.add(JumpInsnNode(Opcodes.IFEQ, label7))
                val label9 = LabelNode()
                insnList.add(label9)
                insnList.add(VarInsnNode(Opcodes.ILOAD, 4))
                val label10 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.IFEQ, label10))
                val label11 = LabelNode()
                insnList.add(label11)
                insnList.add(VarInsnNode(Opcodes.LLOAD, 1))
                insnList.add(VarInsnNode(Opcodes.ILOAD, 5))
                insnList.add(InsnNode(Opcodes.ICONST_0))
                insnList.add(MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/LinuxKeyboard", "getKeySym", "(JII)J", false))
                insnList.add(VarInsnNode(Opcodes.LSTORE, 6))
                insnList.add(JumpInsnNode(Opcodes.GOTO, label10))
                insnList.add(label7)
                insnList.add(VarInsnNode(Opcodes.LLOAD, 1))
                insnList.add(VarInsnNode(Opcodes.ILOAD, 5))
                insnList.add(InsnNode(Opcodes.ICONST_0))
                insnList.add(MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/LinuxKeyboard", "getKeySym", "(JII)J", false))
                insnList.add(VarInsnNode(Opcodes.LSTORE, 6))
                val label12 = LabelNode()
                insnList.add(label12)
                insnList.add(VarInsnNode(Opcodes.ILOAD, 4))
                insnList.add(VarInsnNode(Opcodes.ILOAD, 3))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                insnList.add(FieldInsnNode(Opcodes.GETFIELD, "org/lwjgl/opengl/LinuxKeyboard", "caps_lock_mask", "I"))
                insnList.add(InsnNode(Opcodes.IAND))
                val label13 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.IFEQ, label13))
                insnList.add(InsnNode(Opcodes.ICONST_1))
                val label14 = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.GOTO, label14))
                insnList.add(label13)
                insnList.add(InsnNode(Opcodes.ICONST_0))
                insnList.add(label14)
                insnList.add(InsnNode(Opcodes.IXOR))
                insnList.add(JumpInsnNode(Opcodes.IFEQ, label10))
                val label15 = LabelNode()
                insnList.add(label15)
                insnList.add(VarInsnNode(Opcodes.LLOAD, 6))
                insnList.add(MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/LinuxKeyboard", "toUpper", "(J)J", false))
                insnList.add(VarInsnNode(Opcodes.LSTORE, 6))
                insnList.add(label10)
                insnList.add(VarInsnNode(Opcodes.LLOAD, 6))
                insnList.add(MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/LinuxKeycodes", "mapKeySymToLWJGLKeyCode", "(J)I", false))
                insnList.add(InsnNode(Opcodes.IRETURN))
            }))

        classNode.findMethod(getKeyCode)
            .apply { methodNode ->
                methodNode.maxLocals = 8
                methodNode.maxStack = 6
            }
    }

    private fun transformTextRenderer(classNode: ClassNode) {
        val method959 = "v1_8_9/net/minecraft/client/font/TextRenderer#method_959(Ljava/lang/String;Z)V".toIdentifier()
        classNode.findMethod(method959)
            .apply { methodNode ->
                methodNode.findVarReferences(6, VarReferenceType.LOAD)
                    .nth(9)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(LdcInsnNode(0.99f))
                        insnList.add(InsnNode(Opcodes.FMUL))
                    }))
            }

        classNode.findMethod(method959)
            .apply { methodNode ->
                methodNode.findVarReferences(6, VarReferenceType.LOAD)
                    .nth(12)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(LdcInsnNode(0.99f))
                        insnList.add(InsnNode(Opcodes.FMUL))
                    }))
            }
    }

    private fun transformGameOptions(classNode: ClassNode) {
        val save = "v1_8_9/net/minecraft/client/options/GameOptions#save()V".toIdentifier()
        val load = "v1_8_9/net/minecraft/client/options/GameOptions#load()V".toIdentifier()

        classNode.findMethod(save)
            .apply { methodNode ->
                methodNode.findReturns()
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onSave"))
                    }))
            }

        classNode.findMethod(load)
            .apply { methodNode ->
                methodNode.findReturns()
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                        insnList.add(this.getHook("onLoad"))
                    }))
            }
    }

    private fun transformMinecraftClient(classNode: ClassNode) {
        val stop = "v1_8_9/net/minecraft/client/MinecraftClient#stop()V".toIdentifier()

        classNode.findMethod(stop)
            .apply(Applier.Insert(createList { insnList ->
                insnList.add(this.getHook("onStop"))
            }))
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

        val setupCamera = "v1_8_9/net/minecraft/client/render/GameRenderer#setupCamera(FI)V".toIdentifier()
        val bobView = "v1_8_9/net/minecraft/client/options/GameOptions#bobView".toIdentifier()

        classNode.findMethod(setupCamera)
            .apply { methodNode ->
                methodNode.findFieldReferences(bobView, FieldReferenceType.GET)
                    .apply(Applier.InsertAfter(methodNode, getHook("modifyBobView")))
            }
    }

    private fun transformHandledScreen(classNode: ClassNode) {
        val removed = "v1_8_9/net/minecraft/client/gui/screen/ingame/HandledScreen#removed()V".toIdentifier()

        classNode.findMethod(removed)
            .apply(Applier.Insert(getHook("onCloseContainer")))
    }

}