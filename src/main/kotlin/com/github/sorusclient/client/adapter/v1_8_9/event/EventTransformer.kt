package com.github.sorusclient.client.adapter.v1_8_9.event

import com.github.glassmc.loader.GlassLoader
import com.github.glassmc.loader.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.transform.Applier.Insert
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class EventTransformer : Transformer(), Listener {
    override fun run() {
        GlassLoader.getInstance().registerTransformer(EventTransformer::class.java)
    }

    init {
        setHookClass(EventHook::class.java)
        register("v1_8_9/net/minecraft/client/render/GameRenderer") { classNode: ClassNode ->
            transformGameRenderer(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/MinecraftClient") { classNode: ClassNode ->
            transformMinecraftClient(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/gui/screen/Screen") { classNode: ClassNode -> transformScreen(classNode) }
        register("v1_8_9/net/minecraft/client/gui/hud/InGameHud") { classNode: ClassNode -> transformInGameHud(classNode) }
        register("v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler") { classNode: ClassNode ->
            transformClientPlayerNetworkHandler(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/render/WorldRenderer") { classNode: ClassNode ->
            transformWorldRenderer(
                classNode
            )
        }
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val render = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#render(FJ)V")
        findMethod(classNode, render)
            .apply { methodNode: MethodNode ->
                findReturns(methodNode)
                    .apply(InsertBefore(methodNode, this.getHook("onRender")))
            }
    }

    private fun transformMinecraftClient(classNode: ClassNode) {
        val handleKeyInput = Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient#handleKeyInput()V")
        val connect =
            Identifier.parse("v1_8_9/net/minecraft/client/MinecraftClient#connect(Lv1_8_9/net/minecraft/client/world/ClientWorld;Ljava/lang/String;)V")
        findMethod(classNode, handleKeyInput)
            .apply(Insert(this.getHook("onKey")))
        findMethod(classNode, connect)
            .apply(Insert(createList { insnList: InsnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(VarInsnNode(Opcodes.ALOAD, 2))
                insnList.add(this.getHook("onConnect"))
            }))
    }

    private fun transformScreen(classNode: ClassNode) {
        val handleMouse = Identifier.parse("v1_8_9/net/minecraft/client/gui/screen/Screen#handleMouse()V")
        findMethod(classNode, handleMouse)
            .apply(Insert(this.getHook("onMouse")))
    }

    private fun transformInGameHud(classNode: ClassNode) {
        val render = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#render(F)V")
        val setupHudMatrixMode =
            Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#setupHudMatrixMode()V")

        findMethod(classNode, render)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, setupHudMatrixMode)
                    .apply(InsertAfter(methodNode, this.getHook("onInGameRender")))
            }

        val renderStatusBars =
            Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderStatusBars(Lv1_8_9/net/minecraft/client/util/Window;)V")
        val push = Identifier.parse("v1_8_9/net/minecraft/util/profiler/Profiler#push(Ljava/lang/String;)V")
        val swap = Identifier.parse("v1_8_9/net/minecraft/util/profiler/Profiler#swap(Ljava/lang/String;)V")
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
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
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

        val renderBossBar = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderBossBar()V")
        val framesToLive = Identifier.parse("v1_8_9/net/minecraft/entity/boss/BossBar#framesToLive")
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
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
                        insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                        insnList.add(InsnNode(Opcodes.RETURN))
                        insnList.add(labelNode)
                    }))
            }

        val renderExperienceBar =
            Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderExperienceBar(Lv1_8_9/net/minecraft/client/util/Window;I)V")
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
                    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
                    insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                    insnList.add(InsnNode(Opcodes.RETURN))
                    insnList.add(labelNode)
                }
            })

        val vehicle = Identifier.parse("v1_8_9/net/minecraft/entity/player/PlayerEntity#vehicle")
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
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
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

        val renderHotBar = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderHotbar(Lv1_8_9/net/minecraft/client/util/Window;F)V")
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
                    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
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
                        insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
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
        val renderScoreboardObjective =
            Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderScoreboardObjective(Lv1_8_9/net/minecraft/scoreboard/ScoreboardObjective;Lv1_8_9/net/minecraft/client/util/Window;)V")
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
                    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
                    insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                    insnList.add(InsnNode(Opcodes.RETURN))
                    insnList.add(labelNode)
                }
            })
    }

    private fun transformClientPlayerNetworkHandler(classNode: ClassNode) {
        val onCustomPayload =
            Identifier.parse("v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler#onCustomPayload(Lv1_8_9/net/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;)V")
        val onGameJoin =
            Identifier.parse("v1_8_9/net/minecraft/client/network/ClientPlayNetworkHandler#onGameJoin(Lv1_8_9/net/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V")
        findMethod(classNode, onCustomPayload)
            .apply(Insert(createList { insnList: InsnList ->
                insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
                insnList.add(this.getHook("onCustomPayload"))
            }))
        findMethod(classNode, onGameJoin)
            .apply(Insert(this.getHook("onGameJoin")))
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val method9895 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9895(Lv1_8_9/net/minecraft/util/math/Box;)V")
        findMethod(classNode, method9895)
            .apply(Insert { methodNode: MethodNode ->
                createList { insnList: InsnList ->
                    val eventClassName = BlockOutlineRenderEvent::class.java.name.replace(".", "/")
                    insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
                    insnList.add(this.getHook("onBlockOutlineRender"))
                    val index = methodNode.maxLocals
                    insnList.add(VarInsnNode(Opcodes.ASTORE, index))
                    methodNode.maxLocals++
                    val labelNode = LabelNode()
                    insnList.add(VarInsnNode(Opcodes.ALOAD, index))
                    insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, eventClassName, "isCanceled", "()Z"))
                    insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode))
                    insnList.add(InsnNode(Opcodes.RETURN))
                    insnList.add(labelNode)
                }
            })
    }
}