package com.github.sorusclient.client.feature.impl.itemphysics.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

class ItemPhysicsTransformer : Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(ItemPhysicsTransformer::class.java)
    }

    init {
        setHookClass(ItemPhysicsHook::class.java)
        register("v1_18_1/net/minecraft/client/render/entity/ItemEntityRenderer") { classNode: ClassNode ->
            transformItemEntityRenderer(
                classNode
            )
        }
    }

    private fun transformItemEntityRenderer(classNode: ClassNode) {
        val render = Identifier.parse("v1_18_1/net/minecraft/client/render/entity/ItemEntityRenderer#render(Lv1_18_1/net/minecraft/entity/ItemEntity;FFLv1_18_1/net/minecraft/client/util/math/MatrixStack;Lv1_18_1/net/minecraft/client/render/VertexConsumerProvider;I)V")
        val push = Identifier.parse("v1_18_1/net/minecraft/client/util/math/MatrixStack#push()V")

        findMethod(classNode, render)
            .apply { methodNode: MethodNode ->
                println("Test")
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