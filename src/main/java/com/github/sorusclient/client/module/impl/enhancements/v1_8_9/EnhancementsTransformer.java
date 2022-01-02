package com.github.sorusclient.client.module.impl.enhancements.v1_8_9;

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

public class EnhancementsTransformer implements Listener, ITransformer {

    private final Map<String, Consumer<ClassNode>> transformers = new HashMap<String, Consumer<ClassNode>>() {
        {
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer").getClassName(), EnhancementsTransformer.this::transformHeldItemRenderer);
            put(Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen").getClassName(), EnhancementsTransformer.this::transformInventoryScreen);
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher").getClassName(), EnhancementsTransformer.this::transformEntityRenderDispatcher);
        }
    };

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(EnhancementsTransformer.class);
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

    private void transformHeldItemRenderer(ClassNode classNode) {
        Identifier method_1362 = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_1362(F)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(method_1362.getMethodName()) && methodNode.desc.equals(method_1362.getMethodDesc())) {
                methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, EnhancementsHook.class.getName().replace(".", "/"), "preRenderFireFirstPerson", "()V"));

                for (AbstractInsnNode node : methodNode.instructions) {
                    if (node.getOpcode() == Opcodes.RETURN) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, EnhancementsHook.class.getName().replace(".", "/"), "postRenderFireFirstPerson", "()V"));
                    }
                }
            }
        }
    }

    private void transformInventoryScreen(ClassNode classNode) {
        Identifier applyStatusEffectOffset = Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen#applyStatusEffectOffset()V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(applyStatusEffectOffset.getMethodName()) && methodNode.desc.equals(applyStatusEffectOffset.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions) {
                    if (node instanceof IntInsnNode) {
                        methodNode.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, EnhancementsHook.class.getName().replace(".", "/"), "modifyPotionOffset", "(I)I"));
                    }
                }
            }
        }
    }

    private void transformEntityRenderDispatcher(ClassNode classNode) {
        Identifier transformCamera = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher#method_10200(Lv1_8_9/net/minecraft/world/World;Lv1_8_9/net/minecraft/client/font/TextRenderer;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/entity/Entity;Lv1_8_9/net/minecraft/client/options/GameOptions;F)V");

        Identifier pitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#pitch");
        Identifier prevPitch = Identifier.parse("v1_8_9/net/minecraft/entity/Entity#prevPitch");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(transformCamera.getMethodName()) && methodNode.desc.equals(transformCamera.getMethodDesc())) {
                for (AbstractInsnNode insnNode : methodNode.instructions) {
                    if (insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).owner.equals(pitch.getClassName()) && ((FieldInsnNode) insnNode).name.equals(pitch.getFieldName())) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                                EnhancementsHook.class.getName().replace(".", "/"), "modifyPitch", "(F)F"));
                    }

                    if (insnNode instanceof FieldInsnNode && ((FieldInsnNode) insnNode).owner.equals(prevPitch.getClassName()) && ((FieldInsnNode) insnNode).name.equals(prevPitch.getFieldName())) {
                        methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                                EnhancementsHook.class.getName().replace(".", "/"), "modifyPrevPitch", "(F)F"));
                    }
                }
            }
        }
    }

}
