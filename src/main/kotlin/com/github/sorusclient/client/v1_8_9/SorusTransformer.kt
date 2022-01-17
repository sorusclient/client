package com.github.sorusclient.client.v1_8_9

import com.github.glassmc.loader.GlassLoader
import com.github.glassmc.loader.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier.Insert
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode

class SorusTransformer : Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(SorusTransformer::class.java)
    }

    init {
        setHookClass(SorusHook::class.java)
        register("v1_8_9/net/minecraft/client/ClientBrandRetriever") { classNode: ClassNode ->
            transformClientBrandRetriever(
                classNode
            )
        }
    }

    private fun transformClientBrandRetriever(classNode: ClassNode) {
        val getClientModName = Identifier.parse("v1_8_9/net/minecraft/client/ClientBrandRetriever#getClientModName()Ljava/lang/String;")
        findMethod(classNode, getClientModName)
            .apply(Insert(createList { insnList: InsnList ->
                insnList.add(this.getHook("getBrand"))
                insnList.add(InsnNode(Opcodes.ARETURN))
            }))
    }

}