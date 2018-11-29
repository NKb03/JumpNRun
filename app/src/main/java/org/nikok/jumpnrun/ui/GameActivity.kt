package org.nikok.jumpnrun.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.os.Bundle
import org.nikok.jumpnrun.core.Game
import org.nikok.jumpnrun.core.JumpNRun

sealed class GameActivity : Activity() {
    private val game: Game<*> by serializableExtra(GAME)

    private val jumpNRun: JumpNRun by parcelableExtra(JUMPNRUN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display = windowManager.defaultDisplay
        val p = Point()
        display.getSize(p)
        val gameView = game.resume(this, jumpNRun, p.x.toFloat(), p.y.toFloat())
        setContentView(gameView)
    }

    private fun askIfQuitGame() {
        game.pauseLoop()
        showAlert {
            setTitle("Quit Game?")
            setMessage("Do you really want to quit this game?")
            setNegativeButton("Do not quit") { _, _ ->
                game.startLoop()
            }
            setPositiveButton("Do quit") { _, _ ->
                game.finish()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        askIfQuitGame()
    }

    override fun onStop() {
        super.onStop()
        game.pauseLoop()
    }

    override fun onResume() {
        super.onResume()
        game.startLoop()
    }

    class Portrait : GameActivity()
    class Landscape : GameActivity()

    companion object {
        const val GAME = "GAME"

        const val JUMPNRUN = "JUMPNRUN"

        fun withScreenOrientation(orientation: Int): Class<out GameActivity> = when (orientation) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> Landscape::class.java
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> Portrait::class.java
            else -> throw IllegalArgumentException("No such screen orientation $orientation")
        }
    }
}