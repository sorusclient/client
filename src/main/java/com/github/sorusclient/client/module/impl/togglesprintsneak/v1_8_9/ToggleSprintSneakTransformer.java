package com.github.sorusclient.client.module.impl.togglesprintsneak.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.glassmc.loader.util.Identifier;
import com.github.sorusclient.client.transform.Applier;
import com.github.sorusclient.client.transform.Transformer;
import org.objectweb.asm.tree.ClassNode;

public class ToggleSprintSneakTransformer extends Transformer implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().registerTransformer(ToggleSprintSneakTransformer.class);
    }

    public ToggleSprintSneakTransformer() {
        this.setHookClass(ToggleSprintSneakHook.class);
        this.register("v1_8_9/net/minecraft/client/network/ClientPlayerEntity", this::transformClientPlayerEntity);
        this.register("v1_8_9/net/minecraft/client/input/KeyboardInput", this::transformKeyboardInput);
    }

    private void transformClientPlayerEntity(ClassNode classNode) {
        Identifier tickMovement = Identifier.parse("v1_8_9/net/minecraft/client/network/ClientPlayerEntity#tickMovement()V");
        Identifier isPressed = Identifier.parse("v1_8_9/net/minecraft/client/options/KeyBinding#isPressed()Z");

        this.findMethod(classNode, tickMovement)
                .apply(methodNode -> this.findMethodCalls(methodNode, isPressed)
                        .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifyIsSprintPressed"))));
    }

    private void transformKeyboardInput(ClassNode classNode) {
        Identifier method_1302 = Identifier.parse("v1_8_9/net/minecraft/client/input/KeyboardInput#method_1302()V");
        Identifier isPressed = Identifier.parse("v1_8_9/net/minecraft/client/options/KeyBinding#isPressed()Z");

        this.findMethod(classNode, method_1302)
                .apply(methodNode -> this.findMethodCalls(methodNode, isPressed)
                        .nth(5)
                        .apply(new Applier.InsertAfter<>(methodNode, this.getHook("modifyIsSneakPressed"))));
    }

}
