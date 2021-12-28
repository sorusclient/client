package com.github.sorusclient.client.module.impl.blockoverlay.v1_8_9;

import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.transform.TransformerManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BlockOverlayTransformer implements Listener, ITransformer {

    private static final String WORLD_RENDERER = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer").getClassName();

    @Override
    public void run() {
        Sorus.getInstance().get(TransformerManager.class).register(BlockOverlayTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return WORLD_RENDERER.equals(name);
    }

    @Override
    public byte[] transform(String name, byte[] data) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        this.transformWorldRenderer(classNode);

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private void transformWorldRenderer(ClassNode classNode) {
        Identifier method_9895 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9895(Lv1_8_9/net/minecraft/util/math/Box;)V");
        Identifier box = Identifier.parse("v1_8_9/net/minecraft/util/math/Box");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(method_9895.getMethodName()) && methodNode.desc.equals(method_9895.getMethodDesc())) {
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, BlockOverlayHook.class.getName().replace(".", "/"), "preRenderOutline", "(L" + box.getClassName() + ";)V"));
                methodNode.instructions.insert(insnList);
            }
        }
    }

}
