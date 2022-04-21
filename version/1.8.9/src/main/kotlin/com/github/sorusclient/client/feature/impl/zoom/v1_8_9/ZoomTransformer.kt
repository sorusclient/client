/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.zoom.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Transformer
import com.github.sorusclient.client.transform.findMethod
import com.github.sorusclient.client.transform.findMethodCalls
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

@Suppress("UNUSED")
class ZoomTransformer : Transformer() {

    init {
        setHookClass(ZoomHook::class.java)
        register("v1_8_9/net/minecraft/entity/player/PlayerInventory", this::transformPlayerInventory)
    }

    private fun transformPlayerInventory(classNode: ClassNode) {
        val method3134 = "v1_8_9/net/minecraft/entity/player/PlayerInventory#method_3134(I)V".toIdentifier()

        classNode.findMethod(method3134)
            .apply(Applier.Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ILOAD, 1))
                insnList.add(getHook("onHotBarScroll"))
                insnList.add(VarInsnNode(Opcodes.ISTORE, 1))
            }))
    }

}