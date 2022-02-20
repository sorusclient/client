package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.ui.framework.constraint.Absolute
import com.github.sorusclient.client.ui.framework.constraint.Constraint
import com.github.sorusclient.client.util.Color

class Text : Component() {
    var padding: Constraint = Absolute(0.0)
    var fontRenderer: Constraint? = null
        set(value) {
            AdapterManager.getAdapter().renderer.createFont(value!!.getStringValue(null))
            field = value
        }
    var text: Constraint? = null
    var scale: Constraint = Absolute(1.0)
    var textColor: Constraint = Absolute(Color.WHITE)

    init {
        runtime = Runtime()
    }

    fun setPadding(padding: Constraint): Text {
        this.padding = padding
        return this
    }

    fun setTextColor(textColor: Constraint): Text {
        this.textColor = textColor
        return this
    }

    fun setFontRenderer(fontRenderer: Constraint?): Text {
        this.fontRenderer = fontRenderer
        return this
    }

    fun setText(text: Constraint?): Text {
        this.text = text
        return this
    }

    fun setScale(scale: Constraint): Text {
        this.scale = scale
        return this
    }

    inner class Runtime : Component.Runtime() {

        var cachedFontId: String? = null

        override var padding = 0.0
            private set

        override fun render(x: Double, y: Double, width: Double, height: Double) {
            if (cachedFontId != fontRenderer!!.getStringValue(this)) {
                cachedFontId = fontRenderer!!.getStringValue(this)
                AdapterManager.getAdapter().renderer.createFont(cachedFontId!!)
            }

            AdapterManager.getAdapter().renderer.drawText(
                    cachedFontId!!,
                    text!!.getStringValue(this),
                    x - this.width / 2,
                    y - this.height / 2,
                    scale.getPaddingValue(this),
                    textColor.getColorValue(this)
            )
        }

        override fun onInit() {

        }

        override fun getParent(): Container.Runtime {
            return parent!!.runtime as Container.Runtime
        }

        override val calculatedPosition: DoubleArray
            get() {
                this.padding = 0.0
                this.x = 0.0
                this.y = 0.0
                for (i in 0..2) {
                    this.padding = this@Text.padding.getPaddingValue(this)
                    this.x = this@Text.x.getXValue(this)
                    this.y = this@Text.y.getYValue(this)
                }
                return doubleArrayOf(this.x, this.y, width, height, this.padding)
            }

        override val width: Double
            get() {
                if (cachedFontId != fontRenderer!!.getStringValue(this)) {
                    cachedFontId = fontRenderer!!.getStringValue(this)
                    AdapterManager.getAdapter().renderer.createFont(cachedFontId!!)
                }
                return AdapterManager.getAdapter().renderer.getTextWidth(cachedFontId!!, text!!.getStringValue(this)) * scale.getPaddingValue(this)
            }

        override val height: Double
            get() {
                if (cachedFontId != fontRenderer!!.getStringValue(this)) {
                    cachedFontId = fontRenderer!!.getStringValue(this)
                    AdapterManager.getAdapter().renderer.createFont(cachedFontId!!)
                }
                return AdapterManager.getAdapter().renderer.getTextHeight(cachedFontId!!) * scale.getPaddingValue(this)
            }

        override fun handleMouseEvent(event: MouseEvent): Boolean {
            return false
        }

        override fun handleKeyEvent(event: KeyEvent) {}
    }
}