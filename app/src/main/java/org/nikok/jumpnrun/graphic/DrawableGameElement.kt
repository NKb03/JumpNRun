package org.nikok.jumpnrun.graphic

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import org.nikok.jumpnrun.util.transient

class DrawableGameElement(@DrawableRes drawableId: Int, resources: Resources) : GameElement() {
    val drawable: Drawable by transient { resources.getDrawable(drawableId) }

    private fun updateBounds() {
        drawable.setBounds(0, 0, width.toInt(), height.toInt())
    }

    override var width: Float = drawable.intrinsicWidth.toFloat()
        set(value) {
            field = value
            this.updateBounds()
        }

    override var height: Float = drawable.intrinsicWidth.toFloat()
        set(value) {
            field = value
            this.updateBounds()
        }
    override var view: GameElementView? = null

    override fun createView(context: Context): GameElementView {
        val v = DrawableGameElementView(context, this)
        v.x = x
        v.y = y
        view = v
        return v
    }

    override var x: Float
        get() = super.x
        set(value) {
            super.x = value
            this.updateBounds()
        }

    override var y: Float
        get() = super.y
        set(value) {
            super.y = value
            this.updateBounds()
        }

    override fun contains(point: Point): Boolean {
        val (px, py) = point
        return px in x..x + width && py in y..y + height
    }
}