package com.github.sorusclient.client.hud.impl.bossbar.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BossBarTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(BossBarTransformer.class);
    }

    public BossBarTransformer() {
        this.register("v1_8_9/net/minecraft/client/gui/hud/InGameHud", this::transformInGameHud);
    }

    private void transformInGameHud(ClassNode classNode) {
        Identifier renderBossBar = com.github.glassmc.loader.util.Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderBossBar()V");
        Identifier framesToLive = com.github.glassmc.loader.util.Identifier.parse("v1_8_9/net/minecraft/entity/boss/BossBar#framesToLive");

        this.findMethod(classNode, renderBossBar)
                .apply(methodNode -> this.findFieldReferences(methodNode, framesToLive, FieldReferenceType.PUT)
                        .apply(new Applier.InsertAfter<>(methodNode, new InsnNode(Opcodes.RETURN))));
    }

}
