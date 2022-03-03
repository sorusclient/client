package com.github.sorusclient.client.feature.impl.particles.v1_18_1

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
        register("v1_18_1/net/minecraft/client/particle/EmitterParticle", this::transformEmitterParticle)
        register("v1_18_1/net/minecraft/entity/player/PlayerEntity", this::transformPlayerEntity)
        setHookClass(ParticlesHook::class.java)
    }

    private fun transformEmitterParticle(classNode: ClassNode) {
        val tick = Identifier.parse("v1_18_1/net/minecraft/client/particle/EmitterParticle#tick()V")
        findMethod(classNode, tick)
                .apply { methodNode ->
                    findValues(methodNode, 16)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyParticleSpawns"))
                            }))
                }
    }

    private fun transformPlayerEntity(classNode: ClassNode) {
        val attack = Identifier.parse("v1_18_1/net/minecraft/entity/player/PlayerEntity#attack(Lv1_18_1/net/minecraft/entity/Entity;)V")

        findMethod(classNode, attack)
                .apply { methodNode ->
                    findVarReferences(methodNode, 8, VarReferenceType.LOAD)
                            .nth(3)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyCriticalParticles"))
                            }))

                    findVarReferences(methodNode, 3, VarReferenceType.LOAD)
                            .nth(3)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyEnchantmentParticles"))
                            }))
                }
    }

}