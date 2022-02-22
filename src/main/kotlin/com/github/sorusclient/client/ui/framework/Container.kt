package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.ui.framework.constraint.Absolute
import com.github.sorusclient.client.ui.framework.constraint.Constraint
import com.github.sorusclient.client.ui.toAbsolute
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.Pair
import java.util.function.Consumer
import kotlin.collections.List

open class Container : Component() {
    var backgroundCornerRadius: Constraint = Absolute(0.0)

    var backgroundColor: Constraint? = null
    var topLeftBackgroundColor: Constraint? = null
    var bottomLeftBackgroundColor: Constraint? = null
    var bottomRightBackgroundColor: Constraint? = null
    var topRightBackgroundColor: Constraint? = null

    var borderThickness: Constraint? = 1.0.toAbsolute()
    var borderColor: Constraint? = null

    var backgroundImage: Constraint? = null
        set(value) {
            AdapterManager.getAdapter().renderer.createTexture(value!!.getStringValue(null))
            field = value
        }
    var paddingLeft: Constraint = Absolute(0.0)
    var paddingRight: Constraint = Absolute(0.0)
    var paddingTop: Constraint = Absolute(0.0)
    var paddingBottom: Constraint = Absolute(0.0)

    val children: MutableList<Component> = ArrayList()
    var onClick: ((MutableMap<String, Any>) -> Unit)? = null
    private var onDoubleClick: Consumer<Map<String, Any>>? = null
    var onDrag: ((Pair<MutableMap<String, Any>, Pair<Double, Double>>) -> Unit)? = null
    var onKey: ((Pair<MutableMap<String, Any>, Key>) -> Unit)? = null
    val onInit: MutableList<((Pair<Container, MutableMap<String, Any>>) -> Unit)> = ArrayList()
    var onScroll: ((Pair<Double, MutableMap<String, Any>>) -> Unit)? = null
    val onUpdate: MutableList<(MutableMap<String, Any>) -> Unit> = ArrayList()
    val onClose: MutableList<(MutableMap<String, Any>) -> Unit> = ArrayList()
    var scissor = false
    var consumeClicks = true

    var selectedByDefault = false

    init {
        runtime = Runtime()

        onInit += { state ->
            state.second["hovered"] = false
            state.second["selected"] = selectedByDefault
        }
    }

    fun setPadding(padding: Constraint) {
        paddingLeft = padding
        paddingRight = padding
        paddingBottom = padding
        paddingTop = padding
    }

    open fun addChild(child: Component): Container {
        children.add(child)
        child.parent = this
        return this
    }

    fun clear(): Container {
        children.clear()
        return this
    }

    fun clearAfter(startIndex: Int): Container {
        for (i in startIndex until children.size) {
            children.removeAt(startIndex)
        }
        return this
    }

