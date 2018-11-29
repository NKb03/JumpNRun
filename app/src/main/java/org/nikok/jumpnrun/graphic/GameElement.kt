package org.nikok.jumpnrun.graphic

import android.content.Context
import android.graphics.Paint
import android.view.animation.Animation
import org.nikok.jumpnrun.util.transient
import java.io.Serializable

abstract class GameElement internal constructor() : Serializable {
    protected val paint by transient { Paint() }

    abstract val width: Float

    abstract val height: Float

    fun modifyPaint(action: Paint.() -> Unit) {
        paint.action()
        view?.invalidate()
    }

    var color
        get() = paint.color
        set(value) {
            paint.color = value
            view?.invalidate()
        }

    internal abstract val view: GameElementView?

    internal abstract fun createView(context: Context): GameElementView

    abstract operator fun contains(point: Point): Boolean

    fun startAnimation(animation: Animation) {
        view?.startAnimation(animation)
    }

    open var x = 0.0f
        set(value) {
            field = value
            view?.x = value
        }

    open var y = 0.0f
        set(value) {
            field = value
            view?.y = value
        }
}