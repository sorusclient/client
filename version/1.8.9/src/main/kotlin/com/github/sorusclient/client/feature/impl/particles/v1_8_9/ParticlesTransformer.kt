/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.particles.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.*
import org.objectweb.asm.tree.ClassNode

@Suppress("UNUSED")
class ParticlesTransformer: Transformer() {

    init {
        register("v1_8_9/net/minecraft/client/particle/EmitterParticle", this::transformEmitterParticle)
        register("v1_8_9/net/minecraft/entity/player/PlayerEntity", this::transformPlayerEntity)
        setHookClass(ParticlesHook::class.java)
    }

    private fun transformEmitterParticle(classNode: ClassNode) {
        val tick = "v1_8_9/net/minecraft/client/particle/EmitterParticle#tick()V".toIdentifier()
        classNode.findMethod(tick)
                .apply { methodNode ->
                    methodNode.findValues(16)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyParticleSpawns"))
                            }))
                }
    }

    private fun transformPlayerEntity(classNode: ClassNode) {
        val method3216 = "v1_8_9/net/minecraft/entity/player/PlayerEntity#method_3216(Lv1_8_9/net/minecraft/entity/Entity;)V".toIdentifier()

        classNode.findMethod(method3216)
                .apply { methodNode ->
                    methodNode.findVarReferences(5, VarReferenceType.LOAD)
                            .nth(1)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyCriticalParticles"))
                            }))

                    methodNode.findVarReferences(4, VarReferenceType.LOAD)
                            .nth(2)
                            .apply(Applier.InsertAfter(methodNode, createList { insnList ->
                                insnList.add(this.getHook("modifyEnchantmentParticles"))
                            }))
                }
    }

}