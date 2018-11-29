package org.nikok.jumpnrun.graphic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas

@SuppressLint("ViewConstructor")
internal class DrawableGameElementView(context: Context, override val element: DrawableGameElement) :
    GameElementView(context) {
    override fun onDrawForeground(canvas: Canvas) {
        element.drawable.draw(canvas)
    }
}