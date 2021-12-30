package com.github.sorusclient.client.hud;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;
import com.github.sorusclient.client.setting.SettingContainer;
import com.github.sorusclient.client.setting.Util;
import com.github.sorusclient.client.util.Axis;

import java.util.*;

public abstract class HUDElement implements SettingContainer {

    private final Map<String, Setting<?>> settings = new HashMap<>();

    private final Setting<Map<String, List<AttachType>>> attached;
    private final Setting<Double> x, y;
    private final Setting<Double> offsetX, offsetY;
    private final Setting<Double> scale;

    private final Setting<String> internalId;

    private final String id;

    @SuppressWarnings("unchecked")
    public HUDElement(String id) {
        this.id = id;

        this.register("x", this.x = new Setting<>(0.0));
        this.register("y", this.y = new Setting<>(0.0));
        this.register("offsetX", this.offsetX = new Setting<>(-1.0));
        this.register("offsetY", this.offsetY = new Setting<>(-1.0));
        this.register("scale", this.scale = new Setting<>(1.0));
        this.register("internalId", this.internalId = new Setting<>(""));
        this.register("attached", this.attached = new Setting<>((Class<Map<String, List<AttachType>>>) (Class<?>) Map.class, new HashMap<>()));
    }

    protected void register(String id, Setting<?> setting) {
        this.settings.put(id, setting);
    }

    public void setInternalId(String id) {
        this.internalId.setValue(id);
    }

    public String getInternalId() {
        return internalId.getValue();
    }

    public String getId() {
        return id;
    }

    public void updatePosition(HUDElement attachedElement, double[] screenDimensions) {
        this.updatePosition(attachedElement, screenDimensions, new ArrayList<>());
    }

    private void updatePosition(HUDElement attachedElement, double[] screenDimensions, List<HUDElement> alreadyUpdated) {
        if (!alreadyUpdated.contains(this)) {
            alreadyUpdated.add(this);

            AttachType attachTypeX = null;
            AttachType attachTypeY = null;
            for (AttachType attachType : this.attached.getValue().get(attachedElement == null ? "null" : attachedElement.getInternalId())) {
                if (attachType.getAxis() == Axis.X) {
                    attachTypeX = attachType;
                } else {
                    attachTypeY = attachType;
                }
            }

            double attachedX = attachedElement == null ? screenDimensions[0] / 2 : attachedElement.getX(screenDimensions[0]);
            double attachedWidth = attachedElement == null ? screenDimensions[0] : attachedElement.getScaledWidth();
            double attachedY = attachedElement == null ? screenDimensions[1] / 2 : attachedElement.getY(screenDimensions[1]);
            double attachedHeight = attachedElement == null ? screenDimensions[1] : attachedElement.getScaledHeight();

            double newX = this.getX(screenDimensions[0]);
            if (attachTypeX != null) {
                newX = attachedX + attachTypeX.getOtherSide() * attachedWidth / 2 - attachTypeX.getSelfSide() * this.getScaledWidth() / 2;
            }

            double newY = this.getX(screenDimensions[1]);
            if (attachTypeY != null) {
                newY = attachedY + attachTypeY.getOtherSide() * attachedHeight / 2 - attachTypeY.getSelfSide() * this.getScaledHeight() / 2;
            }

            this.setPosition(
                    newX,
                    newY,
                    screenDimensions);

            for (String hudId : this.attached.getValue().keySet()) {
                HUDElement hud = Sorus.getInstance().get(HUDManager.class).getById(hudId);
                if (hud == null) continue;
                hud.updatePosition(this, screenDimensions, alreadyUpdated);
            }
        }
    }

    public void setScale(double scale) {
        this.scale.setValue(scale);
    }

    public void setPosition(double x, double y, double[] screenDimensions) {
        if (x > screenDimensions[0] * 2 / 3) {
            this.offsetX.setValue(1.0);
        } else if (x > screenDimensions[0] * 1 / 3) {
            this.offsetX.setValue(0.0);
        } else {
            this.offsetX.setValue(-1.0);
        }

        if (y > screenDimensions[1] * 2 / 3) {
            this.offsetY.setValue(1.0);
        } else if (x > screenDimensions[0] * 1 / 3) {
            this.offsetY.setValueRaw(0.0);
        } else {
            this.offsetY.setValue(-1.0);
        }

        this.x.setValue((x + this.getScaledWidth() / 2 * offsetX.getValue()) / screenDimensions[0]);
        this.y.setValue((y + this.getScaledHeight() / 2 * offsetY.getValue()) / screenDimensions[1]);
    }

