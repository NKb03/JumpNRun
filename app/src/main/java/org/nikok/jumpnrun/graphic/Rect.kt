package org.nikok.jumpnrun.graphic

import android.content.Context

class Rect : GameElement {
    constructor(w: Float, h: Float) : this() {
        width = w
        height = h
    }

    override fun contains(point: Point): Boolean {
        val (px, py) = point
        return px in x..x + width && py in y..y + height
    }

    @Transient
    override var view: RectView? = null
        private set

    constructor() : super()

    override var width = 0.0f
        set(value) {
            field = value
            view?.invalidate()
        }
    override var height = 0.0f
        set(value) {
            field = value
            view?.invalidate()
        }

    override fun createView(context: Context): GameElementView {
        val v = RectView(context, this, paint)
        v.x = x
        v.y = y
        view = v
        return v
    }

    override fun toString(): String = buildString {
        append("Rectangle(")
        append("x=$x")
        append("y=$y")
        append("width=$width")
        append("height=$height")
        append(")")
    }
}