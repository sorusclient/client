package com.github.sorusclient.client.feature.impl.itemphysics.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

@Suppress("UNUSED")
class ItemPhysicsTransformer : Transformer() {

    init {
        setHookClass(ItemPhysicsHook::class.java)
        register("v1_18_2/net/minecraft/client/render/entity/ItemEntityRenderer", this::transformItemEntityRenderer)
    }

    private fun transformItemEntityRenderer(classNode: ClassNode) {
        val render = "v1_18_2/net/minecraft/client/render/entity/ItemEntityRenderer#render(Lv1_18_2/net/minecraft/entity/ItemEntity;FFLv1_18_2/net/minecraft/client/util/math/MatrixStack;Lv1_18_2/net/minecraft/client/render/VertexConsumerProvider;I)V".toIdentifier()
        val push = "v1_18_2/net/minecraft/client/util/math/MatrixStack#push()V".toIdentifier()

        findMethod(classNode, render)
            .apply { methodNode: MethodNode ->
                findVarReferences(methodNode, 13, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifyItemBob")))
                findVarReferences(methodNode, 15, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifyItemRotate")))
                findMethodCalls(methodNode, push)
                    .nth(0)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                        insnList.add(VarInsnNode(Opcodes.ALOAD, 4))
                        insnList.add(this.getHook("preRenderItem"))
                    }))
            }
    }

}