package com.github.sorusclient.client.module.impl.particles.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.tree.ClassNode

class ParticlesTransformer: Transformer(), Listener {

    override fun run() {
        GlassLoader.getInstance().registerTransformer(ParticlesTransformer::class.java)
    }

    init {
        register("v1_8_9/net/minecraft/client/particle/EmitterParticle", this::transformEmitterParticle)
        register("v1_8_9/net/minecraft/entity/player/PlayerEntity", this::transformPlayerEntity)
        setHookClass(ParticlesHook::class.java)
    }

    private fun transformEmitterParticle(classNode: ClassNode) {
        val tick = Identifier.parse("v1_8_9/net/minecraft/client/particle/EmitterParticle#tick()V")
        findMethod(classNode, tick)
                .apply { methodNode ->
                    findValues(methodNode, 16)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyParticleSpawns"))
                            }))
                }
    }

    private fun transformPlayerEntity(classNode: ClassNode) {
        val method3216 = Identifier.parse("v1_8_9/net/minecraft/entity/player/PlayerEntity#method_3216(Lv1_8_9/net/minecraft/entity/Entity;)V")

        findMethod(classNode, method3216)
                .apply { methodNode ->
                    findVarReferences(methodNode, 5, VarReferenceType.LOAD)
                            .nth(1)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyCriticalParticles"))
                            }))

                    findVarReferences(methodNode, 4, VarReferenceType.LOAD)
                            .nth(2)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyEnchantmentParticles"))
                            }))
                }
    }

}