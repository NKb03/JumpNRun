package org.nikok.jumpnrun.graphic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

@SuppressLint("ViewConstructor")
internal class RectView(
    context: Context,
    override val element: Rect, private val paint: Paint
) :
    GameElementView(context) {

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0.0f, 0.0f, element.width, element.height, paint)
    }
}