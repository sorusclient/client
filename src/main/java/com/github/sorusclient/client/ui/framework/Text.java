package com.github.sorusclient.client.ui.framework;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.event.impl.MouseEvent;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.ui.framework.constraint.Absolute;
import com.github.sorusclient.client.ui.framework.constraint.Constraint;
import com.github.sorusclient.client.util.Color;

public class Text extends Component {

    private Constraint padding = new Absolute(0);

    private Constraint fontRenderer;
    private Constraint text;

    private Constraint scale = new Absolute(1);
    private Constraint textColor = new Absolute(Color.WHITE);

    public Text() {
        this.runtime = new Runtime();
    }

    public Text setPadding(Constraint padding) {
        this.padding = padding;
        return this;
    }

    public Text setTextColor(Constraint textColor) {
        this.textColor = textColor;
        return this;
    }

    public Text setFontRenderer(Constraint fontRenderer) {
        this.fontRenderer = fontRenderer;
        return this;
    }

    public Text setText(Constraint text) {
        this.text = text;
        return this;
    }

    public Text setScale(Constraint scale) {
        this.scale = scale;
        return this;
    }

    public class Runtime extends Component.Runtime {

        private double x, y;
        private double padding;

        @Override
        public void render(double x, double y, double width, double height) {
            IFontRenderer fontRenderer = Sorus.getInstance().get(Renderer.class).getFontRenderer(Text.this.fontRenderer.getStringValue(this));
            fontRenderer.drawString(Text.this.text.getStringValue(this), x - this.getWidth() / 2, y - this.getHeight() / 2, Text.this.scale.getPaddingValue(this), Text.this.textColor.getColorValue(this));
        }

        @Override
        public Container.Runtime getParent() {
            return (Container.Runtime) Text.this.parent.runtime;
        }

        @Override
        public double[] getCalculatedPosition() {
            this.padding = 0;
            this.x = 0;
            this.y = 0;

            for (int i = 0; i < 3; i++) {
                this.padding = Text.this.padding.getPaddingValue(this);

                this.x = Text.this.x.getXValue(this);
                this.y = Text.this.y.getYValue(this);
            }

            return new double[] {this.x, this.y, this.getWidth(), this.getHeight(), this.padding};
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

        @Override
        public double getWidth() {
            IFontRenderer fontRenderer = Sorus.getInstance().get(Renderer.class).getFontRenderer(Text.this.fontRenderer.getStringValue(this));
            return fontRenderer.getWidth(Text.this.text.getStringValue(this)) * Text.this.scale.getPaddingValue(this);
        }

        @Override
        public double getHeight() {
            IFontRenderer fontRenderer = Sorus.getInstance().get(Renderer.class).getFontRenderer(Text.this.fontRenderer.getStringValue(this));
            return fontRenderer.getHeight() * Text.this.scale.getPaddingValue(this);
        }

        @Override
        public double getPadding() {
            return this.padding;
        }

        @Override
        public boolean handleMouseEvent(MouseEvent event) {
            return false;
        }

        @Override
        public void handleKeyEvent(KeyEvent event) {

        }

    }

}
