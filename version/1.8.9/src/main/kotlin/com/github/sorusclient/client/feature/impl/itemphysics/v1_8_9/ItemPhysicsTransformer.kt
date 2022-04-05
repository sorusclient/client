/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.itemphysics.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import com.github.sorusclient.client.transform.findMethod
import com.github.sorusclient.client.transform.findMethodCalls
import com.github.sorusclient.client.transform.findVarReferences
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

@Suppress("UNUSED")
class ItemPhysicsTransformer : Transformer() {

    init {
        setHookClass(ItemPhysicsHook::class.java)
        register("v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer", this::transformItemEntityRenderer)
    }

    private fun transformItemEntityRenderer(classNode: ClassNode) {
        val method10221 = "v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer#method_10221(Lv1_8_9/net/minecraft/entity/ItemEntity;DDDFLv1_8_9/net/minecraft/client/render/model/BakedModel;)I".toIdentifier()
        val color4f = "v1_8_9/com/mojang/blaze3d/platform/GlStateManager#color4f(FFFF)V".toIdentifier()
        classNode.findMethod(method10221)
            .apply { methodNode: MethodNode ->
                methodNode.findVarReferences(15, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifyItemBob")))
                methodNode.findVarReferences(17, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifyItemRotate")))
                methodNode.findMethodCalls(color4f)
                    .apply(InsertAfter(methodNode, createList { insnList: InsnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(this.getHook("preRenderItem"))
                    }))
            }
    }

}