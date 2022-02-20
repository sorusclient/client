package com.github.sorusclient.client.ui.framework

open class List(private val type: Int) : Container() {
    var columns = 0

    init {
        if (type == VERTICAL) {
            columns = 1
        } else if (type == HORIZONTAL) {
            columns = 9999
        }
        runtime = Runtime()
    }

    override fun addChild(child: Component): List {
        return super.addChild(child) as List
    }

    fun setColumns(columns: Int): List {
        this.columns = columns
        return this
    }

    open inner class Runtime : Container.Runtime() {
        private var xIndex = 0
        private var xLocation = 0.0
        protected var yLocation = 0.0
        override fun render(x: Double, y: Double, width: Double, height: Double) {
            xIndex = 0
            xLocation = -width / 2
            yLocation = -height / 2
            super.render(x, y, width, height)
        }

        override fun getOtherCalculatedPosition(child: Component): DoubleArray {
            val array = super.getOtherCalculatedPosition(child)
            val childRuntime = child.runtime
            xIndex++
            if (type and HORIZONTAL != 0) {
                val position = xLocation + childRuntime.width / 2 + childRuntime.leftPadding
                array[0] = position
                xLocation += childRuntime.width + childRuntime.leftPadding
                if (xIndex >= columns) {
                    xLocation = -this.width / 2
                }
            }
            if (type and VERTICAL != 0) {
                val position = yLocation + childRuntime.height / 2 + childRuntime.topPadding
                array[1] = position
                if (xIndex >= columns) {
                    yLocation += childRuntime.height + childRuntime.topPadding
                }
            }
            if (xIndex >= columns) {
                xIndex = 0
            }

            return array
        }

        override fun addPlacedComponents(child: Component?, wantedPosition: DoubleArray) {}
    }

    companion object {
        const val VERTICAL = 1
        const val HORIZONTAL = 2
        const val GRID = 1 or 2
    }
}