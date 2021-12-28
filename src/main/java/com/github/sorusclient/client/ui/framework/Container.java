package com.github.sorusclient.client.ui.framework;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.Button;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.event.impl.MouseEvent;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.ui.framework.constraint.Absolute;
import com.github.sorusclient.client.ui.framework.constraint.Constraint;
import com.github.sorusclient.client.util.Color;
import com.github.sorusclient.client.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Container extends Component {

    private Constraint backgroundCornerRadius = new Absolute(0);
    private Constraint backgroundColor;
    private Constraint backgroundImage;

    private Constraint padding = new Absolute(0);

    protected final List<Component> children = new ArrayList<>();
    private Container parent;

    private Consumer<Container> onClick;

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

    public Container setBackgroundCornerRadius(Constraint backgroundCornerRadius) {
        this.backgroundCornerRadius = backgroundCornerRadius;
        return this;
    }

    public Container setBackgroundColor(Constraint backgroundColor) {
        this.backgroundColor = backgroundColor;
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

    public Container setOnClick(Consumer<Container> onClick) {
        this.onClick = onClick;
        return this;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public class Runtime extends Component.Runtime {

        private double x, y;
        protected double width, height;
        private double padding;

        private final List<Pair<Component, double[]>> placedComponents = new ArrayList<>();

        @Override
        public void render(double x, double y, double width, double height) {
            this.placedComponents.clear();

            Container container = Container.this;

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            Renderer renderer = Sorus.getInstance().get(Renderer.class);
            if (container.backgroundImage != null) {
                Color color = Color.WHITE;
                if (container.backgroundColor != null) {
                    color = container.backgroundColor.getColorValue(this);
                }

                renderer.drawImage(container.backgroundImage.getStringValue(this), x - width / 2, y - height / 2, width, height, color);
            } else if (container.backgroundColor != null) {
                renderer.drawRectangle(x - width / 2, y - height / 2, width, height, container.backgroundCornerRadius.getCornerRadiusValue(this), container.backgroundColor.getColorValue(this));
            }

            this.placedComponents.add(new Pair<>(null, new double[] {this.getWidth() / 2 + 0.5, 0, 1, this.getHeight() + 1, 0}));
            this.placedComponents.add(new Pair<>(null, new double[] {-this.getWidth() / 2 - 0.5, 0, 1, this.getHeight() + 1, 0}));

            this.placedComponents.add(new Pair<>(null, new double[] {0, this.getHeight() / 2 + 0.5, this.getWidth() + 1, 1, 0}));
            this.placedComponents.add(new Pair<>(null, new double[] {0, -this.getHeight() / 2 - 0.5, this.getWidth() + 1, 1, 0}));

            for (Component child : this.getChildren()) {
                double[] wantedPosition = child.runtime.getCalculatedPosition();
                double childX = wantedPosition[0];
                double childY = wantedPosition[1];
                double childWidth = wantedPosition[2];
                double childHeight = wantedPosition[3];

                this.placedComponents.add(new Pair<>(child, wantedPosition));

                this.renderChild(child.runtime, this.x + childX, this.y + childY, childWidth, childHeight);
            }
        }

        protected void renderChild(Component.Runtime childRuntime, double x, double y, double width, double height) {
            childRuntime.render(x, y, width, height);
        }

        @Override
        public Container.Runtime getParent() {
            return (Runtime) Container.this.parent.runtime;
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
                child.runtime.onStateUpdate(id, value);
            }
        }

        @Override
        public void handleMouseEvent(MouseEvent event) {
            if (Container.this.onClick != null && event.isPressed() && event.getButton() == Button.PRIMARY) {
                Container.this.onClick.accept(Container.this);
            }

            for (Pair<Component, double[]> component : this.placedComponents) {
                double[] componentPosition = component.getSecond();
                if (event.getX() > this.x + componentPosition[0] - componentPosition[2] / 2 &&
                        event.getX() < this.x + componentPosition[0] + componentPosition[2] / 2 &&
                        event.getY() > this.y + componentPosition[1] - componentPosition[3] / 2 &&
                        event.getY() < this.y + componentPosition[1] + componentPosition[3] / 2) {
                    component.getFirst().runtime.handleMouseEvent(event);
                }
            }
        }

        @Override
        public void handleKeyEvent(KeyEvent event) {

        }

    }

}
