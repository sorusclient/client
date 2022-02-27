package com.github.sorusclient.client.adapter.v1_18_1.event

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.transform.Applier.Insert
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Applier.InsertBefore
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
    }

    private fun transformInGameHud(classNode: ClassNode) {
        val render = Identifier.parse("v1_18_1/net/minecraft/client/gui/hud/InGameHud#render(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;F)V")

        findMethod(classNode, render)
            .apply(Insert(createList { insnList ->
                insnList.add(this.getHook("onInGameRender"))
            }))

        findMethod(classNode, render)
            .apply { methodNode ->
                println("Test")
            }
    }

}