package com.github.sorusclient.client.hud.impl.health.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class HealthTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(HealthTransformer.class);
    }

    public HealthTransformer() {
        this.register("v1_8_9/net/minecraft/client/gui/hud/InGameHud", this::transformInGameHud);
    }

    private void transformInGameHud(ClassNode classNode) {
        Identifier renderStatusBars = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderStatusBars(Lv1_8_9/net/minecraft/client/util/Window;)V");
        Identifier vehicle = Identifier.parse("v1_8_9/net/minecraft/entity/player/PlayerEntity#vehicle");
        Identifier drawTexture = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#drawTexture(IIIIII)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(renderStatusBars.getMethodName()) && methodNode.desc.equals(renderStatusBars.getMethodDesc())) {
                boolean foundHealth = false;
                for (AbstractInsnNode node : methodNode.instructions) {
                    if (node.getNext().getNext().getNext() instanceof LdcInsnNode && ((LdcInsnNode) node.getNext().getNext().getNext()).cst.equals("health")) {
                        foundHealth = true;
                    }

                    if (node.getNext() instanceof FieldInsnNode && ((FieldInsnNode) node.getNext()).owner.equals(vehicle.getClassName()) && ((FieldInsnNode) node.getNext()).name.equals(vehicle.getFieldName())) {
                        break;
                    }

                    if (foundHealth) {
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
