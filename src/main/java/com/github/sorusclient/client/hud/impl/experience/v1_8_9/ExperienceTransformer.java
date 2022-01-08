package com.github.sorusclient.client.hud.impl.experience.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;

public class ExperienceTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(ExperienceTransformer.class);
    }

    public ExperienceTransformer() {
        this.register("v1_8_9/net/minecraft/client/gui/hud/InGameHud", this::transformInGameHud);
    }

    private void transformInGameHud(ClassNode classNode) {
        Identifier renderExperienceBar = Identifier.parse("v1_8_9/net/minecraft/client/gui/hud/InGameHud#renderExperienceBar(Lv1_8_9/net/minecraft/client/util/Window;I)V");

        this.findMethod(classNode, renderExperienceBar)
                .apply(new Applier.Insert<>(new InsnNode(Opcodes.RETURN)));
    }

}
