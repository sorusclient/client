package com.github.sorusclient.client.feature.impl.environmentchanger.v1_18_2

import com.github.sorusclient.client.toIdentifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

@Suppress("UNUSED")
class EnvironmentChangerTransformer : Transformer() {

    init {
        setHookClass(EnvironmentChangerHook::class.java)
        register("v1_18_2/net/minecraft/world/World", this::transformWorld)
        register("v1_18_2/net/minecraft/client/world/ClientWorld", this::transformClientWorld)
        register("v1_18_2/net/minecraft/client/render/BackgroundRenderer", this::transformBackgroundRenderer)
        register("v1_18_2/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer)
        register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer)
    }

    private fun transformWorld(classNode: ClassNode) {
        val getSkyAngle = "v1_18_2/net/minecraft/world/World#getSkyAngle(F)F".toIdentifier()
        val getSkyAngleRadians = "v1_18_2/net/minecraft/world/World#getSkyAngleRadians(F)F".toIdentifier()

        findMethod(classNode, getSkyAngleRadians)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
    }

    private fun transformClientWorld(classNode: ClassNode) {
        val getSkyColor = "v1_18_2/net/minecraft/client/world/ClientWorld#getSkyColor(Lv1_18_2/net/minecraft/util/math/Vec3d;F)Lv1_18_2/net/minecraft/util/math/Vec3d;".toIdentifier()
        val getSkyAngle = "v1_18_2/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F".toIdentifier()

        findMethod(classNode, getSkyColor)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val method23787 = "v1_18_2/net/minecraft/client/world/ClientWorld#method_23787(F)F".toIdentifier()

        findMethod(classNode, method23787)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val getStarBrightness = "v1_18_2/net/minecraft/client/world/ClientWorld#getStarBrightness(F)F".toIdentifier()

        findMethod(classNode, getStarBrightness)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val getCloudsColor = "v1_18_2/net/minecraft/client/world/ClientWorld#getCloudsColor(F)Lv1_18_2/net/minecraft/util/math/Vec3d;".toIdentifier()

        findMethod(classNode, getCloudsColor)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
    }

    private fun transformBackgroundRenderer(classNode: ClassNode) {
        val render = "v1_18_2/net/minecraft/client/render/BackgroundRenderer#render(Lv1_18_2/net/minecraft/client/render/Camera;FLv1_18_2/net/minecraft/client/world/ClientWorld;IF)V".toIdentifier()

        val getSkyAngle = "v1_18_2/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F".toIdentifier()

        findMethod(classNode, render)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val getSkyAngle = "v1_18_2/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F".toIdentifier()
        val renderSky = "v1_18_2/net/minecraft/client/render/WorldRenderer#renderSky(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;Lv1_18_2/net/minecraft/util/math/Matrix4f;FLjava/lang/Runnable;)V".toIdentifier()
        findMethod(classNode, renderSky)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val render = "v1_18_2/net/minecraft/client/render/WorldRenderer#render(Lv1_18_2/net/minecraft/client/util/math/MatrixStack;FJZLv1_18_2/net/minecraft/client/render/Camera;Lv1_18_2/net/minecraft/client/render/GameRenderer;Lv1_18_2/net/minecraft/client/render/LightmapTextureManager;Lv1_18_2/net/minecraft/util/math/Matrix4f;)V".toIdentifier()
        val getTime = "v1_18_2/net/minecraft/client/world/ClientWorld#getTime()J".toIdentifier()

        findMethod(classNode, render)
            .apply { methodNode ->
                findMethodCalls(methodNode, getTime)
                    .apply(InsertAfter(methodNode, this.getHook("modifyTime")))
            }

        val getRainGradient = "v1_18_2/net/minecraft/client/world/ClientWorld#getRainGradient(F)F".toIdentifier()

        val renderWeather = "v1_18_2/net/minecraft/client/render/WorldRenderer#renderWeather(Lv1_18_2/net/minecraft/client/render/LightmapTextureManager;FDDD)V".toIdentifier()

        findMethod(classNode, renderWeather)
            .apply { methodNode ->
                findMethodCalls(methodNode, getRainGradient)
                    .apply(InsertAfter(methodNode, this.getHook("modifyRainGradient")))
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