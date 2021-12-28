package com.github.sorusclient.client.module.impl.itemphysics.v1_8_9;

import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.transform.TransformerManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ItemPhysicsTransformer implements Listener, ITransformer {

    private static final String ITEM_ENTITY_RENDERER = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer").getClassName();

    @Override
    public void run() {
        Sorus.getInstance().get(TransformerManager.class).register(ItemPhysicsTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return ITEM_ENTITY_RENDERER.equals(name);
    }

    @Override
    public byte[] transform(String name, byte[] data) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        this.transformItemEntityRenderer(classNode);

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private void transformItemEntityRenderer(ClassNode classNode) {
        Identifier method_10221 = Identifier.parse("v1_8_9/net/minecraft/client/render/entity/ItemEntityRenderer#method_10221(Lv1_8_9/net/minecraft/entity/ItemEntity;DDDFLv1_8_9/net/minecraft/client/render/model/BakedModel;)I");
        Identifier color4f = Identifier.parse("v1_8_9/com/mojang/blaze3d/platform/GlStateManager#color4f(FFFF)V");
        Identifier itemEntity = Identifier.parse("v1_8_9/net/minecraft/entity/ItemEntity");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(method_10221.getMethodName()) && methodNode.desc.equals(method_10221.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 15) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, ItemPhysicsHook.class.getName().replace(".", "/"), "modifyItemBob", "(F)F"));
                    }
                    if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 17) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, ItemPhysicsHook.class.getName().replace(".", "/"), "modifyItemRotate", "(F)F"));
                    }
                    if (node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals (color4f.getClassName()) && ((MethodInsnNode) node).name.equals(color4f.getMethodName())) {
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ItemPhysicsHook.class.getName().replace(".", "/"), "preRenderItem", "(L" + itemEntity.getClassName() + ";)V"));
                        methodNode.instructions.insert(node, insnList);
                    }
                }
            }
        }
    }

}
