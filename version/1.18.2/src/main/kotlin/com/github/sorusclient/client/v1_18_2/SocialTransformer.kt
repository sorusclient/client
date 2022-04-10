/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import com.github.sorusclient.client.transform.findMethod
import com.github.sorusclient.client.transform.findMethodMethodCalls
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.VarInsnNode

class SocialTransformer: Transformer() {

    init {
        setHookClass(SocialHook::class.java)
        register("v1_18_2/net/minecraft/client/render/entity/EntityRenderer", this::transformEntityRenderer)
    }

    private fun transformEntityRenderer(classNode: ClassNode) {
        val renderLabelIfPresent = "v1_18_2/net/minecraft/client/render/entity/EntityRenderer#renderLabelIfPresent(Lv1_18_2/net/minecraft/entity/Entity;Lv1_18_2/net/minecraft/text/Text;Lv1_18_2/net/minecraft/client/util/math/MatrixStack;Lv1_18_2/net/minecraft/client/render/VertexConsumerProvider;I)V".toIdentifier()
        val draw = "v1_18_2/net/minecraft/client/font/TextRenderer#draw(Lv1_18_2/net/minecraft/text/Text;FFIZLv1_18_2/net/minecraft/util/math/Matrix4f;Lv1_18_2/net/minecraft/client/render/VertexConsumerProvider;ZII)I".toIdentifier()

        classNode.findMethod(renderLabelIfPresent)
            .apply { methodNode ->
                findMethodMethodCalls(methodNode, draw)
                    .nth(0)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 2))
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 3))
                        insnList.add(getHook("onRenderNameLight"))
                    }))

                findMethodMethodCalls(methodNode, draw)
                    .nth(1)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 2))
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 3))
                        insnList.add(getHook("onRenderNameHeavy"))
                    }))
            }
    }

}