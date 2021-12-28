package com.github.sorusclient.client.module.impl.oldanimations.v1_8_9;

import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.transform.TransformerManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OldAnimationsTransformer implements Listener, ITransformer {

    private final Map<String, Consumer<ClassNode>> transformers = new HashMap<String, Consumer<ClassNode>>() {
        {
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer").getClassName(), OldAnimationsTransformer.this::transformHeldItemRenderer);
            put(Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient").getClassName(), OldAnimationsTransformer.this::transformMinecraftClient);
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer").getClassName(), OldAnimationsTransformer.this::transformArmorFeatureRenderer);
        }
    };

    @Override
    public void run() {
        Sorus.getInstance().get(TransformerManager.class).register(this.getClass());
    }

    @Override
    public boolean canTransform(String name) {
        return transformers.keySet().stream().anyMatch(identifier -> identifier.equals(name));
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
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_1354(F)V");
        Identifier method_9873 = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_9873(FF)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(render.getMethodName()) && methodNode.desc.equals(render.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions) {
                    if (node instanceof MethodInsnNode && ((MethodInsnNode) node).name.equals(method_9873.getMethodName()) && ((MethodInsnNode) node).desc.equals(method_9873.getMethodDesc()) && node.getPrevious().getOpcode() == Opcodes.T_LONG) {
                        methodNode.instructions.remove(node.getPrevious());

                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.FLOAD, 4));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, OldAnimationsHook.class.getName().replace(".", "/"), "getPartialTicks", "(F)F"));
                        methodNode.instructions.insertBefore(node, insnList);
                    }
                }
            }
        }
    }

    private void transformMinecraftClient(ClassNode classNode) {
        Identifier tick = Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient#tick()V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(tick.getMethodName()) && methodNode.desc.equals(tick.getMethodDesc())) {
                methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, OldAnimationsHook.class.getName().replace(".", "/"), "updateSwing", "()V"));
            }
        }
    }

    private void transformArmorFeatureRenderer(ClassNode classNode) {
        Identifier combineTextures = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer#combineTextures()Z");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(combineTextures.getMethodName()) && methodNode.desc.equals(combineTextures.getMethodDesc())) {
                InsnList insnList = new InsnList();
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, OldAnimationsHook.class.getName().replace(".", "/"), "showArmorDamage", "()Z"));
                insnList.add(new InsnNode(Opcodes.IRETURN));

                methodNode.instructions.insert(insnList);
            }
        }
    }

}
