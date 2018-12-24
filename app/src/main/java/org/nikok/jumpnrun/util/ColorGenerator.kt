package org.nikok.jumpnrun.util

import android.graphics.Color
import java.io.Serializable

class ColorGenerator : Serializable {
    fun randomColor() = colors.randomElement()

    companion object {
        private val colors = listOf(
            Color.parseColor("#0037FF"),
            Color.parseColor("#4BFF00"),
            Color.parseColor("#FF000C"),
            Color.parseColor("#9538FF"),
            Color.parseColor("#FF7D20"),
            Color.parseColor("#FDFF00")
        )
    }
}