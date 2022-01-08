package com.github.sorusclient.client.module.impl.enhancements.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class EnhancementsTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(EnhancementsTransformer.class);
    }

    public EnhancementsTransformer() {
        this.register("v1_8_9/net/minecraft/client/render/item/HeldItemRenderer", this::transformHeldItemRenderer);
        this.register("v1_8_9/net/minecraft/client/gui/screen/ingame/InventoryScreen", this::transformInventoryScreen);
        this.register("v1_8_9/net/minecraft/client/render/entity/EntityRenderDispatcher", this::transformEntityRenderDispatcher);
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
