package com.github.sorusclient.client.feature.impl.itemphysics.v1_8_9

import com.github.sorusclient.client.Identifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

class ItemPhysicsTransformer : Transformer() {

    init {
        setHookClass(ItemPhysicsHook::class.java)
        register("v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer") { classNode: ClassNode ->
            transformItemEntityRenderer(
                classNode
            )
        }
    }

    private fun transformItemEntityRenderer(classNode: ClassNode) {
        val method10221 = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer#method_10221(Lv1_8_9/net/minecraft/entity/ItemEntity;DDDFLv1_8_9/net/minecraft/client/render/model/BakedModel;)I")
        val color4f = Identifier.parse("v1_8_9/com/mojang/blaze3d/platform/GlStateManager#color4f(FFFF)V")
        findMethod(classNode, method10221)
            .apply { methodNode: MethodNode ->
                findVarReferences(methodNode, 15, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifyItemBob")))
                findVarReferences(methodNode, 17, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifyItemRotate")))
                findMethodCalls(methodNode, color4f)
                    .apply(InsertAfter(methodNode, createList { insnList: InsnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(this.getHook("preRenderItem"))
                    }))
            }
    }

}