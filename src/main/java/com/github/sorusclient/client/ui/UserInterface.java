package com.github.sorusclient.client.ui;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.adapter.ScreenType;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.event.impl.RenderEvent;
import com.github.sorusclient.client.hud.HUDElement;
import com.github.sorusclient.client.hud.HUDManager;
import com.github.sorusclient.client.ui.framework.*;
import com.github.sorusclient.client.ui.framework.constraint.Absolute;
import com.github.sorusclient.client.ui.framework.constraint.Copy;
import com.github.sorusclient.client.ui.framework.constraint.Relative;
import com.github.sorusclient.client.ui.framework.constraint.Side;
import com.github.sorusclient.client.util.Color;
import com.github.sorusclient.client.util.Pair;

import java.util.concurrent.atomic.AtomicBoolean;

public class UserInterface {

    private final AtomicBoolean hudEditScreenOpen = new AtomicBoolean(false);

    private final Container mainGui = (Container) new Container()
            .addChild(new List(List.VERTICAL)
                    .setX(new Side(Side.NEGATIVE))
                    .setWidth(new Relative(0.065))
                    .setBackgroundCornerRadius(new Relative(0.015))
                    .setPadding(new Absolute(5))
                    .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                    .apply(container1 -> {
                        String[] tabs = new String[] {"home", "hudEdit", "moduleEdit"};

                        for (String tab : tabs) {
                            container1.addChild(new Container()
                                    .setHeight(new Copy())
                                    .setPadding(new Relative(0.225))
                                    .setBackgroundCornerRadius(new Relative(0.15))
                                    .setBackgroundColor(new Absolute(Color.fromRGB(50, 50, 50, 255)))
                                    .setOnClick(container2 -> container2.getRuntime().setState("currentTab", tab))
                                    .addChild(new Container()
                                            .setPadding(new Relative(0.225))
                                            .setBackgroundImage(new Absolute(tab + ".png"))
                                            .setBackgroundColor(new Absolute(Color.fromRGB(230, 230, 230, 255)))));
                        }
                    }))
            .addChild(new TabHolder()
                    .setStateId("currentTab")
                    .setDefaultTab("home")
                    .addChild("home", new Container()
                            .setBackgroundColor(new Absolute(Color.fromRGB(255, 0, 0, 255))))
                    .addChild("hudEdit", new TabHolder()
                            .setDefaultTab("main")
                            .setStateId("currentHudTab")
                            .addChild("main", new Container()
                                    .setY(new Relative(0.25))
                                    .setWidth(new Relative(0.2))
                                    .setHeight(new Copy(0.3))
                                    .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                    .setBackgroundCornerRadius(new Relative(0.015))
                                    .setOnClick(container -> container.getRuntime().setState("currentHudTab", "addHud")))
                            .addChild("addHud", new List(List.GRID)
                                    .setColumns(4)
                                    .setWidth(new Relative(0.5))
                                    .setBackgroundCornerRadius(new Relative(0.015))
                                    .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                    .apply(container -> {
                                        java.util.List<Pair<Class<? extends HUDElement>, Pair<String, String>>> possibleElements = Sorus.getInstance().get(HUDManager.class).getPossibleElements();
                                        for (Pair<Class<? extends HUDElement>, Pair<String, String>> possibleElement : possibleElements) {
                                            container.addChild(new Container()
                                                    .setWidth(new Relative(0.225))
                                                    .setHeight(new Copy(0.8))
                                                    .setPadding(new Relative(0.02))
                                                    .setBackgroundCornerRadius(new Relative(0.02))
                                                    .setBackgroundColor(new Absolute(Color.fromRGB(50, 50, 50, 255)))
                                                    .addChild(new Text()
                                                            .setScale(new Relative(0.008))
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setText(new Absolute(possibleElement.getSecond().getFirst()))
                                                            .setTextColor(new Absolute(Color.fromRGB(230, 230, 230, 255)))
                                                            .setPadding(new Relative(0.05))
                                                            .setX(new Side(Side.NEGATIVE))
                                                            .setY(new Side(Side.NEGATIVE))
                                                            )
                                                    .addChild(new Text()
                                                            .setScale(new Relative(0.006))
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setText(new Absolute(possibleElement.getSecond().getSecond()))
                                                            .setTextColor(new Absolute(Color.fromRGB(190, 190, 190, 255)))
                                                            .setPadding(new Relative(0.05))
                                                            .setX(new Side(Side.NEGATIVE))
                                                            .setY(new Side(Side.NEGATIVE))
                                                    ));
                                        }
                                    }))
                            .addStoredState("currentHudTab"))
                    .addChild("moduleEdit", new Container()
                            .setBackgroundColor(new Absolute(Color.fromRGB(0, 0, 255, 255))))
                    .setPadding(new Absolute(5)))
            .addStoredState("currentTab")
            .addOnStateUpdate("currentTab", componentObjectPair -> {
                hudEditScreenOpen.set(componentObjectPair.getSecond().equals("hudEdit"));
            });

    private final AtomicBoolean guiOpened = new AtomicBoolean(false);

    public void initialize() {
        EventManager eventManager = Sorus.getInstance().get(EventManager.class);

        eventManager.register(KeyEvent.class, event -> {
            MinecraftAdapter minecraftAdapter = Sorus.getInstance().get(MinecraftAdapter.class);
            if (!event.isRepeat()) {
                if (event.getKey() == Key.P && event.isPressed() && minecraftAdapter.getOpenScreen() == ScreenType.IN_GAME) {
                    minecraftAdapter.openScreen(ScreenType.DUMMY);
                    guiOpened.set(true);
                } else if (event.getKey() == Key.ESCAPE && event.isPressed() && minecraftAdapter.getOpenScreen() == ScreenType.DUMMY) {
                    minecraftAdapter.openScreen(ScreenType.IN_GAME);
                    guiOpened.set(false);
                }
            }
        });

        eventManager.register(RenderEvent.class, event -> {
            if (guiOpened.get()) {
                Sorus.getInstance().get(ContainerRenderer.class).render(mainGui);
            }
        });
    }

    public boolean isHudEditScreenOpen() {
        return this.hudEditScreenOpen.get() && this.guiOpened.get();
    }

}
