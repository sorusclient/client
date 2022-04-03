package com.github.sorusclient.client.adapter.v1_8_9.event

import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier.Insert
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class EventTransformer : Transformer() {

    init {
        setHookClass(EventHook::class.java)
        register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer)
        register("v1_8_9/net/minecraft/client/MinecraftClient", this::transformMinecraftClient)
        register("v1_8_9/net/minecraft/client/gui/screen/Screen", this::transformScreen)
        register("v1_8_9/net/minecraft/client/gui/hud/InGameHud", this::transformInGameHud)
        register("v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler", this::transformClientPlayerNetworkHandler)
        register("v1_8_9/net/minecraft/client/gui/hud/ChatHud", this::transformChatHud)
        register("v1_8_9/net/minecraft/client/ClientBrandRetriever", this::transformClientBrandRetriever)
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val render = "v1_8_9/net/minecraft/client/render/GameRenderer#render(FJ)V".toIdentifier()
        findMethod(classNode, render)
            .apply { methodNode: MethodNode ->
                findReturns(methodNode)
                    .apply(InsertBefore(methodNode, this.getHook("onRender")))
            }

        val getFov = "v1_8_9/net/minecraft/client/render/GameRenderer#getFov(FZ)F".toIdentifier()

        findMethod(classNode, getFov)
                .apply { methodNode ->
                    findReturns(methodNode)
                            .apply(InsertBefore(methodNode, createList { insnList ->
                                insnList.add(this.getHook("onGetFOV"))
                            }))
                }

        findMethod(classNode, render)
                .apply { methodNode ->
                    findVarReferences(methodNode, 5, VarReferenceType.STORE)
                            .nth(0)
                            .apply(InsertBefore(methodNode, createList { insnList ->
                                insnList.add(this.getHook("onGetSensitivity"))
                            }))
                }


        val updateLightmap = "v1_8_9/net/minecraft/client/render/GameRenderer#updateLightmap(F)V".toIdentifier()

        findMethod(classNode, updateLightmap)
                .apply { methodNode ->
                    findVarReferences(methodNode, 17, VarReferenceType.STORE)
                            .nth(2)
                            .apply(InsertBefore(methodNode, createList { insnList ->
                                insnList.add(this.getHook("onGetGamma"))
                            }))
                }

        val tick = "v1_8_9/net/minecraft/client/render/GameRenderer#tick()V".toIdentifier()
        val smoothCameraEnabled = "v1_8_9/net/minecraft/client/options/GameOptions#smoothCameraEnabled".toIdentifier()
        for (methodNode in classNode.methods) {
            if (methodNode.name == render.methodName && methodNode.desc == render.methodDesc) {
                for (node in methodNode.instructions.toArray()) {
                    if (node is FieldInsnNode && node.owner == smoothCameraEnabled.className && node.name == smoothCameraEnabled.fieldName) {
                        insertZoomCinematicCheck(methodNode, node)
                    }
                }
            }
            if (methodNode.name == tick.methodName && methodNode.desc == tick.methodDesc) {
                for (node in methodNode.instructions.toArray()) {
                    if (node is FieldInsnNode && node.owner == smoothCameraEnabled.className && node.name == smoothCameraEnabled.fieldName) {
                        insertZoomCinematicCheck(methodNode, node)
                    }
                }
            }
        }
    }

    private fun insertZoomCinematicCheck(methodNode: MethodNode, node: AbstractInsnNode) {
        methodNode.instructions.insert(node, this.getHook("onGetUseCinematicCamera"))
    }

    private fun transformMinecraftClient(classNode: ClassNode) {
        val handleKeyInput = "v1_8_9/net/minecraft/client/MinecraftClient#handleKeyInput()V".toIdentifier()
        val connect = "v1_8_9/net/minecraft/client/MinecraftClient#connect(Lv1_8_9/net/minecraft/client/world/ClientWorld;Ljava/lang/String;)V".toIdentifier()
        findMethod(classNode, handleKeyInput)
            .apply(Insert(this.getHook("onKey")))
        findMethod(classNode, connect)
            .apply(Insert(createList { insnList: InsnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 2))
                insnList.add(this.getHook("onConnect"))
            }))

        val initializeGame = "v1_8_9/net/minecraft/client/MinecraftClient#initializeGame()V".toIdentifier()
        val createContext = "v1_8_9/com/mojang/blaze3d/platform/GLX#createContext()V".toIdentifier()
        findMethod(classNode, initializeGame)
                .apply { methodNode ->
                    findMethodCalls(methodNode, createContext)
                            .apply(InsertBefore(methodNode, createList { insnList ->
                                insnList.add(this.getHook("onInitialize"))
                            }))
                }

        val tick = "v1_8_9/net/minecraft/client/MinecraftClient#tick()V".toIdentifier()
        findMethod(classNode, tick)
                .apply(Insert(createList { insnList ->
                    insnList.add(this.getHook("onTick"))
                }))

        val openScreen = "v1_8_9/net/minecraft/client/MinecraftClient#openScreen(Lv1_8_9/net/minecraft/client/gui/Screen;)V".toIdentifier()
        findMethod(classNode, openScreen)
            .apply(Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(this.getHook("onOpenScreen"))
            }))

        val getEventButton = "org/lwjgl/input/Mouse#getEventButton()I".toIdentifier()

        findMethod(classNode, tick)
            .apply { methoNode ->
                findMethodCalls(methoNode, getEventButton)
                    .apply(InsertBefore(methoNode, createList { insnList ->
                        insnList.add(this.getHook("onMouse"))
                    }))
            }
    }

    private fun transformScreen(classNode: ClassNode) {
        val handleMouse = "v1_8_9/net/minecraft/client/gui/screen/Screen#handleMouse()V".toIdentifier()
        findMethod(classNode, handleMouse)
            .apply(Insert(this.getHook("onMouse")))
    }

    private fun transformInGameHud(classNode: ClassNode) {
        val render = "v1_8_9/net/minecraft/client/gui/hud/InGameHud#render(F)V".toIdentifier()
        val setupHudMatrixMode = "v1_8_9/net/minecraft/client/render/GameRenderer#setupHudMatrixMode()V".toIdentifier()

        findMethod(classNode, render)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, setupHudMatrixMode)
                    .apply(InsertAfter(methodNode, this.getHook("onInGameRender")))
            }

        val renderStatusBars = "v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderStatusBars(Lv1_8_9/net/minecraft/client/util/Window;)V".toIdentifier()
        val push = "v1_8_9/net/minecraft/util/profiler/Profiler#push(Ljava/lang/String;)V".toIdentifier()
        val swap = "v1_8_9/net/minecraft/util/profiler/Profiler#swap(Ljava/lang/String;)V".toIdentifier()
        findMethod(classNode, renderStatusBars)
            .apply { methodNode: MethodNode ->
                val labelNode = LabelNode()
                findMethodCalls(methodNode, push)
                    .apply(InsertAfter(methodNode, createList { insnList: InsnList ->
                        val eventClassName = ArmorBarRenderEvent::class.java.name.replace(".", "/")
                        insnList.add(this.getHook("onArmorBarRender"))
                        val index = methodNode.maxLocals
                        insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                        methodNode.maxLocals++
                        insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                        insnList.add(JumpInsnNode(Opcodes.IFNE, labelNode))
                    }))
                findMethodCalls(methodNode, swap)
                    .nth(0)
                    .apply { methodInsnNode: MethodInsnNode ->
                        methodNode.instructions.insertBefore(
                            methodInsnNode.previous.previous.previous.previous,
                            labelNode
                        )
                    }
            }

        val renderBossBar = "v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderBossBar()V".toIdentifier()
        val framesToLive = "v1_8_9/net/minecraft/entity/boss/BossBar#framesToLive".toIdentifier()
        findMethod(classNode, renderBossBar)
            .apply { methodNode: MethodNode ->
                findFieldReferences(methodNode, framesToLive, FieldReferenceType.PUT)
                    .apply(InsertAfter(methodNode, createList { insnList: InsnList ->
                        val eventClassName = BossBarRenderEvent::class.java.name.replace(".", "/")
                        insnList.add(this.getHook("onBossBarRender"))
                        val index = methodNode.maxLocals
                        insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                        methodNode.maxLocals++
                        val labelNode = LabelNode()
                        insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                        insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                        insnList.add(InsnNode(Opcodes.RETURN))
                        insnList.add(labelNode)
                    }))
            }

        val renderExperienceBar = "v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderExperienceBar(Lv1_8_9/net/minecraft/client/util/Window;I)V".toIdentifier()
        findMethod(classNode, renderExperienceBar)
            .apply(Insert { methodNode: MethodNode ->
                createList { insnList: InsnList ->
                    val eventClassName = ExperienceBarRenderEvent::class.java.name.replace(".", "/")
                    insnList.add(this.getHook("onExperienceBarRender"))
                    val index = methodNode.maxLocals
                    insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                    methodNode.maxLocals++
                    val labelNode = LabelNode()
                    insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                    insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                    insnList.add(InsnNode(Opcodes.RETURN))
                    insnList.add(labelNode)
                }
            })

        val vehicle = "v1_8_9/net/minecraft/entity/player/PlayerEntity#vehicle".toIdentifier()
        findMethod(classNode, renderStatusBars)
            .apply { methodNode: MethodNode ->
                val labelNode = LabelNode()
                findMethodCalls(methodNode, swap)
                    .nth(0)
                    .apply(InsertAfter(methodNode, createList { insnList: InsnList ->
                        val eventClassName = HealthBarRenderEvent::class.java.name.replace(".", "/")
                        insnList.add(this.getHook("onHealthBarRender"))
                        val index = methodNode.maxLocals
                        insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                        methodNode.maxLocals++
                        insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                        insnList.add(JumpInsnNode(Opcodes.IFNE, labelNode))
                    }))
                findFieldReferences(methodNode, vehicle, FieldReferenceType.GET)
                    .apply { methodInsnNode: FieldInsnNode ->
                        methodNode.instructions.insertBefore(
                            methodInsnNode.previous,
                            labelNode
                        )
                    }
            }

        val renderHotBar = "v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderHotbar(Lv1_8_9/net/minecraft/client/util/Window;F)V".toIdentifier()
        findMethod(classNode, renderHotBar)
            .apply(Insert { methodNode: MethodNode ->
                createList { insnList: InsnList ->
                    val eventClassName = HotBarRenderEvent::class.java.name.replace(".", "/")
                    insnList.add(this.getHook("onHotBarRender"))
                    val index = methodNode.maxLocals
                    insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                    methodNode.maxLocals++
                    val labelNode = LabelNode()
                    insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                    insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                    insnList.add(InsnNode(Opcodes.RETURN))
                    insnList.add(labelNode)
                }
            })

        findMethod(classNode, renderStatusBars)
            .apply { methodNode: MethodNode ->
                val labelNode = LabelNode()
                findVarReferences(methodNode, 34, VarReferenceType.STORE)
                    .apply(InsertAfter(methodNode, createList { insnList: InsnList ->
                        val eventClassName = HungerBarRenderEvent::class.java.name.replace(".", "/")
                        insnList.add(this.getHook("onHungerBarRender"))
                        val index = methodNode.maxLocals
                        insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                        methodNode.maxLocals++
                        insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                        insnList.add(JumpInsnNode(Opcodes.IFNE, labelNode))
                    }))
                findMethodCalls(methodNode, swap)
                    .nth(3)
                    .apply { methodInsnNode: MethodInsnNode ->
                        methodNode.instructions.insertBefore(
                            methodInsnNode.previous.previous.previous.previous,
                            labelNode
                        )
                    }
            }
        val renderScoreboardObjective = "v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderScoreboardObjective(Lv1_8_9/net/minecraft/scoreboard/ScoreboardObjective;Lv1_8_9/net/minecraft/client/util/Window;)V".toIdentifier()
        findMethod(classNode, renderScoreboardObjective)
            .apply(Insert { methodNode: MethodNode ->
                createList { insnList: InsnList ->
                    val eventClassName = SideBarRenderEvent::class.java.name.replace(".", "/")
                    insnList.add(this.getHook("onSideBarRender"))
                    val index = methodNode.maxLocals
                    insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                    methodNode.maxLocals++
                    val labelNode = LabelNode()
                    insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                    insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                    insnList.add(InsnNode(Opcodes.RETURN))
                    insnList.add(labelNode)
                }
            })

        for (methodNode in classNode.methods) {
            if (methodNode.name == render.methodName && methodNode.desc == render.methodDesc) {
                val method9429 = "v1_8_9/net/minecraft/client/gui/hud/InGameHud#method_9429()Z".toIdentifier()
                for (node in methodNode.instructions) {
                    if (node is MethodInsnNode && node.owner == method9429.className && node.name == method9429.methodName && node.desc == method9429.methodDesc) {
                        val jumpInsnNode = node.getNext() as JumpInsnNode
                        val insnList = InsnList()
                        insnList.add(
                            MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                EventHook::class.java.name.replace(".", "/"),
                                "onRenderCrosshair",
                                "()Z"
                            )
                        )
                        insnList.add(JumpInsnNode(Opcodes.IFNE, jumpInsnNode.label))

                        methodNode.instructions.insertBefore(node.previous, insnList)
                    }
                }
            }
        }
    }

    private fun transformClientPlayerNetworkHandler(classNode: ClassNode) {
        val onCustomPayload = "v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler#onCustomPayload(Lv1_8_9/net/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V".toIdentifier()
        val onGameJoin = "v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler#onGameJoin(Lv1_8_9/net/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V".toIdentifier()
        findMethod(classNode, onCustomPayload)
            .apply(Insert(createList { insnList: InsnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(this.getHook("onCustomPayload"))
            }))
        findMethod(classNode, onGameJoin)
            .apply(Insert(this.getHook("onGameJoin")))
    }

    private fun transformChatHud(classNode: ClassNode) {
        val addMessage = "v1_8_9/net/minecraft/client/gui/hud/ChatHud#addMessage(Lv1_8_9/net/minecraft/text/Text;I)V".toIdentifier()
        findMethod(classNode, addMessage)
            .apply(Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(this.getHook("onChatReceived"))
                insnList.add(VarInsnNode(Opcodes.ASTORE, 1))
            }))
    }

    private fun transformClientBrandRetriever(classNode: ClassNode) {
        val getClientModName = "v1_8_9/net/minecraft/client/ClientBrandRetriever#getClientModName()Ljava/lang/String;".toIdentifier()

        findMethod(classNode, getClientModName)
                .apply { methodNode ->
                    findReturns(methodNode)
                            .apply(InsertBefore(methodNode, createList { insnList ->
                                insnList.add(this.getHook("onGetClientBrand"))
                            }))
                }
    }

}