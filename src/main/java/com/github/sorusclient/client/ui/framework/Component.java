package com.github.sorusclient.client.ui.framework;

import com.github.sorusclient.client.adapter.event.KeyEvent;
import com.github.sorusclient.client.adapter.event.MouseEvent;
import com.github.sorusclient.client.ui.framework.constraint.Constraint;
import com.github.sorusclient.client.ui.framework.constraint.Flexible;
import com.github.sorusclient.client.ui.framework.constraint.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Component {

    protected Constraint x = new Side(Side.ZERO), y = new Side(Side.ZERO);
    protected Constraint width = new Flexible(), height = new Flexible();

    protected Runtime runtime;

    protected Container parent;

    private final List<String> storedState = new ArrayList<>();
    private final Map<String, Consumer<Map<String, Object>>> onStateUpdates = new HashMap<>();

    public Component() {
        this.addStoredState("selected");
        this.addStoredState("hasInit");
    }

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

    public Component addOnStateUpdate(String state, Consumer<Map<String, Object>> onStateUpdate) {
        this.onStateUpdates.put(state, onStateUpdate);
        return this;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public abstract class Runtime {

        public final Map<String, Object> state = new HashMap<>();

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

        public Runtime() {
            this.setState("hasInit", false);
        }

        public void setState(String id, Object value) {
            if (Component.this.storedState.contains(id)) {
                this.state.put(id, value);
                this.onStateUpdate(id, value);
            } else {
                this.getParent().setState(id, value);
            }
        }

        public void onStateUpdate(String id, Object value) {
            Consumer<Map<String, Object>> onStateUpdate = Component.this.onStateUpdates.get(id);
            if (onStateUpdate != null) {
                Map<String, Object> state = this.getAvailableState();
                onStateUpdate.accept(state);
                this.setAvailableState(state);
            }
        }

        public Object getState(String id) {
            if (Component.this.storedState.contains(id)) {
                return this.state.get(id);
            } else {
                if (Component.this.parent != null) {
                    return this.getParent().getState(id);
                } else {
                    return null;
                }
            }
        }

        public Map<String, Object> getAvailableState() {
            Map<String, Object> availableState = new HashMap<>();

            if (Component.this.parent != null) {
                availableState.putAll(this.getParent().getAvailableState());
            }

            availableState.putAll(this.state);

            return availableState;
        }

        public void setAvailableState(Map<String, Object> state) {
            for (Map.Entry<String, Object> entry : state.entrySet()) {
                if (!entry.getValue().equals(this.getState(entry.getKey()))) {
                    this.setState(entry.getKey(), entry.getValue());
                }
            }
        }

        public void setHasInit(boolean hasInit) {
            this.setState("hasInit", hasInit);
        }

        public abstract boolean handleMouseEvent(MouseEvent event);
        public abstract void handleKeyEvent(KeyEvent event);

    }

}
