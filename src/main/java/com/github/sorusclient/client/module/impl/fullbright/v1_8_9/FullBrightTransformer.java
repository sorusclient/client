package com.github.sorusclient.client.module.impl.fullbright.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class FullBrightTransformer implements Listener, ITransformer {

    private final Identifier GAME_RENDERER = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer");

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(FullBrightTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return GAME_RENDERER.getClassName().equals(name);
    }

    @Override
    public byte[] transform(String name, byte[] data) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        this.transformGameRenderer(classNode);

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private void transformGameRenderer(ClassNode classNode) {
        Identifier updateLightmap = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#updateLightmap(F)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(updateLightmap.getMethodName()) && methodNode.desc.equals(updateLightmap.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 17 && node.getPrevious() instanceof FieldInsnNode) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, FullBrightHook.class.getName().replace(".", "/"), "modifyGamma", "(F)F"));
                    }
                }
            }
        }
    }

}
