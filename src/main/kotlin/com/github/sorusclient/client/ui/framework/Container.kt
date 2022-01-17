package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.ui.framework.constraint.Absolute
import com.github.sorusclient.client.ui.framework.constraint.Constraint
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.Pair
import java.util.function.Consumer
import kotlin.collections.List

open class Container : Component() {
    private var backgroundCornerRadius: Constraint = Absolute(0.0)
    private var topLeftBackgroundColor: Constraint? = null
    private var bottomLeftBackgroundColor: Constraint? = null
    private var bottomRightBackgroundColor: Constraint? = null
    private var topRightBackgroundColor: Constraint? = null
    private var backgroundImage: Constraint? = null
    private var padding: Constraint = Absolute(0.0)
    protected val children: MutableList<Component> = ArrayList()
    private var onClick: Consumer<MutableMap<String, Any>>? = null
    private var onDoubleClick: Consumer<Map<String, Any>>? = null
    private var onDrag: Consumer<Pair<MutableMap<String, Any>, Pair<Double, Double>>>? = null
    private var onKey: Consumer<Pair<MutableMap<String, Any>, Key>>? = null
    private var onInit: Consumer<Pair<Container, MutableMap<String, Any>>>? = null
    private var onScroll: Consumer<Pair<Double, MutableMap<String, Any>>>? = null
    private val onUpdate: MutableList<Consumer<MutableMap<String, Any>>> = ArrayList()
    private var scissor = false

    init {
        runtime = Runtime()
    }

    override fun setX(x: Constraint): Container {
        return super.setX(x) as Container
    }

    override fun setY(y: Constraint): Container {
        return super.setY(y) as Container
    }

    override fun setWidth(width: Constraint): Container {
        return super.setWidth(width) as Container
    }

    override fun setHeight(height: Constraint): Container {
        return super.setHeight(height) as Container
    }

    //TODO: Make it max out at width / height so now weird looking shapes
    fun setBackgroundCornerRadius(backgroundCornerRadius: Constraint): Container {
        this.backgroundCornerRadius = backgroundCornerRadius
        return this
    }

    fun setBackgroundColor(backgroundColor: Color): Container {
        return this.setBackgroundColor(Absolute(backgroundColor))
    }

    fun setBackgroundColor(backgroundColor: Constraint): Container {
        topLeftBackgroundColor = backgroundColor
        bottomLeftBackgroundColor = backgroundColor
        bottomRightBackgroundColor = backgroundColor
        topRightBackgroundColor = backgroundColor
        return this
    }

    fun setTopLeftBackgroundColor(topLeftBackgroundColor: Constraint): Container {
        this.topLeftBackgroundColor = topLeftBackgroundColor
        return this
    }

    fun setBottomLeftBackgroundColor(bottomLeftBackgroundColor: Constraint): Container {
        this.bottomLeftBackgroundColor = bottomLeftBackgroundColor
        return this
    }

    fun setBottomRightBackgroundColor(bottomRightBackgroundColor: Constraint): Container {
        this.bottomRightBackgroundColor = bottomRightBackgroundColor
        return this
    }

    fun setTopRightBackgroundColor(topRightBackgroundColor: Constraint): Container {
        this.topRightBackgroundColor = topRightBackgroundColor
        return this
    }

    fun setBackgroundImage(backgroundImage: Constraint): Container {
        this.backgroundImage = backgroundImage
        return this
    }

    fun setPadding(padding: Constraint): Container {
        this.padding = padding
        return this
    }

    open fun addChild(child: Component): Container {
        children.add(child)
        child.parent = this
        return this
    }

    fun apply(consumer: Consumer<Container>): Container {
        consumer.accept(this)
        return this
    }

    fun setOnDoubleClick(onDoubleClick: Consumer<Map<String, Any>>): Container {
        this.onDoubleClick = onDoubleClick
        return this
    }

    fun setOnClick(onClick: Consumer<MutableMap<String, Any>>): Container {
        this.onClick = onClick
        return this
    }

    fun setOnDrag(onDrag: Consumer<Pair<MutableMap<String, Any>, Pair<Double, Double>>>): Container {
        this.onDrag = onDrag
        return this
    }

