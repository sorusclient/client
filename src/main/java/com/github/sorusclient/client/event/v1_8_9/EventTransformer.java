package com.github.sorusclient.client.event.v1_8_9;

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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventTransformer implements Listener, ITransformer {

    private final Map<String, Consumer<ClassNode>> transformers = new HashMap<String, Consumer<ClassNode>>() {
        {
            put(Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer").getClassName(), EventTransformer.this::transformGameRenderer);
            put(Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient").getClassName(), EventTransformer.this::transformMinecraftClient);
            put(Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/Screen").getClassName(), EventTransformer.this::transformScreen);
            put(Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud").getClassName(), EventTransformer.this::transformInGameHud);
        }
    };

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(EventTransformer.class);
    }

    @Override
    public boolean canTransform(String name) {
        return transformers.containsKey(name);
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

    private void transformGameRenderer(ClassNode classNode) {
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#render(FJ)V");
        Identifier render2 = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#render(F)V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(render.getMethodName()) && methodNode.desc.equals(render.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.RETURN) {
                        methodNode.instructions.insertBefore(node,
                                new MethodInsnNode(Opcodes.INVOKESTATIC, EventHook.class.getName().replace(".", "/"), "onRender", "()V"));
                    }
                }
            }
        }
    }

    private void transformMinecraftClient(ClassNode classNode) {
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient#handleKeyInput()V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(render.getMethodName()) && methodNode.desc.equals(render.getMethodDesc())) {
                methodNode.instructions.insert(
                        new MethodInsnNode(Opcodes.INVOKESTATIC, EventHook.class.getName().replace(".", "/"), "onKey", "()V"));
            }
        }
    }

    private void transformScreen(ClassNode classNode) {
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/Screen#handleMouse()V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(render.getMethodName()) && methodNode.desc.equals(render.getMethodDesc())) {
                methodNode.instructions.insert(
                        new MethodInsnNode(Opcodes.INVOKESTATIC, EventHook.class.getName().replace(".", "/"), "onMouse", "()V"));
            }
        }
    }

    private void transformInGameHud(ClassNode classNode) {
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#render(F)V");
        Identifier setupHudMatrixMode = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#setupHudMatrixMode()V");

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(render.getMethodName()) && methodNode.desc.equals(render.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node instanceof MethodInsnNode && ((MethodInsnNode) node).owner.equals(setupHudMatrixMode.getClassName()) && ((MethodInsnNode) node).name.equals(setupHudMatrixMode.getMethodName()) && ((MethodInsnNode) node).desc.equals(setupHudMatrixMode.getMethodDesc())) {
                        methodNode.instructions.insert(node, new MethodInsnNode(Opcodes.INVOKESTATIC, EventHook.class.getName().replace(".", "/"), "onInGameRender", "()V"));
                    }
                }
            }
        }
    }

}
