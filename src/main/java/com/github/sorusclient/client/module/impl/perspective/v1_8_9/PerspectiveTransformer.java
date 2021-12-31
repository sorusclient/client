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
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher").getClassName(), PerspectiveTransformer.this::transformEntityRenderDispatcher);
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer").getClassName(), PerspectiveTransformer.this::transformWorldRenderer);
            put(Identifier.parse("v1_8_9/net/minecraft/client/particle/ParticleManager").getClassName(), PerspectiveTransformer.this::transformParticleManager);
            put(Identifier.parse("v1_8_9/net/minecraft/client/class_321").getClassName(), PerspectiveTransformer.this::transformClass321);
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

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(transformCamera.getMethodName()) && methodNode.desc.equals(transformCamera.getMethodDesc())) {
                this.transformRotationCalls(methodNode);
            }
        }
    }

    private void transformEntityRenderDispatcher(ClassNode classNode) {
        Identifier transformCamera = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher#method_10200(Lv1_8_9/net/minecraft/world/World;Lv1_8_9/net/minecraft/client/font/TextRenderer;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/client/options/GameOptions;F)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(transformCamera.getMethodName()) && methodNode.desc.equals(transformCamera.getMethodDesc())) {
                this.transformRotationCalls(methodNode);
            }
        }
    }

    private void transformWorldRenderer(ClassNode classNode) {
        Identifier method_9906 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9906(Lv1_8_9/net/minecraft/entity/Entity;DLv1_8_9/net/minecraft/client/render/debug/CameraView;IZ)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(method_9906.getMethodName()) && methodNode.desc.equals(method_9906.getMethodDesc())) {
                this.transformRotationCalls(methodNode);
            }
        }
    }

    private void transformParticleManager(ClassNode classNode) {
        Identifier method_1299 = Identifier.parse("v1_8_9/net/minecraft/client/particle/ParticleManager#method_1299(Lv1_8_9/net/minecraft/entity/Entity;F)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(method_1299.getMethodName()) && methodNode.desc.equals(method_1299.getMethodDesc())) {
                this.transformRotationCalls(methodNode);
            }
        }
    }

    private void transformClass321(ClassNode classNode) {
        Identifier method_1299 = Identifier.parse("v1_8_9/net/minecraft/client/class_321#method_804(Lv1_8_9/net/minecraft/entity/player/PlayerEntity;Z)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(method_1299.getMethodName()) && methodNode.desc.equals(method_1299.getMethodDesc())) {
                this.transformRotationCalls(methodNode);
            }
        }
    }

    private void transformRotationCalls(MethodNode methodNode) {
        Identifier entity = Identifier.parse("v1_8_9/net/minecraft/entity/Entity");
        Identifier playerEntity = Identifier.parse("v1_8_9/net/minecraft/entity/player/PlayerEntity");

        Identifier pitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#pitch");
        Identifier prevPitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevPitch");
        Identifier yaw = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#yaw");
        Identifier prevYaw = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevYaw");

        for (AbstractInsnNode insnNode : methodNode.instructions) {
            if (insnNode instanceof FieldInsnNode && (((FieldInsnNode) insnNode).owner.equals(entity.getClassName()) || ((FieldInsnNode) insnNode).owner.equals(playerEntity.getClassName())) && ((FieldInsnNode) insnNode).name.equals(pitch.getFieldName())) {
                methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        PerspectiveHook.class.getName().replace(".", "/"), "modifyPitch", "(F)F"));
            }

            if (insnNode instanceof FieldInsnNode && (((FieldInsnNode) insnNode).owner.equals(entity.getClassName()) || ((FieldInsnNode) insnNode).owner.equals(playerEntity.getClassName())) && ((FieldInsnNode) insnNode).name.equals(yaw.getFieldName())) {
                methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        PerspectiveHook.class.getName().replace(".", "/"), "modifyYaw", "(F)F"));
            }

            if (insnNode instanceof FieldInsnNode && (((FieldInsnNode) insnNode).owner.equals(entity.getClassName()) || ((FieldInsnNode) insnNode).owner.equals(playerEntity.getClassName())) && ((FieldInsnNode) insnNode).name.equals(prevPitch.getFieldName())) {
                methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        PerspectiveHook.class.getName().replace(".", "/"), "modifyPrevPitch", "(F)F"));
            }

            if (insnNode instanceof FieldInsnNode && (((FieldInsnNode) insnNode).owner.equals(entity.getClassName()) || ((FieldInsnNode) insnNode).owner.equals(playerEntity.getClassName())) && ((FieldInsnNode) insnNode).name.equals(prevYaw.getFieldName())) {
                methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        PerspectiveHook.class.getName().replace(".", "/"), "modifyPrevYaw", "(F)F"));

            }
        }
    }

}
