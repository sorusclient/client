package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.event.KeyCharEvent
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.ui.framework.constraint.Constraint
import com.github.sorusclient.client.ui.framework.constraint.Flexible
import com.github.sorusclient.client.ui.framework.constraint.Side
import java.util.function.Consumer

abstract class Component {
    var x: Constraint = Side(Side.ZERO)
    var y: Constraint = Side(Side.ZERO)
    var width: Constraint = Flexible()
    var height: Constraint = Flexible()
    lateinit var runtime: Runtime
    var parent: Container? = null
    val storedState: MutableList<String> = ArrayList()
    val onStateUpdate: MutableMap<String, (MutableMap<String, Any>) -> Unit> = HashMap()

    init {
        storedState += "selected"
        storedState += "hasInit"
        storedState += "hidden"
        storedState += "clicked"
        storedState += "hovered"
        storedState += "interacted"
    }

    abstract inner class Runtime {
        val state: MutableMap<String, Any> = HashMap()
        abstract fun render(x: Double, y: Double, width: Double, height: Double)
        abstract fun getParent(): Container.Runtime?
        abstract val calculatedPosition: DoubleArray

        var x: Double = 0.0
        var y: Double = 0.0

        abstract val leftPadding: Double
        abstract val rightPadding: Double
        abstract val topPadding: Double
        abstract val bottomPadding: Double

        abstract val width: Double
        abstract val height: Double

        init {
            setState("hasInit", false)
            setState("hidden", false)
            setState("clicked", false)
            setState("hovered", false)
            setState("selected", false)
            setState("interacted", false)
        }

        abstract fun onInit()

        fun setState(id: String, value: Any) {
            if (storedState.contains(id)) {
                state[id] = value
                onStateUpdate(id, value)
            } else {
                getParent()!!.setState(id, value)
            }
        }

        open fun onStateUpdate(id: String, value: Any) {
            val onStateUpdate = onStateUpdate[id]
            if (onStateUpdate != null) {
                val state = availableState
                onStateUpdate(state)
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

        open fun onClose() {

        }

        abstract fun handleMouseEvent(event: MouseEvent): Boolean
        abstract fun handleKeyEvent(event: KeyEvent): Boolean
        abstract fun handleKeyCharEvent(event: KeyCharEvent): Boolean
    }
}