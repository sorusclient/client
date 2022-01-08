package com.github.sorusclient.client.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.module.impl.perspective.v1_8_9.PerspectiveHook;
import com.github.sorusclient.client.module.impl.perspective.v1_8_9.PerspectiveTransformer;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SorusTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(SorusTransformer.class);
    }

    public SorusTransformer() {
        this.setHookClass(SorusHook.class);
        this.register("v1_8_9/net/minecraft/client/ClientBrandRetriever", this::transformClientBrandRetriever);
    }

    private void transformClientBrandRetriever(ClassNode classNode) {
        Identifier getClientModName = Identifier.parse("v1_8_9/net/minecraft/client/ClientBrandRetriever#getClientModName()Ljava/lang/String;");

        this.findMethod(classNode, getClientModName)
                .apply(new Applier.Insert<>(this.createList(insnList -> {
                    insnList.add(this.getHook("getBrand"));
                    insnList.add(new InsnNode(Opcodes.ARETURN));
                })));
    }

}
