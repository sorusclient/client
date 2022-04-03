package com.github.sorusclient.client.feature.impl.particles.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.*
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
        classNode.findMethod(tick)
                .apply { methodNode ->
                    methodNode.findValues(16)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyParticleSpawns"))
                            }))
                }
    }

    private fun transformPlayerEntity(classNode: ClassNode) {
        val attack = "v1_18_2/net/minecraft/entity/player/PlayerEntity#attack(Lv1_18_2/net/minecraft/entity/Entity;)V".toIdentifier()

        classNode.findMethod(attack)
                .apply { methodNode ->
                    methodNode.findVarReferences(8, VarReferenceType.LOAD)
                            .nth(3)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyCriticalParticles"))
                            }))

                    methodNode.findVarReferences(3, VarReferenceType.LOAD)
                            .nth(3)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyEnchantmentParticles"))
                            }))
                }
    }

}