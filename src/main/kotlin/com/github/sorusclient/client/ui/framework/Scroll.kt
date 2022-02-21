package com.github.sorusclient.client.ui.framework

class Scroll(type: Int) : List(type) {

    init {
        runtime = Runtime()
        storedState += "scroll"
        onScroll = { state ->
            state.second["scroll"] = state.second.getOrDefault("scroll", 0.0) as Double + state.first * 3
        }
        scissor = true
    }

    inner class Runtime : List.Runtime() {
        private var prevYLocation = 0.0

        override fun render(x: Double, y: Double, width: Double, height: Double) {
            super.render(x, y, width, height)
            prevYLocation = yLocation + height / 2
        }

        override fun renderChild(
            childRuntime: Component.Runtime?,
            x: Double,
            y: Double,
            width: Double,
            height: Double
        ) {
            var scroll = getState("scroll") as Double?
            if (scroll == null || scroll.isNaN()) {
                scroll = 0.0
            }
            scroll = Math.max(scroll, -(prevYLocation - this.height + children[0].runtime.topPadding))
            scroll = Math.min(scroll, 0.0)
            setState("scroll", scroll)
            super.renderChild(childRuntime, x, y + scroll, width, height)
        }
    }
}