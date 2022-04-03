package com.github.sorusclient.client.feature.impl.environmentchanger.v1_8_9

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Applier.InsertBefore
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

@Suppress("UNUSED")
class EnvironmentChangerTransformer : Transformer() {

    init {
        setHookClass(EnvironmentChangerHook::class.java)
        register("v1_8_9/net/minecraft/world/World", this::transformWorld)
        register("v1_8_9/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer)
        register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer)
    }

    private fun transformWorld(classNode: ClassNode) {
        val getSkyAngle = "v1_8_9/net/minecraft/world/World#getSkyAngle(F)F".toIdentifier()
        val getFogColor = "v1_8_9/net/minecraft/world/World#getFogColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;".toIdentifier()
        val getSkyAngleRadians = "v1_8_9/net/minecraft/world/World#getSkyAngleRadians(F)F".toIdentifier()
        val getCloudColor = "v1_8_9/net/minecraft/world/World#getCloudColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;".toIdentifier()
        val method3707 = "v1_8_9/net/minecraft/world/World#method_3707(F)F".toIdentifier()
        val method3631 = "v1_8_9/net/minecraft/world/World#method_3631(Lv1_8_9/net/minecraft/entity/Entity;F)Lv1_8_9/net/minecraft/util/math/Vec3d;".toIdentifier()
        val getRainGradient = "v1_8_9/net/minecraft/world/World#getRainGradient(F)F".toIdentifier()
        val getRainGradient2 = "v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F".toIdentifier()
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
        val method9891 = "v1_8_9/net/minecraft/client/render/WorldRenderer#method_9891(FI)V".toIdentifier()
        val getSkyAngle2 = "v1_8_9/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F".toIdentifier()
        val getRainGradient = "v1_8_9/net/minecraft/world/World#getRainGradient(F)F".toIdentifier()
        val getRainGradient2 = "v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F".toIdentifier()
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
        val method9891 = "v1_8_9/net/minecraft/client/render/WorldRenderer#method_9891(FI)V".toIdentifier()
        val getSkyAngle = "v1_8_9/net/minecraft/world/World#getSkyAngle(F)F".toIdentifier()
        val getSkyAngle2 = "v1_8_9/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F".toIdentifier()
        val updateFog = "v1_8_9/net/minecraft/client/render/GameRenderer#updateFog(F)V".toIdentifier()
        val getRainGradient = "v1_8_9/net/minecraft/world/World#getRainGradient(F)F".toIdentifier()
        val getRainGradient2 = "v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F".toIdentifier()
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