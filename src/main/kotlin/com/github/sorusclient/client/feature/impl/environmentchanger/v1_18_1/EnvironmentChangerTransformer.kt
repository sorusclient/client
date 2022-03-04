package com.github.sorusclient.client.feature.impl.environmentchanger.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.transform.Applier.InsertAfter
import com.github.sorusclient.client.transform.Transformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class EnvironmentChangerTransformer : Transformer(), Listener {
    override fun run() {
        GlassLoader.getInstance().registerTransformer(EnvironmentChangerTransformer::class.java)
    }

    init {
        setHookClass(EnvironmentChangerHook::class.java)
        register("v1_18_1/net/minecraft/world/World") { classNode: ClassNode -> transformWorld(classNode) }
        register("v1_18_1/net/minecraft/client/world/ClientWorld") { classNode: ClassNode -> transformClientWorld(classNode) }
        register("v1_18_1/net/minecraft/client/render/BackgroundRenderer") { classNode: ClassNode -> transformBackgroundRenderer(classNode) }
        register("v1_18_1/net/minecraft/client/render/WorldRenderer") { classNode: ClassNode ->
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
        val getSkyAngle = Identifier.parse("v1_18_1/net/minecraft/world/World#getSkyAngle(F)F")
        val getSkyAngleRadians = Identifier.parse("v1_18_1/net/minecraft/world/World#getSkyAngleRadians(F)F")

        findMethod(classNode, getSkyAngleRadians)
            .apply { methodNode: MethodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
    }

    private fun transformClientWorld(classNode: ClassNode) {
        val getSkyColor = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getSkyColor(Lv1_18_1/net/minecraft/util/math/Vec3d;F)Lv1_18_1/net/minecraft/util/math/Vec3d;")
        val getSkyAngle = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F")

        findMethod(classNode, getSkyColor)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val method23787 = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#method_23787(F)F")

        findMethod(classNode, method23787)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val getStarBrightness = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getStarBrightness(F)F")

        findMethod(classNode, getStarBrightness)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val getCloudsColor = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getCloudsColor(F)Lv1_18_1/net/minecraft/util/math/Vec3d;")

        findMethod(classNode, getCloudsColor)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
    }

    private fun transformBackgroundRenderer(classNode: ClassNode) {
        val render = Identifier.parse("v1_18_1/net/minecraft/client/render/BackgroundRenderer#render(Lv1_18_1/net/minecraft/client/render/Camera;FLv1_18_1/net/minecraft/client/world/ClientWorld;IF)V")

        val getSkyAngle = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F")

        findMethod(classNode, render)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }
    }

    private fun transformWorldRenderer(classNode: ClassNode) {
        val getSkyAngle = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F")
        val renderSky = Identifier.parse("v1_18_1/net/minecraft/client/render/WorldRenderer#renderSky(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;Lv1_18_1/net/minecraft/util/math/Matrix4f;FLjava/lang/Runnable;)V")
        findMethod(classNode, renderSky)
            .apply { methodNode ->
                findMethodCalls(methodNode, getSkyAngle)
                    .apply(InsertAfter(methodNode, this.getHook("modifySkyAngle")))
            }

        val render = Identifier.parse("v1_18_1/net/minecraft/client/render/WorldRenderer#render(Lv1_18_1/net/minecraft/client/util/math/MatrixStack;FJZLv1_18_1/net/minecraft/client/render/Camera;Lv1_18_1/net/minecraft/client/render/GameRenderer;Lv1_18_1/net/minecraft/client/render/LightmapTextureManager;Lv1_18_1/net/minecraft/util/math/Matrix4f;)V")
        val getTime = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getTime()J")

        findMethod(classNode, render)
            .apply { methodNode ->
                findMethodCalls(methodNode, getTime)
                    .apply(InsertAfter(methodNode, this.getHook("modifyTime")))
            }

        val getRainGradient = Identifier.parse("v1_18_1/net/minecraft/client/world/ClientWorld#getRainGradient(F)F")

        val renderWeather = Identifier.parse("v1_18_1/net/minecraft/client/render/WorldRenderer#renderWeather(Lv1_18_1/net/minecraft/client/render/LightmapTextureManager;FDDD)V")

        findMethod(classNode, renderWeather)
            .apply { methodNode ->
                findMethodCalls(methodNode, getRainGradient)
                    .apply(InsertAfter(methodNode, this.getHook("modifyRainGradient")))
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