package com.github.sorusclient.client.module.impl.blockoverlay;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.adapter.event.BlockOutlineRenderEvent;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;
import com.github.sorusclient.client.util.Color;

import java.util.List;

public class BlockOverlay extends ModuleDisableable {

    private final Setting<Color> borderColor;
    private final Setting<Double> borderThickness;
    private final Setting<Color> fillColor;

    public BlockOverlay() {
        super("blockOverlay");

        this.register("borderColor", this.borderColor = new Setting<>(Color.BLACK));
        this.register("borderThickness", this.borderThickness = new Setting<>(1.0));
        this.register("fillColor", this.fillColor = new Setting<>(Color.fromRGB(0, 0, 0, 0)));

        Sorus.getInstance().get(EventManager.class).register(BlockOutlineRenderEvent.class, this::preRenderBlockOutline);
    }
    
    private void preRenderBlockOutline(BlockOutlineRenderEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        Box box = event.getBox();
        RenderBuffer buffer = new RenderBuffer();
        buffer.setDrawMode(RenderBuffer.DrawMode.QUAD);

        Color fillColor = this.getFillColor();

        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMinY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMaxY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMaxY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMinY(), box.getMinZ())).setColor(fillColor));

        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMinY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMaxY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMaxY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMinY(), box.getMaxZ())).setColor(fillColor));

        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMinY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMaxY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMaxY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMinY(), box.getMaxZ())).setColor(fillColor));

        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMinY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMaxY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMaxY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMinY(), box.getMinZ())).setColor(fillColor));

        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMaxY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMaxY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMaxY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMaxY(), box.getMaxZ())).setColor(fillColor));

        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMinY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMinY(), box.getMaxZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMinX(), box.getMinY(), box.getMinZ())).setColor(fillColor));
        buffer.push(new Vertex().setPoint(new Point(box.getMaxX(), box.getMinY(), box.getMinZ())).setColor(fillColor));

        IRenderer renderer = Sorus.getInstance().get(IAdapter.class).getRenderer();
        renderer.draw(buffer);

        renderer.setLineThickness(this.getBorderThickness());
        renderer.setColor(this.getBorderColor());
    }

    public Color getBorderColor() {
        return this.borderColor.getValue();
    }

    public double getBorderThickness() {
        return this.borderThickness.getValue();
    }

    public Color getFillColor() {
        return this.fillColor.getValue();
    }

    @Override
    public void addSettings(List<ConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new ConfigurableData.Slider("Border Thickness", this.borderThickness, 0.0, 5.0));
        settings.add(new ConfigurableData.ColorPicker("Border Color", this.borderColor));
        settings.add(new ConfigurableData.ColorPicker("Fill Color", this.fillColor));
    }

}
