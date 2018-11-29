package org.nikok.jumpnrun.core

import org.nikok.jumpnrun.graphic.GameElement
import org.nikok.jumpnrun.graphic.Point

interface EventHandler {
    fun onTouch(action: Int, point: Point, element: GameElement?) {}

    fun onLongClick(element: GameElement?) {}

    fun onClick(element: GameElement?) {}

    fun onSwipeDown(
        from: Point,
        to: Point,
        velocityX: Float,
        velocityY: Float,
        startElement: GameElement?
    ) {
    }

    fun onSwipeUp(
        from: Point,
        to: Point,
        velocityX: Float,
        velocityY: Float,
        startElement: GameElement?
    ) {
    }

    fun onSwipeLeft(
        from: Point,
        to: Point,
        velocityX: Float,
        velocityY: Float,
        startElement: GameElement?
    ) {
    }

    fun onSwipeRight(
        from: Point,
        to: Point,
        velocityX: Float,
        velocityY: Float,
        startElement: GameElement?
    ) {
    }

}