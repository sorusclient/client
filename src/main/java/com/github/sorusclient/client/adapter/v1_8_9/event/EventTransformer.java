package com.github.sorusclient.client.adapter.v1_8_9.event;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class EventTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(EventTransformer.class);
    }
    
    public EventTransformer() {
        this.setHookClass(EventHook.class);
        this.register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer);
        this.register("v1_8_9/net/minecraft/client/MinecraftClient", this::transformMinecraftClient);
        this.register("v1_8_9/net/minecraft/client/gui/screen/Screen", this::transformScreen);
        this.register("v1_8_9/net/minecraft/client/gui/hud/InGameHud", this::transformInGameHud);
        this.register("v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler", this::transformClientPlayerNetworkHandler);
    }

    private void transformGameRenderer(ClassNode classNode) {
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#render(FJ)V");

        this.findMethod(classNode, render)
                .apply(methodNode -> this.findReturns(methodNode)
                    .apply(new Applier.InsertBefore<>(methodNode, this.getHook("onRender"))));
    }

    private void transformMinecraftClient(ClassNode classNode) {
        Identifier handleKeyInput = Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient#handleKeyInput()V");
        Identifier connect = Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient#connect(Lv1_8_9/net/minecraft/client/world/ClientWorld;Ljava/lang/String;)V");

        this.findMethod(classNode, handleKeyInput)
                .apply(new Applier.Insert<>(this.getHook("onKey")));
        this.findMethod(classNode, connect)
                .apply(new Applier.Insert<>(this.createList(insnList -> {
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    insnList.add(this.getHook("onConnect"));
                })));
    }

    private void transformScreen(ClassNode classNode) {
        Identifier handleMouse = Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/Screen#handleMouse()V");

        this.findMethod(classNode, handleMouse)
                .apply(new Applier.Insert<>(this.getHook("onMouse")));
    }

    private void transformInGameHud(ClassNode classNode) {
        Identifier render = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#render(F)V");
        Identifier setupHudMatrixMode = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#setupHudMatrixMode()V");

        this.findMethod(classNode, render)
                .apply(methodNode -> this.findMethodCalls(methodNode, setupHudMatrixMode)
                        .apply(new Applier.InsertAfter<>(methodNode, this.getHook("onInGameRender"))));
    }

    private void transformClientPlayerNetworkHandler(ClassNode classNode) {
        Identifier onCustomPayload = Identifier.parse("v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler#onCustomPayload(Lv1_8_9/net/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V");
        Identifier onGameJoin = Identifier.parse("v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler#onGameJoin(Lv1_8_9/net/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V");

        this.findMethod(classNode, onCustomPayload)
                .apply(new Applier.Insert<>(this.createList(insnList -> {
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insnList.add(this.getHook("onCustomPayload"));
                })));
        this.findMethod(classNode, onGameJoin)
                .apply(new Applier.Insert<>(this.getHook("onGameJoin")));
    }

}
