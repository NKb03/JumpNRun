package org.nikok.jumpnrun.graphic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

@SuppressLint("ViewConstructor")
internal class CircleView(context: Context, override val element: Circle, private val paint: Paint) :
    GameElementView(context) {

    override fun onDraw(canvas: Canvas) {
        val radius = element.radius
        canvas.drawCircle(radius, radius, radius, paint)
    }
}