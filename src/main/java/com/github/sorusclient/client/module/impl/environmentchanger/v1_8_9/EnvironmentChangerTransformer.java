package com.github.sorusclient.client.module.impl.environmentchanger.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.tree.*;

public class EnvironmentChangerTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(EnvironmentChangerTransformer.class);
    }

    public EnvironmentChangerTransformer() {
        this.setHookClass(EnvironmentChangerHook.class);
        this.register("v1_8_9/net/minecraft/world/World", this::transformWorld);
        this.register("v1_8_9/net/minecraft/client/render/WorldRenderer", this::transformWorldRenderer);
        this.register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer);
    }

    private void transformWorld(ClassNode classNode) {
        Identifier getSkyAngle = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngle(F)F");
        Identifier getFogColor = Identifier.parse("v1_8_9/net/minecraft/world/World#getFogColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;");
        Identifier getSkyAngleRadians = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngleRadians(F)F");
        Identifier getCloudColor = Identifier.parse("v1_8_9/net/minecraft/world/World#getCloudColor(F)Lv1_8_9/net/minecraft/util/math/Vec3d;");
        Identifier method_3707 = Identifier.parse("v1_8_9/net/minecraft/world/World#method_3707(F)F");
        Identifier method_3631 = Identifier.parse("v1_8_9/net/minecraft/world/World#method_3631(Lv1_8_9/net/minecraft/entity/Entity;F)Lv1_8_9/net/minecraft/util/math/Vec3d;");

        Identifier getRainGradient = Identifier.parse("v1_8_9/net/minecraft/world/World#getRainGradient(F)F");
        Identifier getRainGradient2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F");

        this.findMethod(classNode, getFogColor)
                .apply(methodNode -> this.findVarReferences(methodNode, 2, VarReferenceType.STORE)
                        .apply(new Applier.InsertBefore<>(methodNode, this.getHook("modifySkyAngle"))));

        this.findMethod(classNode, getSkyAngleRadians)
                .apply(methodNode -> this.findVarReferences(methodNode, 2, VarReferenceType.STORE)
                        .apply(new Applier.InsertBefore<>(methodNode, this.getHook("modifySkyAngle"))));

        this.findMethod(classNode, getCloudColor)
                .apply(methodNode -> {
                    this.findVarReferences(methodNode, 2, VarReferenceType.STORE)
                            .apply(new Applier.InsertBefore<>(methodNode, this.getHook("modifySkyAngle")));

                    this.findMethodCalls(methodNode, getRainGradient)
                            .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifyRainGradient")));
                    this.findMethodCalls(methodNode, getRainGradient2)
                            .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifyRainGradient")));
                });

        this.findMethod(classNode, method_3707)
                .apply(methodNode -> this.findMethodCalls(methodNode, getSkyAngle)
                        .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifySkyAngle"))));

        this.findMethod(classNode, method_3631)
                .apply(methodNode -> {
                    this.findMethodCalls(methodNode, getSkyAngle)
                            .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifySkyAngle")));

                    this.findMethodCalls(methodNode, getRainGradient)
                            .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifyRainGradient")));
                    this.findMethodCalls(methodNode, getRainGradient2)
                            .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifyRainGradient")));
                });
    }

    private void transformWorldRenderer(ClassNode classNode) {
        Identifier method_9891 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9891(FI)V");
        Identifier getSkyAngle2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F");

        Identifier getRainGradient = Identifier.parse("v1_8_9/net/minecraft/world/World#getRainGradient(F)F");
        Identifier getRainGradient2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F");

        this.findMethod(classNode, method_9891)
                .apply(methodNode -> this.findMethodCalls(methodNode, getSkyAngle2)
                        .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifySkyAngle"))));

        for (MethodNode methodNode : classNode.methods) {
            for (AbstractInsnNode node : methodNode.instructions) {
                if (this.isMethodCall(node, getRainGradient) || this.isMethodCall(node, getRainGradient2)) {
                    methodNode.instructions.insert(node, this.getHook("modifyRainGradient"));
                }
            }
        }
    }

    private void transformGameRenderer(ClassNode classNode) {
        Identifier method_9891 = Identifier.parse("v1_8_9/net/minecraft/client/render/WorldRenderer#method_9891(FI)V");
        Identifier getSkyAngle = Identifier.parse("v1_8_9/net/minecraft/world/World#getSkyAngle(F)F");
        Identifier getSkyAngle2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getSkyAngle(F)F");
        Identifier updateFog = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#updateFog(F)V");

        Identifier getRainGradient = Identifier.parse("v1_8_9/net/minecraft/world/World#getRainGradient(F)F");
        Identifier getRainGradient2 = Identifier.parse("v1_8_9/net/minecraft/client/world/ClientWorld#getRainGradient(F)F");

        this.findMethod(classNode, method_9891)
                .apply(methodNode -> this.findMethodCalls(methodNode, getSkyAngle2)
                        .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifySkyAngle"))));

        this.findMethod(classNode, updateFog)
                .apply(methodNode -> this.findMethodCalls(methodNode, getSkyAngle)
                        .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifySkyAngle"))));

        for (MethodNode methodNode : classNode.methods) {
            for (AbstractInsnNode node : methodNode.instructions) {
                if (this.isMethodCall(node, getRainGradient) || this.isMethodCall(node, getRainGradient2)) {
                    methodNode.instructions.insert(node, this.getHook("modifyRainGradient"));
                }
            }
        }
    }

}
