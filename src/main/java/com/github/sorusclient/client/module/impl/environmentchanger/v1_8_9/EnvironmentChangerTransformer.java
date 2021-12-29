package com.github.sorusclient.client.module.impl.environmentchanger.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class EnvironmentChangerTransformer implements Listener, ITransformer {

    private final Identifier WORLD = Identifier.parse("v1_8_9/net/minecraft/world/World");
    private final Identifier WORLD_RENDERER = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer");
    private final Identifier GAME_RENDERER = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer");

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(EnvironmentChangerTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return WORLD.getClassName().equals(name) || WORLD_RENDERER.getClassName().equals(name) || GAME_RENDERER.getClassName().equals(name);
    }

    @Override
    public byte[] transform(String name, byte[] data) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        this.transformWorldWorldRendererGameRenderer(classNode);

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private void transformWorldWorldRendererGameRenderer(ClassNode classNode) {
        Identifier getSkyAngle = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngle(F)F");
        Identifier getFogColor = Identifier.parse("v1_8_9/net/minecraft/world/World#getFogColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;");
        Identifier getSkyAngleRadians = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngleRadians(F)F");
        Identifier getCloudColor = Identifier.parse("v1_8_9/net/minecraft/world/World#getCloudColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;");
        Identifier method_9891 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9891(FI)V");
        Identifier getSkyAngle2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F");
        Identifier updateFog = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#updateFog(F)V");
        Identifier method_3707 = Identifier.parse("v1_8_9/net/minecraft/world/World#method_3707(F)F");
        Identifier method_3631 = Identifier.parse("v1_8_9/net/minecraft/world/World#method_3631(Lv1_8_9/net/minecraft/entity/Entity;F)Lv1_8_9/net/minecraft/util/math/Vec3d;");

        Identifier getRainGradient = Identifier.parse("v1_8_9/net/minecraft/world/World#getRainGradient(F)F");
        Identifier getRainGradient2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F");

        String environmentChangerHook = "com/github/sorusclient/client/module/impl/environmentchanger/v1_8_9/EnvironmentChangerHook";
        
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(getFogColor.getMethodName()) && methodNode.desc.equals(getFogColor.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 2) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifySkyAngle", "(F)F"));
                    }
                }
            }

            if (methodNode.name.equals(getSkyAngleRadians.getMethodName()) && methodNode.desc.equals(getSkyAngleRadians.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 2) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifySkyAngle", "(F)F"));
                    }
                }
            }

            if (methodNode.name.equals(getCloudColor.getMethodName()) && methodNode.desc.equals(getCloudColor.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 2) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifySkyAngle", "(F)F"));
                    }
                }
            }

            if (methodNode.name.equals(method_9891.getMethodName()) && methodNode.desc.equals(method_9891.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals(getSkyAngle2.getClassName()) && ((MethodInsnNode) node).name.equals(getSkyAngle2.getMethodName())) {
                        methodNode.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifySkyAngle", "(F)F"));
                    }
                }
            }

            if (methodNode.name.equals(updateFog.getMethodName()) && methodNode.desc.equals(updateFog.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals(getSkyAngle.getClassName()) && ((MethodInsnNode) node).name.equals(getSkyAngle.getMethodName())) {
                        methodNode.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifySkyAngle", "(F)F"));
                    }
                }
            }

            if (methodNode.name.equals(method_3707.getMethodName()) && methodNode.desc.equals(method_3707.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals(getSkyAngle.getClassName()) && ((MethodInsnNode) node).name.equals(getSkyAngle.getMethodName())) {
                        methodNode.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifySkyAngle", "(F)F"));
                    }
                }
            }

            if (methodNode.name.equals(method_3631.getMethodName()) && methodNode.desc.equals(method_3631.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals(getSkyAngle.getClassName()) && ((MethodInsnNode) node).name.equals(getSkyAngle.getMethodName())) {
                        methodNode.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifySkyAngle", "(F)F"));
                    }
                }
            }

            if (!classNode.name.equals(WORLD.getClassName()) || (
                    (methodNode.name.equals(getCloudColor.getMethodName()) && methodNode.desc.equals(getFogColor.getMethodDesc())) ||
                    (methodNode.name.equals(method_3631.getMethodName()) && methodNode.desc.equals(method_3631.getMethodDesc()))
            )) {
                for (AbstractInsnNode node : methodNode.instructions) {
                    if (node instanceof MethodInsnNode && (((MethodInsnNode) node).owner.equals(getRainGradient2.getClassName()) && ((MethodInsnNode) node).name.equals(getRainGradient2.getMethodName()) ||
                            ((MethodInsnNode) node).owner.equals(getRainGradient.getClassName()) && ((MethodInsnNode) node).name.equals(getRainGradient.getMethodName()))) {
                        methodNode.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, environmentChangerHook, "modifyRainGradient", "(F)F"));
                    }
                }
            }
        }
    }

}
