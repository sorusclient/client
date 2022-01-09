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
        return (List) super.addChild(child);
    }

    public List setColumns(int columns) {
        this.columns = columns;
        return this;
    }

    public class Runtime extends Container.Runtime {

        private boolean firstRender = true;

        private int xIndex;
        private double xLocation;
        private double yLocation;

        @Override
        public void render(double x, double y, double width, double height) {
            this.xIndex = 0;
            this.xLocation = -width / 2;
            this.yLocation = -height / 2;

            super.render(x, y, width, height);
        }

        @Override
        protected double[] getOtherCalculatedPosition(Component child) {
            double[] array = super.getOtherCalculatedPosition(child);
            Component.Runtime childRuntime = child.runtime;

            this.xIndex++;

            if ((List.this.type & HORIZONTAL) != 0) {
                double position = this.xLocation + childRuntime.getWidth() / 2 + childRuntime.getPadding();
                array[0] = position;

                this.xLocation += childRuntime.getWidth() + childRuntime.getPadding();

                if (this.xIndex >= List.this.columns) {
                    this.xLocation = -this.width / 2;
                }
            }

            if ((List.this.type & VERTICAL) != 0) {
                double position = this.yLocation + childRuntime.getHeight() / 2 + childRuntime.getPadding();
                array[1] = position;

                if (this.xIndex >= List.this.columns) {
                    this.yLocation += childRuntime.getHeight() + childRuntime.getPadding();
                }
            }

            if (this.xIndex >= List.this.columns) {
                this.xIndex = 0;
            }

            return array;
        }

    }

}
