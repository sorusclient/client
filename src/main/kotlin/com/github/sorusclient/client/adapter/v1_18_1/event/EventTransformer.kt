package com.github.sorusclient.client.adapter.v1_18_1.event

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.transform.Applier.Insert
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

@Suppress("UNUSED")
class EventTransformer : Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(EventTransformer::class.java)
    }

    init {
        setHookClass(EventHook::class.java)
        register("v1_18_1/net/minecraft/client/gui/hud/InGameHud") { classNode: ClassNode -> transformInGameHud(classNode) }
        register("v1_18_1/net/minecraft/client/gui/hud/BossBarHud") { classNode: ClassNode -> transformBossBarHud(classNode) }
    }

    private fun transformInGameHud(classNode: ClassNode) {
        val render = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/InGameHud#render(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;F)V")

        findMethod(classNode, render)
            .apply(Insert(createList { insnList ->
                insnList.add(this.getHook("onInGameRender"))
            }))

        val renderScoreboardSidebar = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/InGameHud#renderScoreboardSidebar(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;Lv1_18_1/net/minecraft/scoreboard/ScoreboardObjective;)V")

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

        val renderHotbar = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/InGameHud#renderHotbar(FLv1_18_1/net/minecraft/client/util/math/MatrixStack;)V")

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

        val renderExperienceBar = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/InGameHud#renderExperienceBar(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;I)V")

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

        val renderHealthBar = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/InGameHud#renderHealthBar(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;Lv1_18_1/net/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V")

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

        val renderStatusBars = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/InGameHud#renderStatusBars(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;)V")

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
        val render = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/BossBarHud#render(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;)V")

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

}