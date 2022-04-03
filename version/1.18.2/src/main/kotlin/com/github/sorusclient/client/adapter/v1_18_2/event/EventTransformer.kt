package com.github.sorusclient.client.adapter.v1_18_2.event

import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Applier.Insert
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class EventTransformer : Transformer() {

    init {
        setHookClass(EventHook::class.java)
        register("v1_18_2/net/minecraft/client/gui/hud/InGameHud", this::transformInGameHud)
        register("v1_18_2/net/minecraft/client/gui/hud/BossBarHud", this::transformBossBarHud)
        register("v1_18_2/net/minecraft/client/Keyboard", this::transformKeyboard)
        register("v1_18_2/net/minecraft/client/MinecraftClient", this::transformMinecraftClient)
        register("v1_18_2/net/minecraft/client/render/GameRenderer", this::transformGameRenderer)
        register("v1_18_2/net/minecraft/client/Mouse", this::transformMouse)
        register("v1_18_2/net/minecraft/client/util/Window", this::transformWindow)
        register("v1_18_2/net/minecraft/client/render/LightmapTextureManager", this::transformLightMapTextureManager)
        register("v1_18_2/net/minecraft/client/network/ClientPlayNetworkHandler", this::transformClientPlayerNetworkHandler)
        register("org/lwjgl/opengl/GL30", this::transformGl30)
        register("org/lwjgl/opengl/GL15", this::transformGl15)
        register("org/lwjgl/opengl/GL20", this::transformGl20)
    }

    private fun transformInGameHud(classNode: ClassNode) {
        val render = "v1_18_2/net/minecraft/client/gui/hud/InGameHud#render(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;F)V".toIdentifier()

        findMethod(classNode, render)
            .apply(Insert(createList { insnList ->
                insnList.add(this.getHook("onInGameRender"))
            }))

        val renderScoreboardSidebar = "v1_18_2/net/minecraft/client/gui/hud/InGameHud#renderScoreboardSidebar(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;Lv1_18_2/net/minecraft/scoreboard/ScoreboardObjective;)V".toIdentifier()

        findMethod(classNode, renderScoreboardSidebar)
            .apply(Insert(createList { insnList ->
                val eventClassName = SideBarRenderEvent::class.java.name.replace(".", "/")
                insnList.add(this.getHook("onSideBarRender"))
                val labelNode = LabelNode()
                insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                insnList.add(InsnNode(Opcodes.RETURN))
                insnList.add(labelNode)
            }))

        val renderHotbar = "v1_18_2/net/minecraft/client/gui/hud/InGameHud#renderHotbar(FLv1_18_2/net/minecraft/client/util/math/MatrixStack;)V".toIdentifier()

        findMethod(classNode, renderHotbar)
            .apply(Insert(createList { insnList ->
                val eventClassName = HotBarRenderEvent::class.java.name.replace(".", "/")
                insnList.add(this.getHook("onHotBarRender"))
                val labelNode = LabelNode()
                insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                insnList.add(InsnNode(Opcodes.RETURN))
                insnList.add(labelNode)
            }))

        val renderExperienceBar = "v1_18_2/net/minecraft/client/gui/hud/InGameHud#renderExperienceBar(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;I)V".toIdentifier()

        findMethod(classNode, renderExperienceBar)
            .apply(Insert(createList { insnList ->
                val eventClassName = ExperienceBarRenderEvent::class.java.name.replace(".", "/")
                insnList.add(this.getHook("onExperienceBarRender"))
                val labelNode = LabelNode()
                insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                insnList.add(InsnNode(Opcodes.RETURN))
                insnList.add(labelNode)
            }))

        val renderHealthBar = "v1_18_2/net/minecraft/client/gui/hud/InGameHud#renderHealthBar(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;Lv1_18_2/net/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V".toIdentifier()

        findMethod(classNode, renderHealthBar)
            .apply(Insert(createList { insnList ->
                val eventClassName = HealthBarRenderEvent::class.java.name.replace(".", "/")
                insnList.add(this.getHook("onHealthBarRender"))
                val labelNode = LabelNode()
                insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                insnList.add(InsnNode(Opcodes.RETURN))
                insnList.add(labelNode)
            }))

        val renderStatusBars = "v1_18_2/net/minecraft/client/gui/hud/InGameHud#renderStatusBars(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;)V".toIdentifier()

        findMethod(classNode, renderStatusBars)
            .apply { methodNode ->
                val labelNode = LabelNode()

                for (node in methodNode.instructions) {
                    if (node is LdcInsnNode && node.cst == "armor") {
                        val insnList = InsnList()

                        val eventClassName = ArmorBarRenderEvent::class.java.name.replace(".", "/")
                        insnList.add(this.getHook("onArmorBarRender"))
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                        insnList.add(JumpInsnNode(Opcodes.IFNE, labelNode))
                        insnList.add(InsnNode(Opcodes.RETURN))
                        insnList.add(labelNode)

                        methodNode.instructions.insert(node.next, insnList)
                    }

                    if (node is LdcInsnNode && node.cst == "health") {
                        methodNode.instructions.insertBefore(node.previous.previous.previous.previous, labelNode)
                    }
                }
            }

        findMethod(classNode, renderStatusBars)
            .apply { methodNode ->
                val labelNode = LabelNode()

                for (node in methodNode.instructions) {
                    if (node is LdcInsnNode && node.cst == "food") {
                        val insnList = InsnList()

                        val eventClassName = HungerBarRenderEvent::class.java.name.replace(".", "/")
                        insnList.add(this.getHook("onHungerBarRender"))
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                        insnList.add(JumpInsnNode(Opcodes.IFNE, labelNode))

                        methodNode.instructions.insert(node.next, insnList)
                    }

                    if (node is LdcInsnNode && node.cst == "air") {
                        methodNode.instructions.insertBefore(node.previous.previous.previous.previous.previous.previous, labelNode)
                    }
                }
            }
    }

    private fun transformBossBarHud(classNode: ClassNode) {
        val render = "v1_18_2/net/minecraft/client/gui/hud/BossBarHud#render(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;)V".toIdentifier()

        findMethod(classNode, render)
            .apply(Insert(createList { insnList ->
                val eventClassName = BossBarRenderEvent::class.java.name.replace(".", "/")
                insnList.add(this.getHook("onBossBarRender"))
                val labelNode = LabelNode()
                insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "getCanceled", "()Z"))
                insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                insnList.add(InsnNode(Opcodes.RETURN))
                insnList.add(labelNode)
            }))
    }

    private fun transformKeyboard(classNode: ClassNode) {
        val onKey = "v1_18_2/net/minecraft/client/Keyboard#onKey(JIIII)V".toIdentifier()

        findMethod(classNode, onKey)
            .apply { methodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ILOAD, 3))
                        insnList.add(VarInsnNode(Opcodes.ILOAD, 5))
                        insnList.add(this.getHook("onKey"))
                    }))
            }

        val onChar = "v1_18_2/net/minecraft/client/Keyboard#onChar(JII)V".toIdentifier()
        val currentScreen = "v1_18_2/net/minecraft/client/MinecraftClient#currentScreen".toIdentifier()

        findMethod(classNode, onChar)
            .apply { methodNode ->
                findFieldReferences(methodNode, currentScreen, FieldReferenceType.GET)
                    .nth(0)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ILOAD, 3))
                        insnList.add(this.getHook("onChar"))
                    }))
            }
    }

    private fun transformMinecraftClient(classNode: ClassNode) {
        val init = "v1_18_2/net/minecraft/client/MinecraftClient#<init>(Lv1_18_2/net/minecraft/client/RunArgs;)V".toIdentifier()
        val initRenderer = "v1_18_2/com/mojang/blaze3d/systems/RenderSystem#initRenderer(IZ)V".toIdentifier()

        findMethod(classNode, init, true)
            .apply { methodNode ->
                findMethodCalls(methodNode, initRenderer)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onInitialize"))
                    }))
            }

        val tick = "v1_18_2/net/minecraft/client/MinecraftClient#tick()V".toIdentifier()

        findMethod(classNode, tick)
            .apply(Insert(createList { insnList ->
                insnList.add(this.getHook("onTick"))
            }))

        val disconnect = "v1_18_2/net/minecraft/client/MinecraftClient#disconnect(Lv1_18_2/net/minecraft/client/gui/screen/Screen;)V".toIdentifier()
        findMethod(classNode, disconnect)
            .apply(Insert(createList { insnList: InsnList ->
                insnList.add(this.getHook("onDisconnect"))
            }))
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val render = "v1_18_2/net/minecraft/client/render/GameRenderer#render(FJZ)V".toIdentifier()
        findMethod(classNode, render)
            .apply { methodNode: MethodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, this.getHook("onRender")))
            }

        val getFov = "v1_18_2/net/minecraft/client/render/GameRenderer#getFov(Lv1_18_2/net/minecraft/client/render/Camera;FZ)D".toIdentifier()

        findMethod(classNode, getFov)
            .apply { methodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onGetFOV"))
                    }))
            }
    }

    private fun transformMouse(classNode: ClassNode) {
        val onMouseButton = "v1_18_2/net/minecraft/client/Mouse#onMouseButton(JIII)V".toIdentifier()

        findMethod(classNode, onMouseButton)
            .apply { methodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.ILOAD, 3))
                        insnList.add(VarInsnNode(Opcodes.ILOAD, 4))
                        insnList.add(this.getHook("onMousePress"))
                    }))
            }

        val onCursorPos = "v1_18_2/net/minecraft/client/Mouse#onCursorPos(JDD)V".toIdentifier()

        findMethod(classNode, onCursorPos)
            .apply { methodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onMouseMove"))
                    }))
            }

        val onMouseScroll = "v1_18_2/net/minecraft/client/Mouse#onMouseScroll(JDD)V".toIdentifier()

        findMethod(classNode, onMouseScroll)
            .apply { methodNode ->
                findReturns(methodNode)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(VarInsnNode(Opcodes.DLOAD, 5))
                        insnList.add(this.getHook("onMouseScroll"))
                    }))
            }

        val updateMouse = "v1_18_2/net/minecraft/client/Mouse#updateMouse()V".toIdentifier()
        val smoothCameraEnabled = "v1_18_2/net/minecraft/client/option/GameOptions#smoothCameraEnabled".toIdentifier()

        findMethod(classNode, updateMouse)
            .apply { methodNode ->
                findVarReferences(methodNode, 9, VarReferenceType.STORE)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onGetSensitivity"))
                    }))

                findFieldReferences(methodNode, smoothCameraEnabled, FieldReferenceType.GET)
                    .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onGetUseCinematicCamera"))
                    }))
            }
    }

    private fun transformGl30(classNode: ClassNode) {
        val glUseProgram = "org/lwjgl/opengl/GL30#glBindVertexArray(I)V".toIdentifier()
        findMethod(classNode, glUseProgram)
            .apply(Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ILOAD, 0))
                insnList.add(this.getHook("onBindVertexArray"))
            }))
    }

    private fun transformGl15(classNode: ClassNode) {
        val glUseProgram = "org/lwjgl/opengl/GL15#glBindBuffer(II)V".toIdentifier()
        findMethod(classNode, glUseProgram)
            .apply(Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ILOAD, 0))
                insnList.add(VarInsnNode(Opcodes.ILOAD, 1))
                insnList.add(this.getHook("onBindBuffer"))
            }))
    }

    private fun transformGl20(classNode: ClassNode) {
        val glUseProgram = "org/lwjgl/opengl/GL20#glUseProgram(I)V".toIdentifier()
        findMethod(classNode, glUseProgram)
            .apply(Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ILOAD, 0))
                insnList.add(this.getHook("onUseProgram"))
            }))
    }

    private fun transformWindow(classNode: ClassNode) {
        val setTitle = "v1_18_2/net/minecraft/client/util/Window#setTitle(Ljava/lang/String;)V".toIdentifier()
        findMethod(classNode, setTitle)
            .apply(Insert(createList { insnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(this.getHook("updateTitle"))
                val labelNode = LabelNode()
                insnList.add(JumpInsnNode(Opcodes.IFNE, labelNode))
                insnList.add(InsnNode(Opcodes.RETURN))
                insnList.add(labelNode)
            }))
    }

    private fun transformLightMapTextureManager(classNode: ClassNode) {
        val update = "v1_18_2/net/minecraft/client/render/LightmapTextureManager#update(F)V".toIdentifier()

        findMethod(classNode, update)
            .apply { methodNode ->
                findVarReferences(methodNode, 17, VarReferenceType.STORE)
                    .nth(2)
                    .apply(Applier.InsertBefore(methodNode, createList { insnList ->
                        insnList.add(this.getHook("onGetGamma"))
                    }))
            }
    }

    private fun transformClientPlayerNetworkHandler(classNode: ClassNode) {
        val onCustomPayload = "v1_18_2/net/minecraft/client/network/ClientPlayNetworkHandler#onCustomPayload(Lv1_18_2/net/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V".toIdentifier()
        val onGameJoin = "v1_18_2/net/minecraft/client/network/ClientPlayNetworkHandler#onGameJoin(Lv1_18_2/net/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V".toIdentifier()
        findMethod(classNode, onCustomPayload)
            .apply(Insert(createList { insnList: InsnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(this.getHook("onCustomPayload"))
            }))
        findMethod(classNode, onGameJoin)
            .apply(Insert(this.getHook("onGameJoin")))
    }

}