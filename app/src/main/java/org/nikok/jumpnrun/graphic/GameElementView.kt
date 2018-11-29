package org.nikok.jumpnrun.graphic

import android.content.Context
import android.view.View

internal abstract class GameElementView(context: Context) : View(context) {
    abstract val element: GameElement
}