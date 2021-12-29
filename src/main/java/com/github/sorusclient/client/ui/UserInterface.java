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
import com.github.sorusclient.client.module.Module;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.ui.framework.*;
import com.github.sorusclient.client.ui.framework.constraint.*;
import com.github.sorusclient.client.util.Color;
import com.github.sorusclient.client.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserInterface {

    private final AtomicBoolean hudEditScreenOpen = new AtomicBoolean(false);

    private Container mainGui;

    private final AtomicBoolean guiOpened = new AtomicBoolean(false);

    public void initialize() {
        this.initializeUserInterface();

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

    @SuppressWarnings("unchecked")
    public void initializeUserInterface() {
        this.mainGui = (Container) new Container()
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
                                        .setOnClick(state -> state.put("currentTab", tab))
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
                                        .setOnClick(state -> state.put("currentHudTab", "addHud")))
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
                                                                .setY(new Side(Side.NEGATIVE)))
                                                        .setOnDoubleClick(state -> {
                                                            try {
                                                                HUDElement hudElement = possibleElement.getFirst().newInstance();
                                                                double[] screenDimensions = Sorus.getInstance().get(MinecraftAdapter.class).getScreenDimensions();
                                                                hudElement.setPosition(screenDimensions[0] / 2, screenDimensions[1] / 2, screenDimensions);
                                                                hudElement.setScale(1);

                                                                Sorus.getInstance().get(HUDManager.class).add(hudElement);
                                                            } catch (InstantiationException | IllegalAccessException e) {
                                                                e.printStackTrace();
                                                            }
                                                            state.put("currentHudTab", "main");
                                                        }));
                                            }
                                        }))
                                .addStoredState("currentHudTab"))
                        .addChild("moduleEdit", new TabHolder()
                                .setDefaultTab("main")
                                .setStateId("currentModuleTab")
                                .addChild("main", new List(List.GRID)
                                        .setColumns(4)
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .apply(container -> {
                                            java.util.List<Pair<Module, Pair<String, String>>> modules = Sorus.getInstance().get(ModuleManager.class).getModules();
                                            for (Pair<Module, Pair<String, String>> module : modules) {
                                                container.addChild(new Container()
                                                        .setWidth(new Relative(0.225))
                                                        .setHeight(new Copy(0.8))
                                                        .setPadding(new Relative(0.02))
                                                        .setBackgroundCornerRadius(new Relative(0.02))
                                                        .setBackgroundColor(new Absolute(Color.fromRGB(50, 50, 50, 255)))
                                                        .addChild(new Text()
                                                                .setScale(new Relative(0.008))
                                                                .setFontRenderer(new Absolute("minecraft"))
                                                                .setText(new Absolute(module.getSecond().getFirst()))
                                                                .setTextColor(new Absolute(Color.fromRGB(230, 230, 230, 255)))
                                                                .setPadding(new Relative(0.05))
                                                                .setX(new Side(Side.NEGATIVE))
                                                                .setY(new Side(Side.NEGATIVE))
                                                        )
                                                        .addChild(new Text()
                                                                .setScale(new Relative(0.006))
                                                                .setFontRenderer(new Absolute("minecraft"))
                                                                .setText(new Absolute(module.getSecond().getSecond()))
                                                                .setTextColor(new Absolute(Color.fromRGB(190, 190, 190, 255)))
                                                                .setPadding(new Relative(0.05))
                                                                .setX(new Side(Side.NEGATIVE))
                                                                .setY(new Side(Side.NEGATIVE)))
                                                        .setOnDoubleClick(state -> {
                                                            state.put("currentModuleTab", "edit");
                                                            state.put("currentEditingModule", module);
                                                        }));
                                            }
                                        }))
                                .addChild("edit", new Container()
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .setOnInit(state -> {
                                            Container container = state.getFirst();

                                            Pair<Module, Pair<String, String>> module = (Pair<Module, Pair<String, String>>) state.getSecond().get("currentEditingModule");

                                            java.util.List<Pair<Pair<String, Setting<?>>, Pair<String, Object>>> settings = module.getFirst().getSettings();

                                            container
                                                    .clear()
                                                    .addChild(new Text()
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setText(new Absolute(module.getSecond().getFirst()))
                                                            .setX(new Side(Side.NEGATIVE))
                                                            .setY(new Side(Side.NEGATIVE)))
                                                    .addChild(new Container()
                                                            .setX(new Side(Side.POSITIVE))
                                                            .setY(new Side(Side.POSITIVE))
                                                            .setWidth(new Absolute(25))
                                                            .setHeight(new Absolute(25))
                                                            .setBackgroundColor(new Absolute(Color.WHITE))
                                                            .setOnClick(state1 -> state1.put("currentModuleTab", "main")));

                                            container.addChild(new List(List.VERTICAL)
                                                    .setHeight(new Relative(0.6))
                                                    .setY(new Side(Side.NEGATIVE))
                                                    .apply(container1 -> {
                                                        for (Pair<Pair<String, Setting<?>>, Pair<String, Object>> setting : settings) {
                                                            switch (setting.getSecond().getFirst()) {
                                                                case "TOGGLE":
                                                                    container1.addChild(new Container()
                                                                            .setHeight(new Absolute(20))
                                                                            .addChild(new Text()
                                                                                    .setFontRenderer(new Absolute("minecraft"))
                                                                                    .setText(new Absolute(setting.getFirst().getFirst()))
                                                                                    .setX(new Side(Side.NEGATIVE)))
                                                                            .addChild(new Container()
                                                                                    .setX(new Side(Side.NEGATIVE))
                                                                                    .setWidth(new Copy())
                                                                                    .setHeight(new Relative(0.6))
                                                                                    .setPadding(new Relative(0.2, true))
                                                                                    .setBackgroundColor(new Dependent(state1 -> {
                                                                                        boolean toggled = (boolean) state1.get("toggled");
                                                                                        return toggled ? Color.fromRGB(0, 255, 0, 255) : Color.fromRGB(255, 0, 0, 255);
                                                                                    }))
                                                                                    .setOnClick(state1 -> {
                                                                                        boolean toggled = !(boolean) state1.get("toggled");
                                                                                        state1.put("toggled", toggled);
                                                                                        setting.getFirst().getSecond().setValueRaw(toggled);
                                                                                    }))
                                                                            .apply(container2 -> {
                                                                                container2.addStoredState("toggled");
                                                                                container2.getRuntime().setState("toggled", setting.getFirst().getSecond().getValue());
                                                                            }));
                                                                    break;
                                                                case "SLIDER":
                                                                    container1.addChild(new Container()
                                                                            .setHeight(new Absolute(20))
                                                                            .addChild(new Text()
                                                                                    .setFontRenderer(new Absolute("minecraft"))
                                                                                    .setText(new Absolute(setting.getFirst().getFirst()))
                                                                                    .setX(new Side(Side.NEGATIVE)))
                                                                            .addChild(new Container()
                                                                                    .setX(new Side(Side.NEGATIVE))
                                                                                    .setWidth(new Copy(2))
                                                                                    .setHeight(new Relative(0.6))
                                                                                    .setPadding(new Relative(0.2, true))
                                                                                    .setBackgroundColor(new Absolute(Color.WHITE))
                                                                                    .setOnDrag(state1 -> {
                                                                                        Pair<Double, Double> bounds = (Pair<Double, Double>) setting.getSecond().getSecond();
                                                                                        double value = state1.getSecond().getFirst();
                                                                                        state1.getFirst().put("value", value);

                                                                                        Setting<?> actualSetting = setting.getFirst().getSecond();

                                                                                        double valueToSet = (bounds.getSecond() - bounds.getFirst()) * value + bounds.getFirst();

                                                                                        if (actualSetting.getType() == Double.class) {
                                                                                            setting.getFirst().getSecond().setValueRaw(valueToSet);
                                                                                        } else if (actualSetting.getType() == Long.class) {
                                                                                            setting.getFirst().getSecond().setValueRaw((long) valueToSet);
                                                                                        }
                                                                                    })
                                                                                    .addChild(new Container()
                                                                                            .setX(new Dependent(state1 -> new Relative((double) state1.get("value") - 0.5)))
                                                                                            .setWidth(new Absolute(1))
                                                                                            .setBackgroundColor(new Absolute(Color.BLACK))))
                                                                            .apply(container2 -> {
                                                                                container2.addStoredState("value");

                                                                                Pair<Double, Double> bounds = (Pair<Double, Double>) setting.getSecond().getSecond();
                                                                                container2.getRuntime().setState("value", (((Number) setting.getFirst().getSecond().getValue()).doubleValue() - bounds.getFirst()) / (bounds.getSecond() - bounds.getFirst()));
                                                                            }));
                                                                    break;
                                                                case "KEY":
                                                                    container1.addChild(new Container()
                                                                            .setHeight(new Absolute(20))
                                                                            .addChild(new Text()
                                                                                    .setFontRenderer(new Absolute("minecraft"))
                                                                                    .setText(new Absolute(setting.getFirst().getFirst()))
                                                                                    .setX(new Side(Side.NEGATIVE)))
                                                                            .addChild(new Container()
                                                                                    .setX(new Side(Side.NEGATIVE))
                                                                                    .setWidth(new Copy(2))
                                                                                    .setHeight(new Relative(0.6))
                                                                                    .setPadding(new Relative(0.2, true))
                                                                                    .setBackgroundColor(new Absolute(Color.WHITE))
                                                                                    .setOnKey(state1 -> {
                                                                                        state1.getFirst().put("value", state1.getSecond());
                                                                                        setting.getFirst().getSecond().setValueRaw(state1.getSecond());
                                                                                        state1.getFirst().put("selected", false);
                                                                                    })
                                                                                    .addChild(new Text()
                                                                                            .setText(new Dependent(state1 -> {
                                                                                                Key key = (Key) state1.get("value");
                                                                                                return key.toString();
                                                                                            }))
                                                                                            .setFontRenderer(new Absolute("minecraft"))
                                                                                            .setTextColor(new Absolute(Color.BLACK))))
                                                                            .apply(container2 -> {
                                                                                container2.addStoredState("value");

                                                                                container2.getRuntime().setState("value", setting.getFirst().getSecond().getValue());
                                                                            }));
                                                                    break;
                                                                case "CLICK_THROUGH":
                                                                    container1.addChild(new Container()
                                                                            .setHeight(new Absolute(20))
                                                                            .addChild(new Text()
                                                                                    .setFontRenderer(new Absolute("minecraft"))
                                                                                    .setText(new Absolute(setting.getFirst().getFirst()))
                                                                                    .setX(new Side(Side.NEGATIVE)))
                                                                            .addChild(new Container()
                                                                                    .setX(new Side(Side.NEGATIVE))
                                                                                    .setWidth(new Copy(2))
                                                                                    .setHeight(new Relative(0.6))
                                                                                    .setPadding(new Relative(0.2, true))
                                                                                    .setBackgroundColor(new Absolute(Color.WHITE))
                                                                                    .addChild(new Container()
                                                                                            .setX(new Side(Side.NEGATIVE))
                                                                                            .setWidth(new Relative(0.5))
                                                                                            .setOnClick(state1 -> {
                                                                                                int newValue = Math.max(0, (int) state1.get("value") - 1);
                                                                                                state1.put("value", newValue);
                                                                                            }))
                                                                                    .addChild(new Container()
                                                                                            .setX(new Side(Side.POSITIVE))
                                                                                            .setWidth(new Relative(0.5))
                                                                                            .setOnClick(state1 -> {
                                                                                                int valuesLength = 0;
                                                                                                try {
                                                                                                    valuesLength = ((Object[]) setting.getFirst().getSecond().getType().getDeclaredMethod("values").invoke(null)).length - 1;
                                                                                                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                                                                                    e.printStackTrace();
                                                                                                }
                                                                                                int newValue = Math.min(valuesLength, (int) state1.get("value") + 1);
                                                                                                state1.put("value", newValue);
                                                                                            }))
                                                                                    .addChild(new Text()
                                                                                            .setText(new Dependent(state1 -> {
                                                                                                int value = (int) state1.get("value");
                                                                                                try {
                                                                                                    return ((Object[]) setting.getFirst().getSecond().getType().getDeclaredMethod("values").invoke(null))[value].toString();
                                                                                                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                                return null;
                                                                                            }))
                                                                                            .setFontRenderer(new Absolute("minecraft"))
                                                                                            .setTextColor(new Absolute(Color.BLACK)))
                                                                                    .addOnStateUpdate("value", state1 -> {
                                                                                        try {
                                                                                            Object[] values = ((Object[]) setting.getFirst().getSecond().getType().getDeclaredMethod("values").invoke(null));
                                                                                            setting.getFirst().getSecond().setValueRaw(values[(int) state1]);
                                                                                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                                                                            e.printStackTrace();
                                                                                        }
                                                                                    }))
                                                                            .apply(container2 -> {
                                                                                container2.addStoredState("value");

                                                                                container2.getRuntime().setState("value", ((Enum<?>) setting.getFirst().getSecond().getValue()).ordinal());
                                                                            }));
                                                                    break;
                                                                case "COLOR":
                                                                    container1.addChild(new Container()
                                                                            .setHeight(new Absolute(50))
                                                                            .addChild(new Text()
                                                                                    .setFontRenderer(new Absolute("minecraft"))
                                                                                    .setText(new Absolute(setting.getFirst().getFirst()))
                                                                                    .setX(new Side(Side.NEGATIVE)))
                                                                            .addChild(new Container()
                                                                                    .setX(new Side(Side.NEGATIVE))
                                                                                    .setWidth(new Copy())
                                                                                    .setHeight(new Relative(0.6))
                                                                                    .setPadding(new Relative(0.2, true))
                                                                                    .setTopLeftBackgroundColor(new Absolute(Color.WHITE))
                                                                                    .setBottomLeftBackgroundColor(new Absolute(Color.BLACK))
                                                                                    .setBottomRightBackgroundColor(new Absolute(Color.BLACK))
                                                                                    .setTopRightBackgroundColor(new Dependent(state1 -> {
                                                                                        float[] colorData = (float[]) state1.get("value");
                                                                                        int rgb = java.awt.Color.HSBtoRGB(colorData[0], 1, 1);
                                                                                        java.awt.Color javaColor = new java.awt.Color(rgb);
                                                                                        return Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue(), 255);
                                                                                    }))
                                                                                    .setOnDrag(state1 -> {
                                                                                        float[] colorData = (float[]) state1.getFirst().get("value");
                                                                                        colorData[1] = (float) (double) state1.getSecond().getFirst();
                                                                                        colorData[2] = 1 - (float) (double) state1.getSecond().getSecond();
                                                                                    }))
                                                                            .addChild(new Container()
                                                                                    .setX(new Side(Side.NEGATIVE))
                                                                                    .setWidth(new Copy(0.5))
                                                                                    .setHeight(new Relative(0.6))
                                                                                    .setPadding(new Relative(0.2, true))
                                                                                    .setBackgroundColor(new Absolute(Color.WHITE))
                                                                                    .setOnDrag(state1 -> {
                                                                                        float[] colorData = (float[]) state1.getFirst().get("value");
                                                                                        colorData[0] = (float) (double) state1.getSecond().getSecond();
                                                                                    })
                                                                                    .addChild(new Container()
                                                                                            .setY(new Dependent(state1 -> new Relative((double) ((float[]) state1.get("value"))[0] - 0.5)))
                                                                                            .setHeight(new Absolute(1))
                                                                                            .setBackgroundColor(new Absolute(Color.BLACK))))
                                                                            .addChild(new Container()
                                                                                    .setX(new Side(Side.NEGATIVE))
                                                                                    .setWidth(new Copy(0.5))
                                                                                    .setHeight(new Relative(0.6))
                                                                                    .setPadding(new Relative(0.2, true))
                                                                                    .setBackgroundColor(new Absolute(Color.WHITE))
                                                                                    .setOnDrag(state1 -> {
                                                                                        float[] colorData = (float[]) state1.getFirst().get("value");
                                                                                        colorData[3] = (float) (double) state1.getSecond().getSecond();
                                                                                    })
                                                                                    .addChild(new Container()
                                                                                            .setY(new Dependent(state1 -> new Relative((double) ((float[]) state1.get("value"))[3] - 0.5)))
                                                                                            .setHeight(new Absolute(1))
                                                                                            .setBackgroundColor(new Absolute(Color.BLACK))))
                                                                            .apply(container2 -> {
                                                                                container2.addStoredState("value");

                                                                                Color color = (Color) setting.getFirst().getSecond().getValue();
                                                                                java.awt.Color javaColor = new java.awt.Color((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), (int) (color.getAlpha() * 255));

                                                                                float[] colorData = new float[4];
                                                                                java.awt.Color.RGBtoHSB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue(), colorData);
                                                                                colorData[3] = (float) color.getAlpha();

                                                                                container2.getRuntime().setState("value", colorData);
                                                                            })
                                                                            .addOnStateUpdate("value", state1 -> {
                                                                                float[] stateValue = (float[]) state1;

                                                                                int rgb = java.awt.Color.HSBtoRGB(stateValue[0], stateValue[1], stateValue[2]);
                                                                                java.awt.Color javaColor = new java.awt.Color(rgb);

                                                                                setting.getFirst().getSecond().setValueRaw(Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue(), (int) (stateValue[3] * 255)));
                                                                            }));
                                                                    break;
                                                            }
                                                        }
                                                    }));
                                        })
                                )
                                .addStoredState("currentModuleTab")
                                .addStoredState("currentEditingModule"))
                        .setPadding(new Absolute(5)))
                .addStoredState("currentTab")
                .addOnStateUpdate("currentTab", state -> hudEditScreenOpen.set(state.equals("hudEdit")));
    }

    public boolean isHudEditScreenOpen() {
        return this.hudEditScreenOpen.get() && this.guiOpened.get();
    }

}
