package com.github.sorusclient.client.ui.framework;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.Button;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.adapter.IRenderer;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.adapter.event.KeyEvent;
import com.github.sorusclient.client.adapter.event.MouseEvent;
import com.github.sorusclient.client.ui.framework.constraint.Absolute;
import com.github.sorusclient.client.ui.framework.constraint.Constraint;
import com.github.sorusclient.client.util.Color;
import com.github.sorusclient.client.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Container extends Component {

    private Constraint backgroundCornerRadius = new Absolute(0);
    private Constraint topLeftBackgroundColor, bottomLeftBackgroundColor, bottomRightBackgroundColor, topRightBackgroundColor;
    private Constraint backgroundImage;

    private Constraint padding = new Absolute(0);

    protected final List<Component> children = new ArrayList<>();

    private Consumer<Map<String, Object>> onClick;
    private Consumer<Map<String, Object>> onDoubleClick;
    private Consumer<Pair<Map<String, Object>, Pair<Double, Double>>> onDrag;
    private Consumer<Pair<Map<String, Object>, Key>> onKey;
    private Consumer<Pair<Container, Map<String, Object>>> onInit;
    private Consumer<Pair<Double, Map<String, Object>>> onScroll;
    private boolean scissor = false;

    private List<Consumer<Map<String, Object>>> onUpdate = new ArrayList<>();

    public Container() {
        this.runtime = new Runtime();
    }

    public Container setX(Constraint x) {
        return (Container) super.setX(x);
    }

    public Container setY(Constraint y) {
        return (Container) super.setY(y);
    }

    public Container setWidth(Constraint width) {
        return (Container) super.setWidth(width);
    }

    public Container setHeight(Constraint height) {
        return (Container) super.setHeight(height);
    }

    //TODO: Make it max out at width / height so now weird looking shapes
    public Container setBackgroundCornerRadius(Constraint backgroundCornerRadius) {
        this.backgroundCornerRadius = backgroundCornerRadius;
        return this;
    }

    public Container setBackgroundColor(Color backgroundColor) {
        return this.setBackgroundColor(new Absolute(backgroundColor));
    }

    public Container setBackgroundColor(Constraint backgroundColor) {
        this.topLeftBackgroundColor = backgroundColor;
        this.bottomLeftBackgroundColor = backgroundColor;
        this.bottomRightBackgroundColor = backgroundColor;
        this.topRightBackgroundColor = backgroundColor;
        return this;
    }

    public Container setTopLeftBackgroundColor(Constraint topLeftBackgroundColor) {
        this.topLeftBackgroundColor = topLeftBackgroundColor;
        return this;
    }

    public Container setBottomLeftBackgroundColor(Constraint bottomLeftBackgroundColor) {
        this.bottomLeftBackgroundColor = bottomLeftBackgroundColor;
        return this;
    }

    public Container setBottomRightBackgroundColor(Constraint bottomRightBackgroundColor) {
        this.bottomRightBackgroundColor = bottomRightBackgroundColor;
        return this;
    }

    public Container setTopRightBackgroundColor(Constraint topRightBackgroundColor) {
        this.topRightBackgroundColor = topRightBackgroundColor;
        return this;
    }

    public Container setBackgroundImage(Constraint backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

    public Container setPadding(Constraint padding) {
        this.padding = padding;
        return this;
    }

    public Container addChild(Component child) {
        this.children.add(child);
        child.setParent(this);
        return this;
    }

    public Container apply(Consumer<Container> consumer) {
        consumer.accept(this);
        return this;
    }

    public Container setOnDoubleClick(Consumer<Map<String, Object>> onDoubleClick) {
        this.onDoubleClick = onDoubleClick;
        return this;
    }

    public Container setOnClick(Consumer<Map<String, Object>> onClick) {
        this.onClick = onClick;
        return this;
    }

    public Container setOnDrag(Consumer<Pair<Map<String, Object>, Pair<Double, Double>>> onDrag) {
        this.onDrag = onDrag;
        return this;
    }

    public Container setOnKey(Consumer<Pair<Map<String, Object>, Key>> onKey) {
        this.onKey = onKey;
        return this;
    }

    public Container setOnInit(Consumer<Pair<Container, Map<String, Object>>> onInit) {
        this.onInit = onInit;
        return this;
    }

    public void setOnScroll(Consumer<Pair<Double, Map<String, Object>>> onScroll) {
        this.onScroll = onScroll;
    }

    public Container addOnUpdate(Consumer<Map<String, Object>> onUpdate) {
        this.onUpdate.add(onUpdate);
        return this;
    }

    public Container setScissor(boolean scissor) {
        this.scissor = scissor;
        return this;
    }

    public Container clear() {
        this.children.clear();
        return this;
    }

    public class Runtime extends Component.Runtime {

        private double x, y;
        protected double width, height;
        private double padding;

        private final List<Pair<Component, double[]>> placedComponents = new ArrayList<>();
        private final List<Component> placedComponents2 = new ArrayList<>();

        private long prevClick = 0;

        private boolean heldClick = false;

        @Override
        public void render(double x, double y, double width, double height) {
            this.placedComponents.clear();
            this.placedComponents2.clear();

            Container container = Container.this;

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            if (!(boolean) this.getState("hasInit")) {
                if (Container.this.onInit != null) {
                    Map<String, Object> state = this.getAvailableState();
                    Container.this.onInit.accept(new Pair<>(Container.this, state));
                    this.setAvailableState(state);
                }

                this.setState("selected", false);
                this.setState("hasInit", true);
            }

            if (Container.this.onUpdate != null) {
                Map<String, Object> state = this.getAvailableState();
                for (Consumer<Map<String, Object>> onUpdate : Container.this.onUpdate) {
                    onUpdate.accept(state);
                }
                this.setAvailableState(state);
            }

            if ((boolean) this.getState("hidden")) {
                return;
            }

            IRenderer renderer = Sorus.getInstance().get(IAdapter.class).getRenderer();
            if (container.backgroundImage != null) {
                Color color = Color.WHITE;
                if (container.topLeftBackgroundColor != null) {
                    color = container.topLeftBackgroundColor.getColorValue(this);
                }

                renderer.drawImage(container.backgroundImage.getStringValue(this), x - width / 2, y - height / 2, width, height, color);
            } else if (container.topLeftBackgroundColor != null) {
                renderer.drawRectangle(x - width / 2, y - height / 2, width, height, container.backgroundCornerRadius.getCornerRadiusValue(this), container.topLeftBackgroundColor.getColorValue(this), container.bottomLeftBackgroundColor.getColorValue(this), container.bottomRightBackgroundColor.getColorValue(this), container.topRightBackgroundColor.getColorValue(this));
            }

            this.placedComponents.add(new Pair<>(null, new double[] {this.getWidth() / 2 + 0.5, 0, 1, this.getHeight() + 1, 0}));
            this.placedComponents.add(new Pair<>(null, new double[] {-this.getWidth() / 2 - 0.5, 0, 1, this.getHeight() + 1, 0}));

            this.placedComponents.add(new Pair<>(null, new double[] {0, this.getHeight() / 2 + 0.5, this.getWidth() + 1, 1, 0}));
            this.placedComponents.add(new Pair<>(null, new double[] {0, -this.getHeight() / 2 - 0.5, this.getWidth() + 1, 1, 0}));

            if (Container.this.scissor) {
                renderer.scissor(x - width / 2, y - height / 2, width, height);
            }

            for (Component child : this.getChildren()) {
                if (!(boolean) child.getRuntime().getState("hidden")) {
                    double[] wantedPosition = this.getOtherCalculatedPosition(child);
                    double childX = wantedPosition[0];
                    double childY = wantedPosition[1];
                    double childWidth = wantedPosition[2];
                    double childHeight = wantedPosition[3];

                    this.addPlacedComponents(child, wantedPosition);
                    this.placedComponents2.add(child);

                    this.renderChild(child.runtime, this.x + childX, this.y + childY, childWidth, childHeight);
                } else {
                    this.renderChild(child.runtime, -1, -1, -1, -1);
                }
            }

            if (Container.this.scissor) {
                renderer.endScissor();
            }
        }

        protected void renderChild(Component.Runtime childRuntime, double x, double y, double width, double height) {
            childRuntime.render(x, y, width, height);
        }

        @Override
        public Container.Runtime getParent() {
            return (Runtime) Container.this.parent.runtime;
        }

        protected double[] getOtherCalculatedPosition(Component child) {
            return child.runtime.getCalculatedPosition();
        }

        protected void addPlacedComponents(Component child, double[] wantedPosition) {
            this.placedComponents.add(new Pair<>(child, wantedPosition));
        }

        @Override
        public double[] getCalculatedPosition() {
            this.padding = 0;
            this.x = 0;
            this.y = 0;
            this.width = 0;
            this.height = 0;

            for (int i = 0; i < 3; i++) {
                this.padding = Container.this.padding.getPaddingValue(this);

                this.x = Container.this.x.getXValue(this);
                this.y = Container.this.y.getYValue(this);
                this.width = Container.this.width.getWidthValue(this);
                this.height = Container.this.height.getHeightValue(this);
            }

            return new double[] {this.x, this.y, this.width, this.height, this.padding};
        }

        @Override
        public void setX(double x) {
            this.x = x;
        }

        @Override
        public void setY(double y) {
            this.y = y;
        }

        @Override
        public double getX() {
            return this.x;
        }

        @Override
        public double getY() {
            return this.y;
        }

        public double getWidth() {
            return this.width;
        }

        public double getHeight() {
            return this.height;
        }

        @Override
        public double getPadding() {
            return this.padding;
        }

        public List<double[]> getPlacedComponents() {
            List<double[]> placedComponents = new ArrayList<>();
            for (Pair<Component, double[]> componentPair : this.placedComponents) {
                placedComponents.add(componentPair.getSecond());
            }
            return placedComponents;
        }

        protected List<Component> getChildren() {
            return Container.this.children;
        }

        @Override
        public void onStateUpdate(String id, Object value) {
            super.onStateUpdate(id, value);
            for (Component child : this.getChildren()) {
                if (child != null) {
                    child.runtime.onStateUpdate(id, value);
                }
            }
        }

        @Override
        public boolean handleMouseEvent(MouseEvent event) {
            Map<String, Object> state = this.getAvailableState();
            if (event.isPressed() && event.getButton() == Button.PRIMARY) {
                if (event.getX() > this.x - this.width / 2 &&
                        event.getX() < this.x + this.width / 2 &&
                        event.getY() > this.y - this.height / 2 &&
                        event.getY() < this.y + this.height / 2) {

                    state.put("selected", true);
                } else {
                    state.put("selected", false);
                }
            }

            boolean handled = false;
            for (Component component : this.placedComponents2) {
                if (component != null) {
                    handled = component.runtime.handleMouseEvent(event);

                    if (handled) {
                        return true;
                    }
                }
            }

            if (event.getX() > this.x - this.width / 2 &&
                    event.getX() < this.x + this.width / 2 &&
                    event.getY() > this.y - this.height / 2 &&
                    event.getY() < this.y + this.height / 2) {

                if (event.getWheel() != 0 && Container.this.onScroll != null) {
                    Container.this.onScroll.accept(new Pair<>(event.getWheel(), state));
                }
            }

            if (event.isPressed() && event.getButton() == Button.PRIMARY) {

                if (event.getX() > this.x - this.width / 2 &&
                        event.getX() < this.x + this.width / 2 &&
                        event.getY() > this.y - this.height / 2 &&
                        event.getY() < this.y + this.height / 2) {

                    if (Container.this.onClick != null) {
                        handled = true;
                        Container.this.onClick.accept(state);
                    }

                    this.heldClick = true;

                    if (System.currentTimeMillis() - this.prevClick < 400) {
                        if (Container.this.onDoubleClick != null) {
                            handled = true;
                            Container.this.onDoubleClick.accept(state);
                        }
                    }

                    this.prevClick = System.currentTimeMillis();

                    state.put("selected", true);
                } else {
                    state.put("selected", false);
                }
            } else if (!event.isPressed() && event.getButton() == Button.PRIMARY) {
                this.heldClick = false;
            }

            if (this.heldClick) {
                if (Container.this.onDrag != null) {
                    handled = true;
                    Container.this.onDrag.accept(new Pair<>(state, new Pair<>(Math.min(1, Math.max(0, (event.getX() - (this.x - this.width / 2)) / this.width)), Math.min(1, Math.max(0, (event.getY() - (this.y - this.height / 2)) / this.height)))));
                }
            }

            this.setAvailableState(state);

            return handled;
        }

        @Override
        public void handleKeyEvent(KeyEvent event) {
            if ((boolean) this.getState("selected")) {
                if (Container.this.onKey != null) {
                    Map<String, Object> state = this.getAvailableState();

                    Container.this.onKey.accept(new Pair<>(state, event.getKey()));

                    this.setAvailableState(state);
                }
            }

            for (Component component : this.placedComponents2) {
                if (component != null) {
                    component.runtime.handleKeyEvent(event);
                }
            }
        }

        @Override
        public void setHasInit(boolean hasInit) {
            super.setHasInit(hasInit);

            if (!hasInit) {
                for (Component component : this.getChildren()) {
                    component.runtime.setHasInit(false);
                }
            }
        }
    }

}
