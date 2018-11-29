package org.nikok.jumpnrun.games

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import org.nikok.jumpnrun.core.EventHandler
import org.nikok.jumpnrun.core.Game
import org.nikok.jumpnrun.core.GameResult
import org.nikok.jumpnrun.graphic.Circle
import org.nikok.jumpnrun.graphic.GameElement
import org.nikok.jumpnrun.graphic.Point
import org.nikok.jumpnrun.util.Cycler

class DummyGame() : Game<DummyGame.Result>(), EventHandler {
    private var points: Int = 0

    private val circle = Circle().apply {
        modifyPaint {
            color = Color.RED
        }
    }

    override val result: Result get() = Result(points)

    constructor(parcel: Parcel) : this() {
        points = parcel.readInt()
    }

    data class Result(val points: Int) : GameResult(DummyGame::class)

    private var circlePos = 1

    override fun onSwipeLeft(from: Point, to: Point, velocityX: Float, velocityY: Float, startElement: GameElement?) {
        if (circlePos == 0) {
            return
        }
        circlePos--
        relocateCircle()
        points++
    }

    override fun onSwipeRight(from: Point, to: Point, velocityX: Float, velocityY: Float, startElement: GameElement?) {
        if (circlePos == 2) {
            lg("Circle already most right")
            return
        }
        circlePos++
        lg("Incrementing circle pos to $circlePos")
        relocateCircle()
        points++
    }

    override fun onResume() {
        addElement(circle)
        val r = totalWidth / 6
        circle.radius = r
        circle.y = top
        relocateCircle()
        val cycler = Cycler(1) {
            circle.modifyPaint {
                this.alpha++
            }

        }
        addCycler(cycler)
        addEventHandler(this)
    }

    private fun relocateCircle() {
        circle.x = totalWidth / 3 * circlePos
    }

    companion object CREATOR : Parcelable.Creator<DummyGame> {
        override fun createFromParcel(parcel: Parcel): DummyGame {
            return DummyGame(parcel)
        }

        override fun newArray(size: Int): Array<DummyGame?> {
            return arrayOfNulls(size)
        }

        private val TAG = DummyGame::class.qualifiedName!!
        private fun lg(msg: String) {
            Log.v(TAG, msg)
        }
    }
}