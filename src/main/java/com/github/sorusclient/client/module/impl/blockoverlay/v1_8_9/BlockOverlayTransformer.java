package com.github.sorusclient.client.module.impl.blockoverlay.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BlockOverlayTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(BlockOverlayTransformer.class);
    }

    public BlockOverlayTransformer() {
        this.setHookClass(BlockOverlayHook.class);
        this.register("v1_8_9/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer);
    }

    private void transformWorldRenderer(ClassNode classNode) {
        Identifier method_9895 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9895(Lv1_8_9/net/minecraft/util/math/Box;)V");

        this.findMethod(classNode, method_9895)
                .apply(new Applier.Insert<>(this.createList(insnList -> {
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(this.getHook("preRenderOutline"));
                })));
    }

}
