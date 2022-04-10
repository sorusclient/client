/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.v1_8_9

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
        register("v1_8_9/net/minecraft/client/render/entity/EntityRenderer", this::transformEntityRenderer)
    }

    private fun transformEntityRenderer(classNode: ClassNode) {
        val method6917 = "v1_8_9/net/minecraft/client/render/entity/EntityRenderer#method_6917(Lv1_8_9/net/minecraft/entity/Entity;Ljava/lang/String;DDDI)V".toIdentifier()
        val draw = "v1_8_9/net/minecraft/client/font/TextRenderer#draw(Ljava/lang/String;III)I".toIdentifier()

        classNode.findMethod(method6917)
            .apply { methodNode ->
                findMethodMethodCalls(methodNode, draw)
                    .nth(1)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 2))
                        insnList.add(getHook("onRenderName"))
                    }))
            }
    }

}