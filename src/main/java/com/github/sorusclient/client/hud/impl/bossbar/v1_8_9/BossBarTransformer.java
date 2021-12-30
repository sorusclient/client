package com.github.sorusclient.client.hud.impl.bossbar.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.hud.impl.bossbar.IBossBarRenderer;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;

public class BossBarTransformer implements Listener, ITransformer {

    private final String IN_GAME_HUD = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud").getClassName();

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(BossBarTransformer.class);
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
        Identifier renderBossBar = com.github.glassmc.loader.util.Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderBossBar()V");
        Identifier framesToLive = com.github.glassmc.loader.util.Identifier.parse("v1_8_9/net/minecraft/entity/boss/BossBar#framesToLive");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(renderBossBar.getMethodName()) && methodNode.desc.equals(renderBossBar.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions) {
                    if (node instanceof FieldInsnNode && ((FieldInsnNode) node).owner.equals(framesToLive.getClassName()) && ((FieldInsnNode) node).name.equals(framesToLive.getFieldName())) {
                        methodNode.instructions.insert(node, new InsnNode(Opcodes.RETURN));
                    }
                }
            }
        }
    }

}
