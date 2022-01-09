package com.github.sorusclient.client.adapter.v1_8_9.event;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.adapter.event.*;
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

        Identifier renderStatusBars = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderStatusBars(Lv1_8_9/net/minecraft/client/util/Window;)V");

        Identifier push = Identifier.parse("v1_8_9/net/minecraft/util/profiler/Profiler#push(Ljava/lang/String;)V");
        Identifier swap = Identifier.parse("v1_8_9/net/minecraft/util/profiler/Profiler#swap(Ljava/lang/String;)V");

        this.findMethod(classNode, renderStatusBars)
                .apply(methodNode -> {
                    LabelNode labelNode = new LabelNode();
                    this.findMethodCalls(methodNode, push)
                            .apply(new Applier.InsertAfter<>(methodNode, this.createList(insnList -> {
                                String eventClassName = ArmorBarRenderEvent.class.getName().replace(".", "/");
                                insnList.add(this.getHook("onArmorBarRender"));
                                int index = methodNode.maxLocals;
                                insnList.add(new VarInsnNode(Opcodes.ASTORE, index));
                                methodNode.maxLocals++;

                                insnList.add(new VarInsnNode(Opcodes.ALOAD, index));
                                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"));
                                insnList.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
                            })));

                    this.findMethodCalls(methodNode, swap)
                            .nth(0)
                            .apply(methodInsnNode -> methodNode.instructions.insertBefore(methodInsnNode.getPrevious().getPrevious().getPrevious().getPrevious(), labelNode));
                });

        Identifier renderBossBar = com.github.glassmc.loader.util.Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderBossBar()V");
        Identifier framesToLive = com.github.glassmc.loader.util.Identifier.parse("v1_8_9/net/minecraft/entity/boss/BossBar#framesToLive");

        this.findMethod(classNode, renderBossBar)
                .apply(methodNode -> this.findFieldReferences(methodNode, framesToLive, FieldReferenceType.PUT)
                        .apply(new Applier.InsertAfter<>(methodNode, this.createList(insnList -> {
                            String eventClassName = BossBarRenderEvent.class.getName().replace(".", "/");
                            insnList.add(this.getHook("onBossBarRender"));
                            int index = methodNode.maxLocals;
                            insnList.add(new VarInsnNode(Opcodes.ASTORE, index));
                            methodNode.maxLocals++;

                            LabelNode labelNode = new LabelNode();

                            insnList.add(new VarInsnNode(Opcodes.ALOAD, index));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"));
                            insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                            insnList.add(new InsnNode(Opcodes.RETURN));
                            insnList.add(labelNode);
                        }))));

        Identifier renderExperienceBar = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderExperienceBar(Lv1_8_9/net/minecraft/client/util/Window;I)V");

        this.findMethod(classNode, renderExperienceBar)
                .apply(new Applier.Insert<>(methodNode -> this.createList(insnList -> {
                    String eventClassName = ExperienceBarRenderEvent.class.getName().replace(".", "/");
                    insnList.add(this.getHook("onExperienceBarRender"));
                    int index = methodNode.maxLocals;
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, index));
                    methodNode.maxLocals++;

                    LabelNode labelNode = new LabelNode();

                    insnList.add(new VarInsnNode(Opcodes.ALOAD, index));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"));
                    insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                    insnList.add(new InsnNode(Opcodes.RETURN));
                    insnList.add(labelNode);
                })));

        Identifier vehicle = Identifier.parse("v1_8_9/net/minecraft/entity/player/PlayerEntity#vehicle");

        this.findMethod(classNode, renderStatusBars)
                .apply(methodNode -> {
                    LabelNode labelNode = new LabelNode();
                    this.findMethodCalls(methodNode, swap)
                            .nth(0)
                            .apply(new Applier.InsertAfter<>(methodNode, this.createList(insnList -> {
                                String eventClassName = HealthBarRenderEvent.class.getName().replace(".", "/");
                                insnList.add(this.getHook("onHealthBarRender"));
                                int index = methodNode.maxLocals;
                                insnList.add(new VarInsnNode(Opcodes.ASTORE, index));
                                methodNode.maxLocals++;

                                insnList.add(new VarInsnNode(Opcodes.ALOAD, index));
                                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"));
                                insnList.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
                            })));

                    this.findFieldReferences(methodNode, vehicle, FieldReferenceType.GET)
                            .apply(methodInsnNode -> methodNode.instructions.insertBefore(methodInsnNode.getPrevious(), labelNode));
                });

        Identifier renderHotBar = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderHotbar(Lv1_8_9/net/minecraft/client/util/Window;F)V");

        this.findMethod(classNode, renderHotBar)
                .apply(new Applier.Insert<>(methodNode -> this.createList(insnList -> {
                    String eventClassName = HotBarRenderEvent.class.getName().replace(".", "/");
                    insnList.add(this.getHook("onHotBarRender"));
                    int index = methodNode.maxLocals;
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, index));
                    methodNode.maxLocals++;

                    LabelNode labelNode = new LabelNode();

                    insnList.add(new VarInsnNode(Opcodes.ALOAD, index));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"));
                    insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                    insnList.add(new InsnNode(Opcodes.RETURN));
                    insnList.add(labelNode);
                })));

        this.findMethod(classNode, renderStatusBars)
                .apply(methodNode -> {
                    LabelNode labelNode = new LabelNode();
                    this.findVarReferences(methodNode, 34, VarReferenceType.STORE)
                            .apply(new Applier.InsertAfter<>(methodNode, this.createList(insnList -> {
                                String eventClassName = HungerBarRenderEvent.class.getName().replace(".", "/");
                                insnList.add(this.getHook("onHungerBarRender"));
                                int index = methodNode.maxLocals;
                                insnList.add(new VarInsnNode(Opcodes.ASTORE, index));
                                methodNode.maxLocals++;

                                insnList.add(new VarInsnNode(Opcodes.ALOAD, index));
                                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"));
                                insnList.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
                            })));

                    this.findMethodCalls(methodNode, swap)
                            .nth(3)
                            .apply(methodInsnNode -> methodNode.instructions.insertBefore(methodInsnNode.getPrevious().getPrevious().getPrevious().getPrevious(), labelNode));
                });

        Identifier renderScoreboardObjective = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderScoreboardObjective(Lv1_8_9/net/minecraft/scoreboard/ScoreboardObjective;Lv1_8_9/net/minecraft/client/util/Window;)V");

        this.findMethod(classNode, renderScoreboardObjective)
                .apply(new Applier.Insert<>(methodNode -> this.createList(insnList -> {
                    String eventClassName = SideBarRenderEvent.class.getName().replace(".", "/");
                    insnList.add(this.getHook("onSideBarRender"));
                    int index = methodNode.maxLocals;
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, index));
                    methodNode.maxLocals++;

                    LabelNode labelNode = new LabelNode();

                    insnList.add(new VarInsnNode(Opcodes.ALOAD, index));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"));
                    insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                    insnList.add(new InsnNode(Opcodes.RETURN));
                    insnList.add(labelNode);
                })));
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
