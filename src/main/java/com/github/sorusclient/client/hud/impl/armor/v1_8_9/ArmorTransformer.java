package com.github.sorusclient.client.hud.impl.armor.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ArmorTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(ArmorTransformer.class);
    }

    public ArmorTransformer() {
        this.register("v1_8_9/net/minecraft/client/gui/hud/InGameHud", this::transformInGameHud);
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
