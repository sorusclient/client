package com.github.sorusclient.client.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.module.impl.perspective.v1_8_9.PerspectiveHook;
import com.github.sorusclient.client.module.impl.perspective.v1_8_9.PerspectiveTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SorusTransformer implements Listener, ITransformer {

    private final Map<String, Consumer<ClassNode>> transformers = new HashMap<String, Consumer<ClassNode>>() {
        {
            put(Identifier.parse("v1_8_9/net/minecraft/client/ClientBrandRetriever").getClassName(), SorusTransformer.this::transformClientBrandRetriever);
        }
    };

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(SorusTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return this.transformers.keySet().stream().anyMatch(identifier -> identifier.equals(name));
    }

    @Override
    public byte[] transform(String name, byte[] data) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        this.transformers.get(name).accept(classNode);

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private void transformClientBrandRetriever(ClassNode classNode) {
        Identifier increaseTransforms = Identifier.parse("v1_8_9/net/minecraft/client/ClientBrandRetriever#getClientModName()Ljava/lang/String;");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(increaseTransforms.getMethodName()) && methodNode.desc.equals(increaseTransforms.getMethodDesc())) {
                InsnList insnList = new InsnList();
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, SorusHook.class.getName().replace(".", "/"), "getBrand", "()Ljava/lang/String;"));
                insnList.add(new InsnNode(Opcodes.ARETURN));

                methodNode.instructions.insert(insnList);

            }
        }
    }

}