    fun setOnKey(onKey: Consumer<Pair<MutableMap<String, Any>, Key>>): Container {
        this.onKey = onKey
        return this
    }

    fun setOnInit(onInit: Consumer<Pair<Container, MutableMap<String, Any>>>): Container {
        this.onInit = onInit
        return this
    }

    fun setOnScroll(onScroll: Consumer<Pair<Double, MutableMap<String, Any>>>) {
        this.onScroll = onScroll
    }

    fun addOnUpdate(onUpdate: Consumer<MutableMap<String, Any>>): Container {
        this.onUpdate.add(onUpdate)
        return this
    }

    fun setScissor(scissor: Boolean): Container {
        this.scissor = scissor
        return this
    }

    fun clear(): Container {
        children.clear()
        return this
    }

    open inner class Runtime : Component.Runtime() {
        private var widthInternal = 0.0
        private var heightInternal = 0.0

        override val width: Double
            get() = widthInternal
        override val height: Double
            get() = heightInternal

        final override var padding = 0.0
            private set

        val placedComponents: MutableList<Pair<Component?, DoubleArray>> = ArrayList()
        private val placedComponents2: MutableList<Component> = ArrayList()
        private var prevClick: Long = 0
        private var heldClick = false

        override fun render(x: Double, y: Double, width: Double, height: Double) {
            placedComponents.clear()
            placedComponents2.clear()
            val container = this@Container
            this.x = x
            this.y = y
            this.widthInternal = width
            this.heightInternal = height
            if (!(getState("hasInit") as Boolean)) {
                if (onInit != null) {
                    val state = this.availableState
                    onInit!!.accept(Pair(this@Container, state))
                    this.availableState = state
                }
                setState("selected", false)
                setState("hasInit", true)
            }
            val state = this.availableState
            for (onUpdate in onUpdate) {
                onUpdate.accept(state)
            }
            this.availableState = state
            if (getState("hidden") as Boolean) {
                return
            }

            val renderer = AdapterManager.getAdapter().renderer
            if (container.backgroundImage != null) {
                var color = Color.WHITE
                if (container.topLeftBackgroundColor != null) {
                    color = container.topLeftBackgroundColor!!.getColorValue(this)
                }
                renderer.drawImage(
                    container.backgroundImage!!.getStringValue(this),
                    x - width / 2,
                    y - height / 2,
                    width,
                    height,
                    color
                )
            } else if (container.topLeftBackgroundColor != null) {
                renderer.drawRectangle(
                    x - width / 2,
                    y - height / 2,
                    width,
                    height,
                    container.backgroundCornerRadius.getCornerRadiusValue(this),
                    container.topLeftBackgroundColor!!.getColorValue(this),
                    container.bottomLeftBackgroundColor!!.getColorValue(this),
                    container.bottomRightBackgroundColor!!.getColorValue(this),
                    container.topRightBackgroundColor!!.getColorValue(this)
                )
            }
            placedComponents.add(Pair(null, doubleArrayOf(width / 2 + 0.5, 0.0, 1.0, height + 1, 0.0)))
            placedComponents.add(Pair(null, doubleArrayOf(-width / 2 - 0.5, 0.0, 1.0, height + 1, 0.0)))
            placedComponents.add(Pair(null, doubleArrayOf(0.0, height / 2 + 0.5, width + 1, 1.0, 0.0)))
            placedComponents.add(Pair(null, doubleArrayOf(0.0, -height / 2 - 0.5, width + 1, 1.0, 0.0)))

            if (scissor) {
                renderer.scissor(x - width / 2, y - height / 2, width, height)
            }

            for (child in getChildren()) {
                if (!(child.runtime.getState("hidden") as Boolean)) {
                    val wantedPosition = getOtherCalculatedPosition(child)
                    val childX = wantedPosition[0]
                    val childY = wantedPosition[1]
                    val childWidth = wantedPosition[2]
                    val childHeight = wantedPosition[3]
                    addPlacedComponents(child, wantedPosition)
                    placedComponents2.add(child)
                    renderChild(child.runtime, this.x + childX, this.y + childY, childWidth, childHeight)
                } else {
                    renderChild(child.runtime, -1.0, -1.0, -1.0, -1.0)
                }
            }

            if (scissor) {
                renderer.endScissor()
            }
        }

        protected open fun renderChild(
            childRuntime: Component.Runtime?,
            x: Double,
            y: Double,
            width: Double,
            height: Double
        ) {
            childRuntime!!.render(x, y, width, height)
        }

        override fun getParent(): Runtime? {
            return parent!!.runtime as Runtime
        }

        protected open fun getOtherCalculatedPosition(child: Component): DoubleArray {
            return child.runtime.calculatedPosition
        }

        protected open fun addPlacedComponents(child: Component?, wantedPosition: DoubleArray) {
            placedComponents.add(Pair(child, wantedPosition))
        }

        override val calculatedPosition: DoubleArray
            get() {
                this.padding = 0.0
                this.x = 0.0
                this.y = 0.0
                this.widthInternal = 0.0
                this.heightInternal = 0.0
                for (i in 0..2) {
                    this.padding = this@Container.padding.getPaddingValue(this)
                    this.x = this@Container.x.getXValue(this)
                    this.y = this@Container.y.getYValue(this)
                    this.widthInternal = this@Container.width.getWidthValue(this)
                    this.heightInternal = this@Container.height.getHeightValue(this)
                }
                return doubleArrayOf(this.x, this.y, this.width, this.height, this.padding)
            }

        override fun onStateUpdate(id: String, value: Any) {
            super.onStateUpdate(id, value)
            for (child in getChildren()) {
                child.runtime.onStateUpdate(id, value)
            }
        }

        override fun handleMouseEvent(event: MouseEvent): Boolean {
            val state = this.availableState
            if (event.isPressed && event.button === Button.PRIMARY) {
                state["selected"] = event.x > this.x - this.width / 2 && event.x < this.x + this.width / 2 && event.y > this.y - this.height / 2 && event.y < this.y + this.height / 2
            }
            var handled = false
            for (component in placedComponents2) {
                handled = component.runtime.handleMouseEvent(event)
                if (handled) {
                    return true
                }
            }
            if (event.x > this.x - this.width / 2 && event.x < this.x + this.width / 2 && event.y > this.y - this.height / 2 && event.y < this.y + this.height / 2) {
                if (event.wheel != 0.0 && onScroll != null) {
                    onScroll!!.accept(Pair(event.wheel, state))
                }
            }
            if (event.isPressed && event.button === Button.PRIMARY) {
                if (event.x > this.x - this.width / 2 && event.x < this.x + this.width / 2 && event.y > this.y - this.height / 2 && event.y < this.y + this.height / 2) {
                    if (onClick != null) {
                        handled = true
                        onClick!!.accept(state)
                    }
                    heldClick = true
                    if (System.currentTimeMillis() - prevClick < 400) {
                        if (onDoubleClick != null) {
                            handled = true
                            onDoubleClick!!.accept(state)
                        }
                    }
                    prevClick = System.currentTimeMillis()
                    state["selected"] = true
                } else {
                    state["selected"] = false
                }
            } else if (!event.isPressed && event.button === Button.PRIMARY) {
                heldClick = false
            }
            if (heldClick) {
                if (onDrag != null) {
                    handled = true
                    onDrag!!.accept(
                        Pair(
                            state,
                            Pair(
                                ((event.x - (this.x - this.width / 2)) / this.width).coerceIn(0.0..1.0),
                                ((event.y - (this.y - this.height / 2)) / this.height).coerceIn(0.0..1.0)
                            )
                        )
                    )
                }
            }
            this.availableState = state
            return handled
        }

        override fun handleKeyEvent(event: KeyEvent) {
            if (getState("selected") as Boolean) {
                if (onKey != null) {
                    val state = this.availableState
                    onKey!!.accept(Pair(state, event.key))
                    this.availableState = state
                }
            }
            for (component in placedComponents2) {
                component.runtime.handleKeyEvent(event)
            }
        }

        override fun setHasInit(hasInit: Boolean) {
            super.setHasInit(hasInit)
            if (!hasInit) {
                for (component in getChildren()) {
                    component.runtime.setHasInit(false)
                }
            }
        }

        open fun getChildren(): List<Component> {
            return children
        }
    }
}