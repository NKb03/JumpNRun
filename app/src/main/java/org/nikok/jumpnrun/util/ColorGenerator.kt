package org.nikok.jumpnrun.util

import android.graphics.Color
import java.io.Serializable
import java.util.concurrent.ThreadLocalRandom

class ColorGenerator(colorPoolSize: Int) : Serializable {
    init {
        require(colorPoolSize > 0) { "Non positive color pool size" }
    }

    private val colors by lazy { List(colorPoolSize) { createRandomColor() } }

    fun randomColor() = colors.randomElement()

    companion object {
        private fun createRandomColor(): Int {
            val random = ThreadLocalRandom.current()
            return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255))
        }
    }
}