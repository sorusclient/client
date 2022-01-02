package com.github.sorusclient.client.module.impl.enhancements.v1_8_9;

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

public class EnhancementsTransformer implements Listener, ITransformer {

    private final Map<String, Consumer<ClassNode>> transformers = new HashMap<String, Consumer<ClassNode>>() {
        {
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer").getClassName(), EnhancementsTransformer.this::transformHeldItemRenderer);
            put(Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen").getClassName(), EnhancementsTransformer.this::transformInventoryScreen);
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

}
