package com.github.sorusclient.client.module.impl.itemphysics.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ItemPhysicsTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(ItemPhysicsTransformer.class);
    }

    public ItemPhysicsTransformer() {
        this.setHookClass(ItemPhysicsHook.class);
        this.register("v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer", this::transformItemEntityRenderer);
    }

    private void transformItemEntityRenderer(ClassNode classNode) {
        Identifier method_10221 = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer#method_10221(Lv1_8_9/net/minecraft/entity/ItemEntity;DDDFLv1_8_9/net/minecraft/client/render/model/BakedModel;)I");
        Identifier color4f = Identifier.parse("v1_8_9/com/mojang/blaze3d/platform/GlStateManager#color4f(FFFF)V");

        this.findMethod(classNode, method_10221)
                .apply(methodNode -> {
                    this.findVarReferences(methodNode, 15, VarReferenceType.STORE)
                            .apply(new Applier.InsertBefore<>(methodNode, this.getHook("modifyItemBob")));

                    this.findVarReferences(methodNode, 17, VarReferenceType.STORE)
                            .apply(new Applier.InsertBefore<>(methodNode, this.getHook("modifyItemRotate")));

                    this.findMethodCalls(methodNode, color4f)
                            .apply(new Applier.InsertAfter<>(methodNode, this.createList(insnList -> {
                                insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                insnList.add(this.getHook("preRenderItem"));
                            })));
                });
    }

}
