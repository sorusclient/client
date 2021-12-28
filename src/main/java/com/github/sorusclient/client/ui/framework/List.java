package com.github.sorusclient.client.ui.framework;

import com.github.sorusclient.client.ui.framework.constraint.Side;

public class List extends Container {

    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;
    public static final int GRID = 1 | 2;

    private final int type;

    private int columns;

    public List(int type) {
        this.type = type;

        if (this.type == VERTICAL) {
            this.columns = 1;
        } else if (this.type == HORIZONTAL) {
            this.columns = 9999;
        }

        this.runtime = new Runtime();
    }

    @Override
    public List addChild(Component child) {
        if ((List.this.type & HORIZONTAL) != 0) {
            child.setX(new Side(Side.NEGATIVE));
        }
        if ((this.type & VERTICAL) != 0) {
            child.setY(new Side(Side.NEGATIVE));
        }

        return (List) super.addChild(child);
    }

    public List setColumns(int columns) {
        this.columns = columns;
        return this;
    }

    public class Runtime extends Container.Runtime {

        private boolean firstRender = true;

        private int index = 0;

        @Override
        public void render(double x, double y, double width, double height) {
            this.index = 0;
            if (this.firstRender) {
                for (Component component : List.this.children) {
                    if ((List.this.type & HORIZONTAL) != 0) {
                        component.runtime.setX(width / 3);
                    }
                    if ((List.this.type & VERTICAL) != 0) {
                        component.runtime.setY(height / 3);
                    }
                }
                this.firstRender = false;
            }

            super.render(x, y, width, height);
        }

        @Override
        protected double[] getOtherCalculatedPosition(Component child) {
            double[] array = super.getOtherCalculatedPosition(child);
            Component.Runtime childRuntime = child.runtime;

            if ((List.this.type & HORIZONTAL) != 0) {
                array[0] = -this.width / 2 + childRuntime.getWidth() / 2 + childRuntime.getPadding() + (childRuntime.getWidth() + childRuntime.getPadding()) * (index % columns);
            }
            if ((List.this.type & VERTICAL) != 0) {
                array[1] = -this.height / 2 + childRuntime.getHeight() / 2 + childRuntime.getPadding() + (childRuntime.getHeight() + childRuntime.getPadding()) * (int) (index / columns);
            }

            this.index++;

            return array;
        }

    }

}
