package com.github.sorusclient.client.feature.impl.particles.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.tree.ClassNode

@Suppress("UNUSED")
class ParticlesTransformer: Transformer() {

    init {
        register("v1_18_2/net/minecraft/client/particle/EmitterParticle", this::transformEmitterParticle)
        register("v1_18_2/net/minecraft/entity/player/PlayerEntity", this::transformPlayerEntity)
        setHookClass(ParticlesHook::class.java)
    }

    private fun transformEmitterParticle(classNode: ClassNode) {
        val tick = "v1_18_2/net/minecraft/client/particle/EmitterParticle#tick()V".toIdentifier()
        findMethod(classNode, tick)
                .apply { methodNode ->
                    findValues(methodNode, 16)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyParticleSpawns"))
                            }))
                }
    }

    private fun transformPlayerEntity(classNode: ClassNode) {
        val attack = "v1_18_2/net/minecraft/entity/player/PlayerEntity#attack(Lv1_18_2/net/minecraft/entity/Entity;)V".toIdentifier()

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