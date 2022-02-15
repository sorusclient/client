package com.github.sorusclient.client.module.impl.environmentchanger.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class EnvironmentChangerTransformer : Transformer(), Listener {
    override fun run() {
        GlassLoader.getInstance().registerTransformer(EnvironmentChangerTransformer::class.java)
    }

    init {
        setHookClass(EnvironmentChangerHook::class.java)
        register("v1_8_9/net/minecraft/world/World") { classNode: ClassNode -> transformWorld(classNode) }
        register("v1_8_9/net/minecraft/client/render/WorldRenderer") { classNode: ClassNode ->
            transformWorldRenderer(
                classNode
            )
        }
        register("v1_8_9/net/minecraft/client/render/GameRenderer") { classNode: ClassNode ->
            transformGameRenderer(
                classNode
            )
        }
    }

    private fun transformWorld(classNode: ClassNode) {
        val getSkyAngle = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngle(F)F")
        val getFogColor = Identifier.parse("v1_8_9/net/minecraft/world/World#getFogColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;")
        val getSkyAngleRadians = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngleRadians(F)F")
        val getCloudColor = Identifier.parse("v1_8_9/net/minecraft/world/World#getCloudColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;")
        val method3707 = Identifier.parse("v1_8_9/net/minecraft/world/World#method_3707(F)F")
        val method3631 = Identifier.parse("v1_8_9/net/minecraft/world/World#method_3631(Lv1_8_9/net/minecraft/entity/Entity;F)Lv1_8_9/net/minecraft/util/math/Vec3d;")
        val getRainGradient = Identifier.parse("v1_8_9/net/minecraft/world/World#getRainGradient(F)F")
        val getRainGradient2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F")
        findMethod(classNode, getFogColor)
            .apply { methodNode: MethodNode ->
                findVarReferences(methodNode, 2, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifySkyAngle")))
            }
        findMethod(classNode, getSkyAngleRadians)
            .apply { methodNode: MethodNode ->
                findVarReferences(methodNode, 2, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifySkyAngle")))
            }
        findMethod(classNode, getCloudColor)
            .apply { methodNode: MethodNode ->
                findVarReferences(methodNode, 2, VarReferenceType.STORE)
                    .apply(InsertBefore(methodNode, this.getHook("modifySkyAngle")))
                findMethodCalls(methodNode, getRainGradient)
                    .apply(InsertAfter(methodNode, this.getHook("modifyRainGradient")))
                findMethodCalls(methodNode, getRainGradient2)
                    .apply(InsertAfter(methodNode, this.getHook("modifyRainGradient")))
            }
        findMethod(classNode, method3707)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
        findMethod(classNode, method3631)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
                findMethodCalls(methodNode, getRainGradient)
                    .apply(InsertAfter(methodNode, this.getHook("modifyRainGradient")))
                findMethodCalls(methodNode, getRainGradient2)
                    .apply(InsertAfter(methodNode, this.getHook("modifyRainGradient")))
            }
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val method9891 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9891(FI)V")
        val getSkyAngle2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F")
        val getRainGradient = Identifier.parse("v1_8_9/net/minecraft/world/World#getRainGradient(F)F")
        val getRainGradient2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F")
        findMethod(classNode, method9891)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, getSkyAngle2)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
        for (methodNode in classNode.methods) {
            for (node in methodNode.instructions) {
                if (isMethodCall(node, getRainGradient) || isMethodCall(node, getRainGradient2)) {
                    methodNode.instructions.insert(node, this.getHook("modifyRainGradient"))
                }
            }
        }
    }

    private fun transformGameRenderer(classNode: ClassNode) {
        val method9891 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9891(FI)V")
        val getSkyAngle = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngle(F)F")
        val getSkyAngle2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F")
        val updateFog = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#updateFog(F)V")
        val getRainGradient = Identifier.parse("v1_8_9/net/minecraft/world/World#getRainGradient(F)F")
        val getRainGradient2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F")
        findMethod(classNode, method9891)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, getSkyAngle2)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
        findMethod(classNode, updateFog)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
        for (methodNode in classNode.methods) {
            for (node in methodNode.instructions) {
                if (isMethodCall(node, getRainGradient) || isMethodCall(node, getRainGradient2)) {
                    methodNode.instructions.insert(node, this.getHook("modifyRainGradient"))
                }
            }
        }
    }
}