package com.github.sorusclient.client.ui.framework;

import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.event.impl.MouseEvent;
import com.github.sorusclient.client.ui.framework.constraint.Constraint;
import com.github.sorusclient.client.ui.framework.constraint.Flexible;
import com.github.sorusclient.client.ui.framework.constraint.Side;
import com.github.sorusclient.client.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Component {

    protected Constraint x = new Side(Side.ZERO), y = new Side(Side.ZERO);
    protected Constraint width = new Flexible(), height = new Flexible();

    protected Runtime runtime;

    private List<String> storedState = new ArrayList<>();
    private Map<String, Consumer<Pair<Component, Object>>> onStateUpdates = new HashMap<>();

    public Component setX(Constraint x) {
        this.x = x;
        return this;
    }

    public Component setY(Constraint y) {
        this.y = y;
        return this;
    }

    public Component setWidth(Constraint width) {
        this.width = width;
        return this;
    }

    public Component setHeight(Constraint height) {
        this.height = height;
        return this;
    }

    public Component addStoredState(String storedState) {
        this.storedState.add(storedState);
        return this;
    }

    public Component addOnStateUpdate(String state, Consumer<Pair<Component, Object>> onStateUpdate) {
        this.onStateUpdates.put(state, onStateUpdate);
        return this;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public abstract void setParent(Container parent);

    public abstract class Runtime {

        private final Map<String, Object> state = new HashMap<>();

        public abstract void render(double x, double y, double width, double height);

        public abstract Container.Runtime getParent();

        public abstract double[] getCalculatedPosition();

        public abstract void setX(double x);
        public abstract void setY(double y);

        public abstract double getX();
        public abstract double getY();

        public abstract double getWidth();
        public abstract double getHeight();

        public abstract double getPadding();

        public void setState(String id, Object value) {
            if (Component.this.storedState.contains(id)) {
                this.state.put(id, value);
                this.onStateUpdate(id, value);
            } else {
                this.getParent().setState(id, value);
            }
        }

        public void onStateUpdate(String id, Object value) {
            Consumer<Pair<Component, Object>> onStateUpdate = Component.this.onStateUpdates.get(id);
            if (onStateUpdate != null) {
                onStateUpdate.accept(new Pair<>(Component.this, value));
            }
        }

        public Object getState(String id) {
            if (Component.this.storedState.contains(id)) {
                return this.state.get(id);
            } else {
                return this.getParent().getState(id);
            }
        }

        public abstract void handleMouseEvent(MouseEvent event);
        public abstract void handleKeyEvent(KeyEvent event);

    }

}
