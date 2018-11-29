package org.nikok.jumpnrun.graphic

import android.content.Context
import kotlin.math.pow

class Circle() : GameElement() {
    constructor(r: Float) : this() {
        radius = r
    }

    constructor(r: Float, cx: Float, cy: Float) : this(r) {
        this.cx = cx
        this.cy = cy
    }

    override fun contains(point: Point): Boolean {
        val (px, py) = point
        val cx = radius + x
        val cy = radius + y
        return (px - cx).pow(2) + (py - cy).pow(2) < radius.pow(2)
    }

    var cx
        get() = x + radius
        set(value) {
            x = value - radius
        }
    var cy
        get() = y + radius
        set(value) {
            y = value - radius
        }

    var center
        get() = Point(cx, cy)
        set(c) {
            cx = c.x
            cy = c.y
        }

    @Transient
    override var view: CircleView? = null
        private set

    var radius = 0.0f
        set(value) {
            field = value
            view?.invalidate()
        }

    override val width: Float
        get() = radius * 2

    override val height: Float
        get() = radius * 2

    override fun createView(context: Context): GameElementView {
        val v = CircleView(context, this, paint)
        v.x = x
        v.y = y
        view = v
        return v
    }
}