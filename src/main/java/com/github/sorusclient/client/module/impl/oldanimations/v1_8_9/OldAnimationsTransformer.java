package com.github.sorusclient.client.module.impl.oldanimations.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class OldAnimationsTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(this.getClass());
    }

    public OldAnimationsTransformer() {
        this.register("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer", this::transformHeldItemRenderer);
        this.register("v1_8_9/net/minecraft/client/MinecraftClient", this::transformMinecraftClient);
        this.register("v1_8_9/net/minecraft/client/render/entity/feature/ArmorFeatureRenderer", this::transformArmorFeatureRenderer);
    }

    private void transformHeldItemRenderer(ClassNode classNode) {
        Identifier method_1354 = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_1354(F)V");
        Identifier method_9873 = Identifier.parse("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer#method_9873(FF)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(method_1354.getMethodName()) && methodNode.desc.equals(method_1354.getMethodDesc())) {
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