    public void setPositionSpecific(double x, double y, double offsetX, double offsetY) {
        this.x.setValue(x);
        this.y.setValue(y);
        this.offsetX.setValue(offsetX);
        this.offsetY.setValue(offsetY);
    }

    public double getX(double screenWidth) {
        return -this.offsetX.getValue() * this.getScaledWidth() / 2 + this.x.getValue() * screenWidth;
    }

    public double getY(double screenHeight) {
        return -this.offsetY.getValue() * this.getScaledHeight() / 2 + this.y.getValue() * screenHeight;
    }

    public double getScale() {
        return this.scale.getValue();
    }

    public void addAttached(HUDElement hudElement, AttachType attachType) {
        this.attached.getValue().computeIfAbsent(hudElement == null ? "null" : hudElement.getInternalId(), k -> new ArrayList<>()).add(attachType);
    }

    public void clearStaticAttached(List<HUDElement> alreadyCleared) {
        alreadyCleared.add(this);

        for (String key : new HashMap<>(this.attached.getValue()).keySet()) {
            if (key.equals("null")) {
                this.attached.getValue().remove(key);
            }
        }

        for (String elementId : this.attached.getValue().keySet()) {
            HUDElement hudElement = Sorus.getInstance().get(HUDManager.class).getById(elementId);
            if (!alreadyCleared.contains(hudElement)) {
                hudElement.clearStaticAttached(alreadyCleared);
            }
        }
    }

    public void detach() {
        for (String elementId : this.attached.getValue().keySet()) {
            Sorus.getInstance().get(HUDManager.class).getById(elementId).detachOther(this);
        }

        this.attached.getValue().clear();
    }

    private void detachOther(HUDElement hud) {
        this.attached.getValue().remove(hud.getInternalId());
    }

    public void delete(List<HUDElement> alreadyDeleted) {
        alreadyDeleted.add(this);
        Sorus.getInstance().get(HUDManager.class).remove(this);

        for (String elementId : this.attached.getValue().keySet()) {
            HUDElement element = Sorus.getInstance().get(HUDManager.class).getById(elementId);
            if (!alreadyDeleted.contains(element)) {
                if (element != null) {
                    element.delete(alreadyDeleted);
                }
            }
        }
    }

    public List<String> getAttached() {
        return new ArrayList<>(this.attached.getValue().keySet());
    }

    protected abstract void render(double x, double y, double scale);
    public abstract double getWidth();
    public abstract double getHeight();

    public double getScaledWidth() {
        return this.getWidth() * this.getScale();
    }

    public double getScaledHeight() {
        return this.getHeight() * this.getScale();
    }

    public final void render() {
        double[] screenDimensions = Sorus.getInstance().get(MinecraftAdapter.class).getScreenDimensions();
        this.render(this.getX(screenDimensions[0]) - this.getScaledWidth() / 2, this.getY(screenDimensions[1]) - this.getScaledHeight() / 2, this.getScale());
    }

    public boolean isAttachedTo(HUDElement other) {
        return this.getAttached().contains(other.getInternalId());
    }

    @Override
    public void load(Map<String, Object> settings) {
        for (Map.Entry<String, Object> setting : settings.entrySet()) {
            Setting<?> setting1 = this.settings.get(setting.getKey());
            if (setting1 != null) {
                setting1.setValueRaw(Util.toJava(setting1.getType(), setting.getValue()));
            }
        }
    }

    @Override
    public Map<String, Object> save() {
        Map<String, Object> settingsMap = new HashMap<>();
        for (Map.Entry<String, Setting<?>> setting : this.settings.entrySet()) {
            settingsMap.put(setting.getKey(), Util.toData(setting.getValue().getValue()));
        }
        settingsMap.put("class", this.getClass().getName());
        return settingsMap;
    }

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public void setShared(boolean isShared) {

    }

    public void addSettings(List<SettingConfigurableData> settings) {

    }

}
