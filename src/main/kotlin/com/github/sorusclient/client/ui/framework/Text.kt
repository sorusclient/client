package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.KeyCharEvent
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.ui.framework.constraint.Absolute
import com.github.sorusclient.client.ui.framework.constraint.Constraint
import com.github.sorusclient.client.util.Color
import kotlin.math.max

class Text : Component() {

    var paddingLeft: Constraint = Absolute(0.0)
    var paddingRight: Constraint = Absolute(0.0)
    var paddingTop: Constraint = Absolute(0.0)
    var paddingBottom: Constraint = Absolute(0.0)

    var fontRenderer: Constraint? = null
        set(value) {
            value?.getStringValue(null)?.let { AdapterManager.getAdapter().renderer.createFont(it) }
            field = value
        }
    var text: Constraint? = null
    var scale: Constraint = Absolute(1.0)
    var textColor: Constraint = Absolute(Color.WHITE)

    init {
        runtime = Runtime()
    }

    fun setPadding(padding: Constraint) {
        paddingLeft = padding
        paddingRight = padding
        paddingBottom = padding
        paddingTop = padding
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

        override var leftPadding = 0.0
            private set
        override var rightPadding = 0.0
            private set
        override var topPadding = 0.0
            private set
        override var bottomPadding = 0.0
            private set

        override fun render(x: Double, y: Double, width: Double, height: Double) {
            if (cachedFontId != fontRenderer!!.getStringValue(this)) {
                cachedFontId = fontRenderer!!.getStringValue(this)
                AdapterManager.getAdapter().renderer.createFont(cachedFontId!!)
            }

            AdapterManager.getAdapter().renderer.drawText(
                    cachedFontId!!,
                    text!!.getStringValue(this)!!,
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
                this.leftPadding = 0.0
                this.rightPadding = 0.0
                this.topPadding = 0.0
                this.bottomPadding = 0.0

                this.x = 0.0
                this.y = 0.0
                for (i in 0..2) {
                    this.leftPadding = this@Text.paddingLeft.getPaddingValue(this)
                    this.rightPadding = this@Text.paddingRight.getPaddingValue(this)
                    this.topPadding = this@Text.paddingTop.getPaddingValue(this)
                    this.bottomPadding = this@Text.paddingBottom.getPaddingValue(this)

                    this.x = this@Text.x.getXValue(this)
                    this.y = this@Text.y.getYValue(this)
                }
                return doubleArrayOf(this.x, this.y, this.width, this.height, this.leftPadding, this.rightPadding, this.topPadding, this.bottomPadding)
            }

        override val width: Double
            get() {
                if (cachedFontId != fontRenderer!!.getStringValue(this)) {
                    cachedFontId = fontRenderer!!.getStringValue(this)
                    AdapterManager.getAdapter().renderer.createFont(cachedFontId!!)
                }
                return max(0.01, AdapterManager.getAdapter().renderer.getTextWidth(cachedFontId!!, text!!.getStringValue(this)!!) * scale.getPaddingValue(this))
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

        override fun handleKeyEvent(event: KeyEvent): Boolean {
            return false
        }

        override fun handleKeyCharEvent(event: KeyCharEvent): Boolean {
            return false
        }
    }
}