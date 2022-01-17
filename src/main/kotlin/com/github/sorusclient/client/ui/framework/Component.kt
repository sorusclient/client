package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.ui.framework.constraint.Constraint
import com.github.sorusclient.client.ui.framework.constraint.Flexible
import com.github.sorusclient.client.ui.framework.constraint.Side
import java.util.function.Consumer

abstract class Component {
    protected var x: Constraint = Side(Side.ZERO)
    protected var y: Constraint = Side(Side.ZERO)
    protected var width: Constraint = Flexible()
    protected var height: Constraint = Flexible()
    lateinit var runtime: Runtime
    var parent: Container? = null
    private val storedState: MutableList<String> = ArrayList()
    private val onStateUpdates: MutableMap<String, Consumer<MutableMap<String, Any>>> = HashMap()

    init {
        addStoredState("selected")
        addStoredState("hasInit")
        addStoredState("hidden")
    }

    open fun setX(x: Constraint): Component {
        this.x = x
        return this
    }

    open fun setY(y: Constraint): Component {
        this.y = y
        return this
    }

    open fun setWidth(width: Constraint): Component {
        this.width = width
        return this
    }

    open fun setHeight(height: Constraint): Component {
        this.height = height
        return this
    }

    fun addStoredState(storedState: String): Component {
        this.storedState.add(storedState)
        return this
    }

    fun addOnStateUpdate(state: String, onStateUpdate: Consumer<MutableMap<String, Any>>): Component {
        onStateUpdates[state] = onStateUpdate
        return this
    }

    abstract inner class Runtime {
        val state: MutableMap<String, Any> = HashMap()
        abstract fun render(x: Double, y: Double, width: Double, height: Double)
        abstract fun getParent(): Container.Runtime?
        abstract val calculatedPosition: DoubleArray

        var x: Double = 0.0
        var y: Double = 0.0

        abstract val padding: Double

        abstract val width: Double
        abstract val height: Double

        init {
            setState("hasInit", false)
            setState("hidden", false)
        }

        fun setState(id: String, value: Any) {
            if (storedState.contains(id)) {
                state[id] = value
                onStateUpdate(id, value)
            } else {
                getParent()!!.setState(id, value)
            }
        }

        open fun onStateUpdate(id: String, value: Any) {
            val onStateUpdate = onStateUpdates[id]
            if (onStateUpdate != null) {
                val state = availableState
                onStateUpdate.accept(state)
                availableState = state
            }
        }

        fun getState(id: String?): Any? {
            return if (storedState.contains(id)) {
                state[id]
            } else {
                if (parent != null) {
                    getParent()!!.getState(id)
                } else {
                    null
                }
            }
        }

        var availableState: MutableMap<String, Any>
            get() {
                val availableState: MutableMap<String, Any> = HashMap()
                if (parent != null) {
                    availableState.putAll(parent!!.runtime.availableState)
                }
                availableState.putAll(state)
                return availableState
            }
            set(state) {
                for ((key, value) in state) {
                    if (value != getState(key)) {
                        setState(key, value)
                    }
                }
            }

        open fun setHasInit(hasInit: Boolean) {
            setState("hasInit", hasInit)
        }

        abstract fun handleMouseEvent(event: MouseEvent): Boolean
        abstract fun handleKeyEvent(event: KeyEvent)
    }
}