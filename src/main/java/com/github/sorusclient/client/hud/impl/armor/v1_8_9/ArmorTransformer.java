package com.github.sorusclient.client.hud.impl.armor.v1_8_9;

import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.transform.TransformerManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ArmorTransformer implements Listener, ITransformer {

    private final String IN_GAME_HUD = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud").getClassName();

    @Override
    public void run() {
        Sorus.getInstance().get(TransformerManager.class).register(ArmorTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return name.equals(IN_GAME_HUD);
    }

    @Override
    public byte[] transform(String name, byte[] data) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, 0);

        this.transformInGameHud(classNode);

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private void transformInGameHud(ClassNode classNode) {
        Identifier renderStatusBars = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderStatusBars(Lv1_8_9/net/minecraft/client/util/Window;)V");
        Identifier client = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#client");
        Identifier drawTexture = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#drawTexture(IIIIII)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(renderStatusBars.getMethodName()) && methodNode.desc.equals(renderStatusBars.getMethodDesc())) {
                boolean foundArmor = false;
                for (AbstractInsnNode node : methodNode.instructions) {
                    AbstractInsnNode nodeNext = node.getNext().getNext().getNext();
                    if (nodeNext instanceof LdcInsnNode && ((LdcInsnNode) nodeNext).cst.equals("armor")) {
                        foundArmor = true;
                    }

                    if (foundArmor && nodeNext instanceof FieldInsnNode && ((FieldInsnNode) nodeNext).owner.equals(client.getClassName()) && ((FieldInsnNode) nodeNext).name.equals(client.getFieldName())) {
                        break;
                    }

                    if (foundArmor) {
                        if (node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals(drawTexture.getClassName()) && ((MethodInsnNode) node).name.equals(drawTexture.getMethodName()) && ((MethodInsnNode) node).desc.equals(drawTexture.getMethodDesc())) {
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.POP2));
                            insnList.add(new InsnNode(Opcodes.POP2));
                            insnList.add(new InsnNode(Opcodes.POP2));
                            insnList.add(new InsnNode(Opcodes.POP));
                            methodNode.instructions.insertBefore(node, insnList);
                            methodNode.instructions.remove(node);
                        }
                    }
                }
            }
        }

    }

}