    open inner class Runtime : Component.Runtime() {
        private var widthInternal = 0.0
        private var heightInternal = 0.0

        override val width: Double
            get() = widthInternal
        override val height: Double
            get() = heightInternal

        final override var leftPadding = 0.0
            private set

        final override var rightPadding = 0.0
            private set

        final override var topPadding = 0.0
            private set

        final override var bottomPadding = 0.0
            private set

        val placedComponents: MutableList<Pair<Component?, DoubleArray>> = ArrayList()
        private val placedComponents2: MutableList<Component> = ArrayList()
        private var prevClick: Long = 0
        private var heldClick = false

        override fun onInit() {
            for (onInit in this@Container.onInit) {
                val state = this.availableState
                onInit(Pair(this@Container, state))
                this.availableState = state
            }

            for (component in getChildren()) {
                component.parent = this@Container
                component.runtime.onInit()
            }
        }

        override fun render(x: Double, y: Double, width: Double, height: Double) {
            placedComponents.clear()
            placedComponents2.clear()
            val container = this@Container
            this.x = x
            this.y = y
            this.widthInternal = width
            this.heightInternal = height

            if (!(getState("hasInit") as Boolean)) {
                onInit()

                setState("hasInit", true)
            }

            val state = this.availableState
            for (onUpdate in onUpdate) {
                onUpdate(state)
            }
            this.availableState = state
            if (getState("hidden") as Boolean) {
                setState("clicked", false)
                setState("hovered", false)
                setState("selected", false)
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
                    container.backgroundCornerRadius.getCornerRadiusValue(this),
                    true,
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
            } else if (container.backgroundColor != null) {
                renderer.drawRectangle(
                    x - width / 2,
                    y - height / 2,
                    width,
                    height,
                    container.backgroundCornerRadius.getCornerRadiusValue(this),
                    container.backgroundColor!!.getColorValue(this)
                )
            }

            if (container.borderColor != null) {
                renderer.drawRectangleBorder(x - width / 2, y - height / 2, width, height, container.backgroundCornerRadius.getCornerRadiusValue(this), container.borderThickness!!.getPaddingValue(this), container.borderColor!!.getColorValue(this))
            }

            placedComponents.add(Pair(null, doubleArrayOf(width / 2 + 0.5, 0.0, 1.0, height + 1, 0.0, 0.0, 0.0, 0.0)))
            placedComponents.add(Pair(null, doubleArrayOf(-width / 2 - 0.5, 0.0, 1.0, height + 1, 0.0, 0.0, 0.0, 0.0)))
            placedComponents.add(Pair(null, doubleArrayOf(0.0, height / 2 + 0.5, width + 1, 1.0, 0.0, 0.0, 0.0, 0.0)))
            placedComponents.add(Pair(null, doubleArrayOf(0.0, -height / 2 - 0.5, width + 1, 1.0, 0.0, 0.0, 0.0, 0.0)))

            if (scissor) {
                renderer.scissor(x - width / 2, y - height / 2, width, height)
            }

            for (child in getChildren()) {
                if (!(child.runtime.getState("hidden") as Boolean)) {
                    child.parent = this@Container
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
                this.leftPadding = 0.0
                this.rightPadding = 0.0
                this.topPadding = 0.0
                this.bottomPadding = 0.0

                this.x = 0.0
                this.y = 0.0
                this.widthInternal = 0.0
                this.heightInternal = 0.0
                for (i in 0..2) {
                    this.leftPadding = this@Container.paddingLeft.getPaddingValue(this)
                    this.rightPadding = this@Container.paddingRight.getPaddingValue(this)
                    this.topPadding = this@Container.paddingTop.getPaddingValue(this)
                    this.bottomPadding = this@Container.paddingBottom.getPaddingValue(this)

                    this.x = this@Container.x.getXValue(this)
                    this.y = this@Container.y.getYValue(this)
                    this.widthInternal = this@Container.width.getWidthValue(this)
                    this.heightInternal = this@Container.height.getHeightValue(this)
                }
                return doubleArrayOf(this.x, this.y, this.width, this.height, this.leftPadding, this.rightPadding, this.topPadding, this.bottomPadding)
            }

        override fun onStateUpdate(id: String, value: Any) {
            super.onStateUpdate(id, value)
            for (child in getChildren()) {
                child.runtime.onStateUpdate(id, value)
            }
        }

        override fun handleMouseEvent(event: MouseEvent): Boolean {
            var handled = false
            for (component in placedComponents2) {
                handled = component.runtime.handleMouseEvent(event)
                if (handled) {
                    return true
                }
            }

            val state = this.availableState
            if (event.isPressed && event.button === Button.PRIMARY) {
                state["selected"] = event.x > this.x - this.width / 2 && event.x < this.x + this.width / 2 && event.y > this.y - this.height / 2 && event.y < this.y + this.height / 2
            }

            if (event.x > this.x - this.width / 2 && event.x < this.x + this.width / 2 && event.y > this.y - this.height / 2 && event.y < this.y + this.height / 2) {
                if (event.wheel != 0.0 && onScroll != null) {
                    onScroll!!(Pair(event.wheel, state))
                }

                state["hovered"] = true
            } else {
                state["hovered"] = false
            }

            if (event.isPressed && event.button === Button.PRIMARY) {
                if (event.x > this.x - this.width / 2 && event.x < this.x + this.width / 2 && event.y > this.y - this.height / 2 && event.y < this.y + this.height / 2) {
                    if (onClick != null) {
                        if (consumeClicks) {
                            handled = true
                        }
                        onClick!!(state)
                    }
                    heldClick = true
                    if (System.currentTimeMillis() - prevClick < 400) {
                        if (onDoubleClick != null) {
                            if (consumeClicks) {
                                handled = true
                            }
                            onDoubleClick!!.accept(state)
                        }
                    }
                    prevClick = System.currentTimeMillis()
                    state["selected"] = true
                    state["clicked"] = true
                } else {
                    state["selected"] = false
                }
            } else if (!event.isPressed && event.button === Button.PRIMARY) {
                heldClick = false
                state["clicked"] = false
            }

            if (heldClick) {
                if (onDrag != null) {
                    if (consumeClicks) {
                        handled = true
                    }
                    onDrag!!(
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

            state["interacted"] = heldClick

            this.availableState = state
            return handled
        }

        override fun handleKeyEvent(event: KeyEvent) {
            if (getState("selected") as Boolean && event.isPressed) {
                if (onKey != null) {
                    val state = this.availableState
                    onKey!!(Pair(state, event.key))
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

        override fun onClose() {
            val availableState = availableState
            for (onClose in onClose) {
                onClose(availableState)
            }

            this.availableState = availableState

            for (child in children) {
                child.runtime.onClose()
            }
        }

    }
}