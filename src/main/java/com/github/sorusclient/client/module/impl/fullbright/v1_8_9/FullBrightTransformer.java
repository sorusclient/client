package com.github.sorusclient.client.module.impl.fullbright.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.loader.ITransformer;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class FullBrightTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(FullBrightTransformer.class);
    }

    public FullBrightTransformer() {
        this.register("v1_8_9/net/minecraft/client/render/GameRenderer", this::transformGameRenderer);
    }

    private void transformGameRenderer(ClassNode classNode) {
        Identifier updateLightmap = Identifier.parse("v1_8_9/net/minecraft/client/render/GameRenderer#updateLightmap(F)V");

        /*this.findMethod(classNode, updateLightmap)
                .apply(methodNode -> this.findVarReferences(methodNode, 17, VarReferenceType.STORE)
                        .apply(new Applier.InsertBefore<>(methodNode, this.getHook("modifyGamma"))));*/
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(updateLightmap.getMethodName()) && methodNode.desc.equals(updateLightmap.getMethodDesc())) {
                for (AbstractInsnNode node : methodNode.instructions.toArray()) {
                    if (node.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) node).var == 17 && node.getPrevious() instanceof FieldInsnNode) {
                        methodNode.instructions.insertBefore(node, new MethodInsnNode(Opcodes.INVOKESTATIC, FullBrightHook.class.getName().replace(".", "/"), "modifyGamma", "(F)F"));
                    }
                }
            }
        }
    }

}
