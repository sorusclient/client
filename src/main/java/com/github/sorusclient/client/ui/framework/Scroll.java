package com.github.sorusclient.client.ui.framework;

public class Scroll extends List {

    public Scroll(int type) {
        super(type);
        this.runtime = new Runtime();

        this.addStoredState("scroll");
        this.setOnInit(state -> state.getSecond().put("scroll", 0.0));
        this.setOnScroll(state -> {
            state.getSecond().put("scroll", (double) state.getSecond().get("scroll") + state.getFirst() * 3);
        });
    }

    public class Runtime extends List.Runtime {

        private double prevYLocation;

        @Override
        public void render(double x, double y, double width, double height) {
            super.render(x, y, width, height);
            this.prevYLocation = this.yLocation + height / 2;
        }

        @Override
        protected void renderChild(Component.Runtime childRuntime, double x, double y, double width, double height) {
            double scroll = (double) this.getState("scroll");
            scroll = Math.max(scroll, -(this.prevYLocation - this.height + this.getChildren().get(0).getRuntime().getPadding()));
            scroll = Math.min(scroll, 0);
            this.setState("scroll", scroll);
            super.renderChild(childRuntime, x, y + scroll, width, height);
        }

    }

}
