package com.github.sorusclient.client.ui.framework;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.event.impl.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class ContainerRenderer {

    private final List<KeyEvent> keyEvents = new ArrayList<>();
    private final List<MouseEvent> mouseEvents = new ArrayList<>();

    public void initialize() {
        EventManager eventManager = Sorus.getInstance().get(EventManager.class);
        eventManager.register(KeyEvent.class, this.keyEvents::add);
        eventManager.register(MouseEvent.class, this.mouseEvents::add);
    }

    public void render(Container container) {
        double[] screenDimensions = Sorus.getInstance().get(MinecraftAdapter.class).getScreenDimensions();

        container.runtime.render(screenDimensions[0] / 2, screenDimensions[1] / 2, screenDimensions[0], screenDimensions[1]);

        for (KeyEvent event : this.keyEvents) {
            container.runtime.handleKeyEvent(event);
        }

        for (MouseEvent event : this.mouseEvents) {
            container.runtime.handleMouseEvent(event);
        }

        this.keyEvents.clear();
        this.mouseEvents.clear();
    }

}
