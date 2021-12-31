package com.github.sorusclient.client.module.impl.perspective.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PerspectiveTransformer implements Listener, ITransformer {

    private final Map<String, Consumer<ClassNode>> transformers = new HashMap<String, Consumer<ClassNode>>() {
        {
            put(Identifier.parse("v1_8_9/net/minecraft/entity/Entity").getClassName(), PerspectiveTransformer.this::transformEntity);
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer").getClassName(), PerspectiveTransformer.this::transformGameRenderer);
        }
    };

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(PerspectiveTransformer.class);
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

    private void transformEntity(ClassNode classNode) {
        Identifier increaseTransforms = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#increaseTransforms(FF)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(increaseTransforms.getMethodName()) && methodNode.desc.equals(increaseTransforms.getMethodDesc())) {
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.FLOAD, 1));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, PerspectiveHook.class.getName().replace(".", "/"), "modifyDelta", "(F)F"));
                insnList.add(new VarInsnNode(Opcodes.FSTORE, 1));
                insnList.add(new VarInsnNode(Opcodes.FLOAD, 2));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, PerspectiveHook.class.getName().replace(".", "/"), "modifyDelta", "(F)F"));
                insnList.add(new VarInsnNode(Opcodes.FSTORE, 2));

                methodNode.instructions.insert(insnList);
            }
        }
    }

    private void transformGameRenderer(ClassNode classNode) {
        Identifier transformCamera = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#transformCamera(F)V");

        Identifier pitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#pitch");
        Identifier prevPitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevPitch");
        Identifier yaw = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#yaw");
        Identifier prevYaw = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevYaw");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(transformCamera.getMethodName()) && methodNode.desc.equals(transformCamera.getMethodDesc())) {
                for (AbstractInsnNode insnNode : methodNode.instructions) {
                    if (insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).owner.equals(pitch.getClassName()) && ((FieldInsnNode) insnNode).name.equals(pitch.getFieldName())) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                                PerspectiveHook.class.getName().replace(".", "/"), "modifyPitch", "(F)F"));
                    }

                    if (insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).owner.equals(yaw.getClassName()) && ((FieldInsnNode) insnNode).name.equals(yaw.getFieldName())) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                                PerspectiveHook.class.getName().replace(".", "/"), "modifyYaw", "(F)F"));
                    }

                    if (insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).owner.equals(prevPitch.getClassName()) && ((FieldInsnNode) insnNode).name.equals(prevPitch.getFieldName())) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                                PerspectiveHook.class.getName().replace(".", "/"), "modifyPrevPitch", "(F)F"));
                    }

                    if (insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).owner.equals(prevYaw.getClassName()) && ((FieldInsnNode) insnNode).name.equals(prevYaw.getFieldName())) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                                PerspectiveHook.class.getName().replace(".", "/"), "modifyPrevYaw", "(F)F"));
                    }
                }
            }
        }
    }

}
