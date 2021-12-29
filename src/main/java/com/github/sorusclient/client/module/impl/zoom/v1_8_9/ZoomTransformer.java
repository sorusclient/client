package com.github.sorusclient.client.module.impl.zoom.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.transform.TransformerManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ZoomTransformer implements Listener, ITransformer {

    private static final String GAME_RENDERER = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer").getClassName();

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(ZoomTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return GAME_RENDERER.equals(name);
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
        Identifier getFov = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#getFov(FZ)F");
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#render(FJ)V");
        Identifier tick = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#tick()V");

        Identifier smoothCameraEnabled = Identifier.parse("v1_8_9/net/minecraft/client/options/GameOptions#smoothCameraEnabled");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(getFov.getMethodName()) && methodNode.desc.equals(getFov.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.FRETURN && node.getPrevious() instanceof VarInsnNode) {
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.FLOAD, 4));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ZoomHook.class.getName().replace(".", "/"), "modifyFOV", "(F)F"));
                        insnList.add(new VarInsnNode(Opcodes.FSTORE, 4));
                        methodNode.instructions.insertBefore(node.getPrevious(), insnList);
                    }
                }
            }

            if (methodNode.name.equals(render.getMethodName()) && methodNode.desc.equals(render.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 5 && node.getPrevious().getOpcode() == Opcodes.FADD) {
                        InsnList insnList = new InsnList();
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ZoomHook.class.getName().replace(".", "/"), "modifySensitivity", "(F)F"));
                        methodNode.instructions.insertBefore(node, insnList);
                    }

                    if (node instanceof FieldInsnNode && ((FieldInsnNode) node).owner.equals(smoothCameraEnabled.getClassName()) && ((FieldInsnNode) node).name.equals(smoothCameraEnabled.getFieldName())) {
                        this.insertZoomCinematicCheck(methodNode, node);
                    }
                }
            }

            if (methodNode.name.equals(tick.getMethodName()) && methodNode.desc.equals(tick.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof FieldInsnNode && ((FieldInsnNode) node).owner.equals(smoothCameraEnabled.getClassName()) && ((FieldInsnNode) node).name.equals(smoothCameraEnabled.getFieldName())) {
                        this.insertZoomCinematicCheck(methodNode, node);
                    }
                }
            }
        }
    }

    private void insertZoomCinematicCheck(MethodNode methodNode, AbstractInsnNode node) {
        InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ZoomHook.class.getName().replace(".", "/"), "useCinematicCamera", "()Z"));
        insnList.add(new JumpInsnNode(Opcodes.IFNE, (LabelNode) node.getNext().getNext()));

        methodNode.instructions.insertBefore(node.getPrevious().getPrevious().getPrevious(), insnList);
    }

}
