package org.nikok.jumpnrun.util

class Cycler(var frameRate: Int, private val action: () -> Unit) {
    private var elapsedSinceLast = 0L

    fun elapsed(deltaTime: Long) {
        elapsedSinceLast += deltaTime
        while (elapsedSinceLast >= frameRate) {
            action()
            elapsedSinceLast -= frameRate
        }
    }
}