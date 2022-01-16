package com.github.sorusclient.client.ui;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.adapter.ScreenType;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.adapter.event.KeyEvent;
import com.github.sorusclient.client.adapter.event.RenderEvent;
import com.github.sorusclient.client.module.ModuleData;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;
import com.github.sorusclient.client.ui.framework.*;
import com.github.sorusclient.client.ui.framework.constraint.*;
import com.github.sorusclient.client.util.Color;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserInterface {

    private final AtomicBoolean hudEditScreenOpen = new AtomicBoolean(false);

    private Container mainGui;

    private final AtomicBoolean guiOpened = new AtomicBoolean(false);

    public void initialize() {
        this.initializeUserInterface();

        EventManager eventManager = Sorus.getInstance().get(EventManager.class);

        eventManager.register(KeyEvent.class, event -> {
            IAdapter adapter = Sorus.getInstance().get(IAdapter.class);
            if (!event.isRepeat()) {
                if (event.getKey() == Key.P && event.isPressed()/* && adapter.getOpenScreen() == ScreenType.IN_GAME*/) {
                    adapter.openScreen(ScreenType.DUMMY);
                    guiOpened.set(true);
                } else if (event.getKey() == Key.ESCAPE && event.isPressed() && adapter.getOpenScreen() == ScreenType.DUMMY) {
                    adapter.openScreen(ScreenType.IN_GAME);
                    guiOpened.set(false);
                }
            }
        });

        eventManager.register(RenderEvent.class, event -> {
            if (guiOpened.get()) {
                Sorus.getInstance().get(ContainerRenderer.class).render(mainGui);
            }
        });

        eventManager.register(KeyEvent.class, event -> {
            if (event.isPressed() && !event.isRepeat() && event.getKey() == Key.U) {
                this.initializeUserInterface();
            }
        });
    }

    private void addSettingsList(Container container, java.util.List<ConfigurableData> settings) {
        for (ConfigurableData setting : settings) {
            if (setting instanceof ConfigurableData.Toggle) {
                container.addChild(new Container()
                        .setHeight(new Absolute(15))
                        .addChild(new Container()
                                .setWidth(new Relative(0.05))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Text()
                                .setFontRenderer(new Absolute("minecraft"))
                                .setText(new Absolute(setting.getDisplayName()))
                                .setScale(new Relative(0.0025))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Container()
                                .setWidth(new Relative(0.05))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Container()
                                .setX(new Side(Side.NEGATIVE))
                                .setWidth(new Copy(2))
                                .setHeight(new Relative(0.6))
                                .setBackgroundCornerRadius(new Relative(0.01))
                                .setPadding(new Relative(0.2, true))
                                .setBackgroundColor(new Dependent(state1 -> {
                                    boolean toggled = (Boolean) setting.getSetting().getValue();
                                    return toggled ? Color.fromRGB(20, 118, 188, 255) : Color.fromRGB(20, 118, 188, 125);
                                }))
                                .setOnClick(state1 -> {
                                    if (!setting.getSetting().isForcedValue()) {
                                        boolean toggled = !(boolean) state1.get("toggled");
                                        state1.put("toggled", toggled);
                                        setting.getSetting().setValueRaw(toggled);
                                    }
                                })
                                .addChild(new Container()
                                        .setWidth(new Copy())
                                        .setHeight(new Relative(0.8))
                                        .setPadding(new Relative(0.1, true))
                                        .setX(new Dependent(state -> new Side((boolean) state.get("toggled") ? 1 : -1)))
                                        .setBackgroundColor(Color.WHITE)
                                        .setBackgroundCornerRadius(new Relative(0.1))))
                        .apply(container2 -> {
                            container2.addStoredState("toggled");
                            container2.getRuntime().setState("toggled", setting.getSetting().getValue());
                        }));
            } else if (setting instanceof ConfigurableData.Slider) {
                container.addChild(new Container()
                        .setHeight(new Absolute(15))
                        .addChild(new Container()
                                .setWidth(new Relative(0.05))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Text()
                                .setFontRenderer(new Absolute("minecraft"))
                                .setText(new Absolute(setting.getDisplayName()))
                                .setScale(new Relative(0.0025))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Container()
                                .setWidth(new Relative(0.05))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Container()
                                .setX(new Side(Side.NEGATIVE))
                                .setWidth(new Copy(35))
                                .setHeight(new Relative(0.1))
                                .setPadding(new Relative(0.2, true))
                                .setBackgroundColor(new Absolute(Color.fromRGB(255, 255, 255, 155)))
                                .setBackgroundCornerRadius(new Relative(0.05, true))
                                .setOnDrag(state1 -> {
                                    if (!setting.getSetting().isForcedValue()) {
                                        double minimum = ((ConfigurableData.Slider) setting).getMinimum();
                                        double maximum = ((ConfigurableData.Slider) setting).getMaximum();

                                        double value = state1.getSecond().getFirst();
                                        state1.getFirst().put("value", value);

                                        Setting<?> actualSetting = setting.getSetting();

                                        double valueToSet = (maximum - minimum) * value + minimum;

                                        if (actualSetting.getType() == Double.class) {
                                            actualSetting.setValueRaw(valueToSet);
                                        } else if (actualSetting.getType() == Long.class) {
                                            actualSetting.setValueRaw((long) valueToSet);
                                        }
                                    }
                                })
                                .addChild(new Container()
                                        .setX(new Side(Side.NEGATIVE))
                                        .setWidth(new Dependent(state -> {
                                            double minimum = ((ConfigurableData.Slider) setting).getMinimum();
                                            double maximum = ((ConfigurableData.Slider) setting).getMaximum();
                                            return new Relative((((Number) setting.getSetting().getValue()).doubleValue() - minimum) / (maximum - minimum));
                                        }))
                                        .setBackgroundColor(Color.fromRGB(20, 118, 188, 255))
                                        .setBackgroundCornerRadius(new Relative(0.5, true)))
                                .addChild(new Container()
                                        .setWidth(new Copy())
                                        .setHeight(new Relative(2))
                                        .setX(new Dependent(state -> {
                                            double minimum = ((ConfigurableData.Slider) setting).getMinimum();
                                            double maximum = ((ConfigurableData.Slider) setting).getMaximum();
                                            return new Relative((((Number) setting.getSetting().getValue()).doubleValue() - minimum) / (maximum - minimum) - 0.5);
                                        }))
                                        .setY(new Relative(0))
                                        .setBackgroundColor(Color.WHITE)
                                        .setBackgroundCornerRadius(new Relative(1, true))))
                        .apply(container2 -> {
                            container2.addStoredState("value");

                            double minimum = ((ConfigurableData.Slider) setting).getMinimum();
                            double maximum = ((ConfigurableData.Slider) setting).getMaximum();
                            container2.getRuntime().setState("value", (((Number) setting.getSetting().getValue()).doubleValue() - minimum) / (maximum - minimum));
                        }));
            } else if (setting instanceof ConfigurableData.KeyBind) {
                container.addChild(new Container()
                        .setHeight(new Absolute(20))
                        .addChild(new Text()
                                .setFontRenderer(new Absolute("minecraft"))
                                .setText(new Absolute(setting.getDisplayName()))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Container()
                                .setX(new Side(Side.NEGATIVE))
                                .setWidth(new Copy(2))
                                .setHeight(new Relative(0.6))
                                .setPadding(new Relative(0.2, true))
                                .setBackgroundColor(new Absolute(Color.WHITE))
                                .setOnKey(state1 -> {
                                    state1.getFirst().put("value", state1.getSecond());
                                    setting.getSetting().setValueRaw(state1.getSecond());
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

                            container2.getRuntime().setState("value", setting.getSetting().getValue());
                        }));
            } else if (setting instanceof ConfigurableData.ClickThrough) {
                container.addChild(new Container()
                        .setHeight(new Absolute(20))
                        .addChild(new Container()
                                .setWidth(new Relative(0.05))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Text()
                                .setFontRenderer(new Absolute("minecraft"))
                                .setText(new Absolute(setting.getDisplayName()))
                                .setScale(new Relative(0.0025))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Container()
                                .setWidth(new Relative(0.05))
                                .setX(new Side(Side.NEGATIVE)))
                        .addChild(new Container()
                                .setX(new Side(Side.NEGATIVE))
                                .setWidth(new Copy(5))
                                .setHeight(new Relative(0.6))
                                .setPadding(new Relative(0.2, true))
                                .setBackgroundColor(new Absolute(Color.fromRGB(20, 118, 188, 255)))
                                .addChild(new Container()
                                        .setX(new Side(Side.NEGATIVE))
                                        .setWidth(new Copy())
                                        .setHeight(new Relative(0.6))
                                        .setPadding(new Relative(0.15, true))
                                        .setBackgroundImage(new Absolute("arrow_left.png"))
                                        .setOnClick(state1 -> {
                                            int newValue = Math.max(0, (int) state1.get("value") - 1);
                                            state1.put("value", newValue);
                                        }))
                                .addChild(new Container()
                                        .setX(new Side(Side.POSITIVE))
                                        .setWidth(new Copy())
                                        .setHeight(new Relative(0.6))
                                        .setPadding(new Relative(0.15, true))
                                        .setBackgroundImage(new Absolute("arrow_right.png"))
                                        .setOnClick(state1 -> {
                                            int valuesLength = 0;
                                            try {
                                                valuesLength = ((Object[]) setting.getSetting().getType().getDeclaredMethod("values").invoke(null)).length;
                                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                                e.printStackTrace();
                                            }
                                            if ((int) state1.get("value") + 1 >= valuesLength) {
                                                state1.put("value", 0);
                                            } else {
                                                state1.put("value", (int) state1.get("value") + 1);
                                            }
                                        }))
                                .addChild(new Text()
                                        .setText(new Dependent(state1 -> setting.getSetting().getValue().toString()))
                                        .setFontRenderer(new Absolute("minecraft"))
                                        .setScale(new Relative(0.01))
                                        .setTextColor(new Absolute(Color.WHITE)))
                                .addOnStateUpdate("value", state1 -> {
                                    try {
                                        Object[] values = ((Object[]) setting.getSetting().getType().getDeclaredMethod("values").invoke(null));

                                        Setting<?> setting1 = setting.getSetting();
                                        if (setting1.isForcedValue()) {
                                            int index = (int) state1.get("value");
                                            while (index != -1) {
                                                if (setting1.getForcedValues().contains(values[index])) {
                                                    setting1.setValueRaw(values[index]);
                                                    state1.put("value", index);
                                                    index = -1;
                                                } else {
                                                    index++;
                                                    if (index >= values.length) {
                                                        index = 0;
                                                    }
                                                }
                                            }
                                        } else {
                                            setting1.setValueRaw(values[(int) state1.get("value")]);
                                        }
                                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                }))
                        .apply(container2 -> {
                            container2.addStoredState("value");

                            container2.getRuntime().setState("value", ((Enum<?>) setting.getSetting().getValue()).ordinal());
                        }));
            } else if (setting instanceof ConfigurableData.ColorPicker) {
                container.addChild(new Container()
                        .setHeight(new Absolute(50))
                        .addChild(new Text()
                                .setFontRenderer(new Absolute("minecraft"))
                                .setText(new Absolute(setting.getDisplayName()))
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
                                    float[] colorDataNew = new float[] {colorData[0], (float) (double) state1.getSecond().getFirst(), 1 - (float) (double) state1.getSecond().getSecond(), colorData[3]};
                                    state1.getFirst().put("value", colorDataNew);
                                }))
                        .addChild(new Container()
                                .setX(new Side(Side.NEGATIVE))
                                .setWidth(new Copy(0.5))
                                .setHeight(new Relative(0.6))
                                .setPadding(new Relative(0.2, true))
                                .setBackgroundColor(new Absolute(Color.WHITE))
                                .setOnDrag(state1 -> {
                                    float[] colorData = (float[]) state1.getFirst().get("value");
                                    float[] colorDataNew = new float[] {(float) (double) state1.getSecond().getSecond(), colorData[1], colorData[2], colorData[3]};
                                    state1.getFirst().put("value", colorDataNew);
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
                                    float[] colorDataNew = new float[] {colorData[0], colorData[1], colorData[2], (float) (double) state1.getSecond().getSecond()};
                                    state1.getFirst().put("value", colorDataNew);
                                })
                                .addChild(new Container()
                                        .setY(new Dependent(state1 -> new Relative((double) ((float[]) state1.get("value"))[3] - 0.5)))
                                        .setHeight(new Absolute(1))
                                        .setBackgroundColor(new Absolute(Color.BLACK))))
                        .apply(container2 -> {
                            container2.addStoredState("value");

                            Color color = (Color) setting.getSetting().getValue();
                            java.awt.Color javaColor = new java.awt.Color((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), (int) (color.getAlpha() * 255));

                            float[] colorData = new float[4];
                            java.awt.Color.RGBtoHSB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue(), colorData);
                            colorData[3] = (float) color.getAlpha();

                            container2.getRuntime().setState("value", colorData);
                        })
                        .addOnStateUpdate("value", state1 -> {
                            float[] stateValue = (float[]) state1.get("value");

                            int rgb = java.awt.Color.HSBtoRGB(stateValue[0], stateValue[1], stateValue[2]);
                            java.awt.Color javaColor = new java.awt.Color(rgb);

                            setting.getSetting().setValueRaw(Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue(), (int) (stateValue[3] * 255)));
                        }));
            }
        }
    }

    public void initializeUserInterface() {
        /*this.mainGui = (Container) new Container()
                .addChild(new List(List.VERTICAL)
                        .setX(new Side(Side.NEGATIVE))
                        .setWidth(new Relative(0.065))
                        .setBackgroundCornerRadius(new Relative(0.015))
                        .setPadding(new Absolute(5))
                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                        .apply(container1 -> {
                            String[] tabs = new String[] {"home", "hudEdit", "moduleEdit", "profileEdit", "pluginEdit"};

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
                                        .setOnClick(state -> state.put("currentHudTab", "addHud"))
                                        .setOnUpdate(state -> {
                                            HUDElement hud = Sorus.getInstance().get(HUDManager.class).hudToOpenSettings;
                                            if (hud != null) {
                                                state.put("currentHudTab", "editHud");
                                                state.put("currentEditingHud", hud);

                                                Sorus.getInstance().get(HUDManager.class).hudToOpenSettings = null;
                                            }
                                        }))
                                .addChild("addHud", new List(List.GRID)
                                        .setColumns(4)
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .apply(container -> {
                                            java.util.List<HUDData> possibleHuds = Sorus.getInstance().get(HUDManager.class).getPossibleHuds();
                                            for (HUDData possibleHud : possibleHuds) {
                                                container.addChild(new Container()
                                                        .setWidth(new Relative(0.225))
                                                        .setHeight(new Copy(0.8))
                                                        .setPadding(new Relative(0.02))
                                                        .setBackgroundCornerRadius(new Relative(0.02))
                                                        .setBackgroundColor(new Absolute(Color.fromRGB(50, 50, 50, 255)))
                                                        .addChild(new Text()
                                                                .setScale(new Relative(0.008))
                                                                .setFontRenderer(new Absolute("minecraft"))
                                                                .setText(new Absolute(possibleHud.getName()))
                                                                .setTextColor(new Absolute(Color.fromRGB(230, 230, 230, 255)))
                                                                .setPadding(new Relative(0.05))
                                                                .setX(new Side(Side.NEGATIVE))
                                                                .setY(new Side(Side.NEGATIVE))
                                                        )
                                                        .addChild(new Text()
                                                                .setScale(new Relative(0.006))
                                                                .setFontRenderer(new Absolute("minecraft"))
                                                                .setText(new Absolute(possibleHud.getDescription()))
                                                                .setTextColor(new Absolute(Color.fromRGB(190, 190, 190, 255)))
                                                                .setPadding(new Relative(0.05))
                                                                .setX(new Side(Side.NEGATIVE))
                                                                .setY(new Side(Side.NEGATIVE)))
                                                        .setOnDoubleClick(state -> {
                                                            try {
                                                                HUDElement hudElement = possibleHud.getHudClass().newInstance();
                                                                double[] screenDimensions = Sorus.getInstance().get(IAdapter.class).getScreenDimensions();
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
                                .addChild("editHud", new Container()
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .setOnInit(state -> {
                                            Container container = state.getFirst();


                                            HUDElement hud = (HUDElement) state.getSecond().get("currentEditingHud");
                                            HUDData hudData = null;
                                            for (HUDData hudData1 : Sorus.getInstance().get(HUDManager.class).getPossibleHuds()) {
                                                if (hudData1.getHudClass().equals(hud.getClass())) {
                                                    hudData = hudData1;
                                                }
                                            }

                                            if (hudData == null) return;

                                            java.util.List<SettingConfigurableData> settings = new ArrayList<>();
                                            hud.addSettings(settings);

                                            container
                                                    .clear()
                                                    .addChild(new Text()
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setText(new Absolute(hudData.getName()))
                                                            .setX(new Side(Side.NEGATIVE))
                                                            .setY(new Side(Side.NEGATIVE)))
                                                    .addChild(new Container()
                                                            .setX(new Side(Side.POSITIVE))
                                                            .setY(new Side(Side.POSITIVE))
                                                            .setWidth(new Absolute(25))
                                                            .setHeight(new Absolute(25))
                                                            .setBackgroundColor(new Absolute(Color.WHITE))
                                                            .setOnClick(state1 -> state1.put("currentHudTab", "main")));

                                            container.addChild(new List(List.VERTICAL)
                                                    .setHeight(new Relative(0.6))
                                                    .setY(new Side(Side.NEGATIVE))
                                                    .apply(container1 -> this.addSettingsList(container1, settings)));
                                        })
                                )
                                .addStoredState("currentHudTab")
                                .addStoredState("currentEditingHud"))
                        .addChild("moduleEdit", new TabHolder()
                                .setDefaultTab("main")
                                .setStateId("currentModuleTab")
                                .addChild("main", new List(List.GRID)
                                        .setColumns(4)
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .apply(container -> {
                                            java.util.List<ModuleData> modules = Sorus.getInstance().get(ModuleManager.class).getModules();
                                            for (ModuleData moduleData : modules) {
                                                container.addChild(new Container()
                                                        .setWidth(new Relative(0.225))
                                                        .setHeight(new Copy(0.8))
                                                        .setPadding(new Relative(0.02))
                                                        .setBackgroundCornerRadius(new Relative(0.02))
                                                        .setBackgroundColor(new Absolute(Color.fromRGB(50, 50, 50, 255)))
                                                        .addChild(new Text()
                                                                .setScale(new Relative(0.008))
                                                                .setFontRenderer(new Absolute("minecraft"))
                                                                .setText(new Absolute(moduleData.getName()))
                                                                .setTextColor(new Absolute(Color.fromRGB(230, 230, 230, 255)))
                                                                .setPadding(new Relative(0.05))
                                                                .setX(new Side(Side.NEGATIVE))
                                                                .setY(new Side(Side.NEGATIVE))
                                                        )
                                                        .addChild(new Text()
                                                                .setScale(new Relative(0.006))
                                                                .setFontRenderer(new Absolute("minecraft"))
                                                                .setText(new Absolute(moduleData.getDescription()))
                                                                .setTextColor(new Absolute(Color.fromRGB(190, 190, 190, 255)))
                                                                .setPadding(new Relative(0.05))
                                                                .setX(new Side(Side.NEGATIVE))
                                                                .setY(new Side(Side.NEGATIVE)))
                                                        .setOnDoubleClick(state -> {
                                                            state.put("currentModuleTab", "edit");
                                                            state.put("currentEditingModule", moduleData);
                                                        }));
                                            }
                                        }))
                                .addChild("edit", new Container()
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .setOnInit(state -> {
                                            Container container = state.getFirst();

                                            ModuleData moduleData = (ModuleData) state.getSecond().get("currentEditingModule");

                                            java.util.List<SettingConfigurableData> settings = new ArrayList<>();

                                            moduleData.getModule().addSettings(settings);

                                            container
                                                    .clear()
                                                    .addChild(new Text()
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setText(new Absolute(moduleData.getName()))
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
                                                    .apply(container1 -> this.addSettingsList(container1, settings)));
                                        })
                                )
                                .addStoredState("currentModuleTab")
                                .addStoredState("currentEditingModule"))
                        .addChild("profileEdit", new Container()
                                .addChild(new Container()
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .addChild(new List(List.VERTICAL)
                                                .setY(new Side(Side.NEGATIVE))
                                                .setHeight(new Relative(0.6))
                                                .setOnInit(state -> {
                                                    state.getFirst().clear();

                                                    java.util.List<Profile> profiles = Sorus.getInstance().get(SettingManager.class).getAllProfiles();

                                                    for (Profile profile : profiles) {
                                                        state.getFirst().addChild(new Container()
                                                                .setHeight(new Absolute(30))
                                                                .setBackgroundColor(new Dependent(state1 -> Sorus.getInstance().get(SettingManager.class).getCurrentProfile().equals(profile) ? Color.fromRGB(0, 255, 0, 255) : Color.WHITE))
                                                                .addChild(new Text()
                                                                        .setFontRenderer(new Absolute("minecraft"))
                                                                        .setTextColor(new Absolute(Color.BLACK))
                                                                        .setText(new Absolute(profile.getId())))
                                                                .addChild(new Container()
                                                                        .setX(new Side(Side.POSITIVE))
                                                                        .setWidth(new Copy())
                                                                        .setHeight(new Relative(0.5))
                                                                        .setBackgroundColor(new Absolute(Color.fromRGB(255, 0, 0, 255)))
                                                                        .setOnClick(state1 -> {
                                                                            Sorus.getInstance().get(SettingManager.class).delete(profile);
                                                                            state1.put("refreshProfiles", true);
                                                                        }))
                                                                .setOnClick(state1 -> Sorus.getInstance().get(SettingManager.class).load(profile)));
                                                    }
                                                })
                                                .addOnStateUpdate("refreshProfiles", state -> {
                                                    if ((boolean) state.get("refreshProfiles")) {
                                                        state.put("refreshProfiles", false);
                                                        state.put("hasInit", false);
                                                    }
                                                })
                                        )
                                        .addChild(new Container()
                                                .setX(new Side(Side.POSITIVE))
                                                .setY(new Side(Side.POSITIVE))
                                                .setWidth(new Absolute(25))
                                                .setHeight(new Absolute(25))
                                                .setBackgroundColor(new Absolute(Color.WHITE))
                                                .setOnClick(state -> {
                                                    Sorus.getInstance().get(SettingManager.class).createNewProfile();
                                                    state.put("refreshProfiles", true);
                                                }))
                                        .setOnInit(state -> {
                                            state.getFirst().addStoredState("refreshProfiles");
                                            state.getSecond().put("refreshProfiles", false);
                                        })))
                        .addChild("pluginEdit", new Container()
                                .addChild(new Container()
                                        .setWidth(new Relative(0.5))
                                        .setBackgroundCornerRadius(new Relative(0.015))
                                        .setBackgroundColor(new Absolute(Color.fromRGB(30, 30, 30, 255)))
                                        .addChild(new List(List.VERTICAL)
                                                .setY(new Side(Side.NEGATIVE))
                                                .setHeight(new Relative(0.6))
                                                .setOnInit(state -> {
                                                    state.getFirst().clear();

                                                    java.util.List<Plugin> plugins = Sorus.getInstance().get(PluginManager.class).getPlugins();

                                                    for (Plugin plugin : plugins) {
                                                        state.getFirst().addChild(new Container()
                                                                .setHeight(new Absolute(30))
                                                                .setBackgroundColor(new Absolute(Color.WHITE))
                                                                .addChild(new Text()
                                                                        .setFontRenderer(new Absolute("minecraft"))
                                                                        .setTextColor(new Absolute(Color.BLACK))
                                                                        .setText(new Absolute(plugin.getName())))
                                                                .addChild(new Container()
                                                                        .setX(new Side(Side.POSITIVE))
                                                                        .setWidth(new Copy())
                                                                        .setHeight(new Relative(0.5))
                                                                        .setBackgroundColor(new Absolute(Color.fromRGB(255, 0, 0, 255)))
                                                                        .setOnClick(state1 -> {
                                                                            Sorus.getInstance().get(PluginManager.class).remove(plugin);
                                                                            state1.put("refreshPlugins", true);
                                                                        })));
                                                    }
                                                })
                                                .addOnStateUpdate("refreshPlugins", state -> {
                                                    if ((boolean) state.get("refreshPlugins")) {
                                                        state.put("refreshPlugins", false);
                                                        state.put("hasInit", false);
                                                    }
                                                })
                                        )
                                        .setOnInit(state -> {
                                            state.getFirst().addStoredState("refreshPlugins");
                                            state.getSecond().put("refreshPlugins", false);
                                        })))
                        .setPadding(new Absolute(5)))
                .addStoredState("currentTab")
                .addOnStateUpdate("currentTab", state -> hudEditScreenOpen.set(state.get("currentTab").equals("hudEdit")));*/

        this.mainGui = (Container) new TabHolder()
                .setStateId("currentTab")
                .apply(container -> {
                    String[] tabs = new String[] {"home", "hudEdit", "moduleEdit", "pluginEdit", "profileEdit"};

                    TabHolder tabHolder = (TabHolder) container;
                    for (String tab : tabs) {
                        Container container1 = new Container();
                        this.addNavBar(container1, tabs);

                        if (tab.equals("moduleEdit")) {
                            this.addModulesScreen(container1);
                        }

                        tabHolder.addChild(tab, container1);
                    }
                })
                /*.addChild(new Container()
                        .setY(new Side(Side.NEGATIVE))
                        .setWidth(new Relative(0.55))
                        .setPadding(new Relative(0.01))
                        .setBackgroundCornerRadius(new Relative(0.0075))
                        .setBackgroundColor(Color.fromRGB(26, 26, 26, 230)))
                .addChild(new Container()
                        .setX(new Side(Side.NEGATIVE))
                        .setY(new Side(Side.POSITIVE))
                        .setHeight(new Relative(0.09))
                        .setPadding(new Relative(0.01))
                        .setBackgroundCornerRadius(new Relative(0.0075))
                        .setBackgroundColor(Color.fromRGB(26, 26, 26, 230)))
                .addChild(new Container()
                        .setX(new Side(Side.NEGATIVE))
                        .setY(new Side(Side.NEGATIVE))
                        .setPadding(new Relative(0.01))
                        .setBackgroundCornerRadius(new Relative(0.0075))
                        .setBackgroundColor(Color.fromRGB(26, 26, 26, 230)))
                .addChild(new Container()
                        .setX(new Side(Side.POSITIVE))
                        .setY(new Side(Side.POSITIVE))
                        .setHeight(new Relative(0.4))
                        .setPadding(new Relative(0.01))
                        .setBackgroundCornerRadius(new Relative(0.0075))
                        .setBackgroundColor(Color.fromRGB(26, 26, 26, 230)))
                .addChild(new Container()
                        .setX(new Side(Side.POSITIVE))
                        .setY(new Side(Side.NEGATIVE))
                        .setPadding(new Relative(0.01))
                        .setBackgroundCornerRadius(new Relative(0.0075))
                        .setBackgroundColor(Color.fromRGB(26, 26, 26, 230)))*/
                .setOnInit(state -> {
                    state.getFirst().addStoredState("currentTab");
                    state.getSecond().put("currentTab", "home");
                })
                .addOnStateUpdate("currentTab", state -> hudEditScreenOpen.set(state.get("currentTab").equals("hudEdit")));
    }

    private void addNavBar(Container container, String[] tabs) {
        container.addChild(new Container()
                .setY(new Side(Side.POSITIVE))
                .setWidth(new Relative(0.53))
                .setHeight(new Relative(0.09))
                .setPadding(new Relative(0.01))
                .setBackgroundCornerRadius(new Relative(0.0075))
                .setBackgroundColor(Color.fromRGB(26, 26, 26, 230))
                .addChild(new Container()
                        .setX(new Side(Side.NEGATIVE))
                        .setWidth(new Copy())
                        .setHeight(new Relative(0.7))
                        .setPadding(new Relative(0.02))
                        .setBackgroundImage(new Absolute("sorus.png")))
                .addChild(new List(List.HORIZONTAL)
                        .setX(new Side(Side.ZERO))
                        .setY(new Side(Side.ZERO))
                        .setHeight(new Relative(0.7))
                        .apply(list -> {
                            for (String tab : tabs) {
                                list.addChild(new Container()
                                                .setWidth(new Copy())
                                                .setPadding(new Relative(0.01))
                                                .setBackgroundCornerRadius(new Relative(0.0075))
                                                .setOnClick(state -> state.put("currentTab", tab))
                                                .setBackgroundColor(new Dependent(state -> {
                                                    if (state.get("currentTab").equals(tab)) {
                                                        return Color.fromRGB(20, 118, 188, 255);
                                                    } else {
                                                        return Color.fromRGB(24, 24, 24, 255);
                                                    }
                                                }))
                                                .addChild(new Container()
                                                        .setWidth(new Relative(0.5))
                                                        .setHeight(new Relative(0.5))
                                                        .setBackgroundImage(new Absolute(tab + ".png"))))
                                        .addChild(new Container()
                                                .setWidth(new Copy(0.1)));
                            }
                        })));
    }

    private void addModulesScreen(Container container) {
        container.addChild(new TabHolder()
                        .addChild("main", new Container()
                            .setY(new Side(Side.NEGATIVE))
                            .setWidth(new Relative(0.53))
                            .setPadding(new Relative(0.01))
                            .setBackgroundCornerRadius(new Relative(0.0075))
                            .setBackgroundColor(Color.fromRGB(26, 26, 26, 230))
                            .addChild(new Container()
                                    .setY(new Side(Side.NEGATIVE))
                                    .setHeight(new Copy(0.04))
                                    .setPadding(new Relative(0.005))
                                    .addChild(new Text()
                                            .setFontRenderer(new Absolute("minecraft"))
                                            .setPadding(new Relative(0.01))
                                            .setText(new Absolute("Modules"))
                                            .setScale(new Relative(0.003))
                                            .setX(new Side(Side.NEGATIVE))))
                            .addChild(new List(List.VERTICAL)
                                    .apply(container1 -> {
                                        for (ModuleData module : Sorus.getInstance().get(ModuleManager.class).getModules()) {
                                            container1.addChild(new Container()
                                                    .setHeight(new Copy(0.1))
                                                    .setPadding(new Relative(0.01))
                                                    .setBackgroundCornerRadius(new Relative(0.01))
                                                    .setBackgroundColor(Color.fromRGB(24, 24, 24, 255))
                                                    .addChild(new Container()
                                                            .setX(new Side(Side.NEGATIVE))
                                                            .setWidth(new Copy())
                                                            .setHeight(new Relative(0.6))
                                                            .setPadding(new Relative(0.2, true))
                                                            .setBackgroundCornerRadius(new Relative(0.01))
                                                            .setBackgroundColor(Color.fromRGB(255, 255, 255, 200)))
                                                    .addChild(new Text()
                                                            .setText(new Absolute(module.getName()))
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setScale(new Relative(0.003))
                                                            .setPadding(new Relative(0.175, true))
                                                            .setX(new Side(Side.NEGATIVE))
                                                            .setY(new Side(Side.NEGATIVE)))
                                                    .addChild(new Text()
                                                            .setText(new Absolute(module.getDescription()))
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setScale(new Relative(0.003))
                                                            .setTextColor(new Absolute(Color.fromRGB(255, 255, 255, 80)))
                                                            .setPadding(new Relative(0.175, true))
                                                            .setX(new Side(Side.NEGATIVE))
                                                            .setY(new Side(Side.POSITIVE)))
                                                    .addChild(new Container()
                                                            .setBackgroundImage(new Absolute("gear.png"))
                                                            .setX(new Side(Side.POSITIVE))
                                                            .setY(new Side(Side.ZERO))
                                                            .setWidth(new Copy())
                                                            .setHeight(new Relative(0.4))
                                                            .setPadding(new Relative(0.3, true))
                                                            .setBackgroundColor(Color.fromRGB(255, 255, 255, 75))
                                                            .setOnClick(state -> {
                                                                state.put("moduleScreen", "edit");
                                                                state.put("currentEditingModule", module);
                                                            })));
                                        }
                                    })))
                        .addChild("edit", new Container()
                                .setY(new Side(Side.NEGATIVE))
                                .setWidth(new Relative(0.53))
                                .setPadding(new Relative(0.01))
                                .setBackgroundCornerRadius(new Relative(0.0075))
                                .setBackgroundColor(Color.fromRGB(26, 26, 26, 230))
                                .setOnInit(state -> {
                                    Container container1 = state.getFirst();

                                    ModuleData moduleData = (ModuleData) state.getSecond().get("currentEditingModule");

                                    java.util.List<ConfigurableData> settings = new ArrayList<>();

                                    moduleData.getModule().addSettings(settings);

                                    container1
                                            .clear()
                                            .addChild(new Container()
                                                    .setY(new Side(Side.NEGATIVE))
                                                    .setHeight(new Copy(0.04))
                                                    .setPadding(new Relative(0.005))
                                                    .addChild(new Text()
                                                            .setFontRenderer(new Absolute("minecraft"))
                                                            .setPadding(new Relative(0.01))
                                                            .setText(new Absolute("Modules"))
                                                            .setScale(new Relative(0.003))
                                                            .setX(new Side(Side.NEGATIVE))))
                                            .addChild(new Container()
                                                    .setX(new Side(Side.POSITIVE))
                                                    .setY(new Side(Side.POSITIVE))
                                                    .setWidth(new Absolute(25))
                                                    .setHeight(new Absolute(25))
                                                    .setBackgroundColor(new Absolute(Color.WHITE))
                                                    .setOnClick(state1 -> state1.put("currentModuleTab", "main")));

                                    container1.addChild(new List(List.VERTICAL)
                                            .setHeight(new Relative(0.6))
                                            .setY(new Side(Side.NEGATIVE))
                                            .apply(container2 -> this.addSettingsList(container2, settings)));
                                }))
                        .setStateId("moduleScreen")
                        .setOnInit(state -> {
                            state.getFirst().addStoredState("moduleScreen");
                            state.getFirst().addStoredState("currentEditingModule");
                            state.getSecond().put("moduleScreen", "main");
                        }))
                .addChild(new Container()
                        .setX(new Side(Side.NEGATIVE))
                        .setPadding(new Relative(0.01))
                        .setBackgroundCornerRadius(new Relative(0.0075))
                        .setBackgroundColor(Color.fromRGB(26, 26, 26, 230)));
    }

    public boolean isHudEditScreenOpen() {
        return this.hudEditScreenOpen.get() && this.guiOpened.get();
    }

}
