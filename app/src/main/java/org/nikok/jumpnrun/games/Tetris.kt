package org.nikok.jumpnrun.games

import android.content.Context
import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import org.nikok.jumpnrun.core.Game
import org.nikok.jumpnrun.core.GameResult
import org.nikok.jumpnrun.graphic.GameElement
import org.nikok.jumpnrun.graphic.GameElementView
import org.nikok.jumpnrun.graphic.Point
import org.nikok.jumpnrun.graphic.Rect

class Tetris : Game<Tetris.Result>() {
    private var points = 0

    override val result: Result
        get() = Result(points)

    override fun onResume() {

    }

    data class Result(val points: Int) : GameResult(Tetris::class, credits = points)


    private class Tetromino private constructor(private val matrix: Array<Array<Boolean>>) : Parcelable {
        @Suppress("UNCHECKED_CAST")
        constructor(parcel: Parcel)
                : this(parcel.readArray(Tetromino::class.java.classLoader)!! as Array<Array<Boolean>>)

        fun rotate(): Tetromino {
            return Tetromino(matrix.rotate())
        }

        fun createElement(sides: Float): GameElement {
            return Element(sides, this)
        }

        inline fun forEachSquare(action: (Int, Int) -> Unit) {
            for ((y, row) in matrix.withIndex()) {
                for ((x, p) in row.withIndex()) {
                    if (p) {
                        action(x, y)
                    }
                }
            }
        }

        val height: Int get() = matrix.size
        val width: Int get() = matrix.first().size

        private class Element(private val sides: Float, private val tetromino: Tetromino) : GameElement() {
            override val width: Float
                get() = sides * tetromino.width

            override val height: Float
                get() = sides * tetromino.height

            override var view: GameElementView? = null

            override fun createView(context: Context): GameElementView {
                return TetrominoView(context)
            }

            private inner class TetrominoView(context: Context) : GameElementView(context) {
                override val element: GameElement
                    get() = this@Element

                override fun onDraw(canvas: Canvas) {
                    tetromino.forEachSquare { left, top ->
                        val r = left + element.width
                        val b = top + element.height
                        canvas.drawRect(left.toFloat(), top.toFloat(), r, b, paint)
                    }
                }
            }

            override fun contains(point: Point): Boolean {
                tetromino.forEachSquare { left, top ->
                    val square = Rect(height, width).also {
                        it.x = left.toFloat()
                        it.y = top.toFloat()
                    }
                    if (point in square) return true
                }
                return false
            }
        }

        companion object CREATOR : Parcelable.Creator<Tetromino> {
            private inline fun <reified T> Array<Array<T>>.rotate(): Array<Array<T>> {
                if (isEmpty()) return emptyArray()
                return Array(first().size) { x ->
                    Array(size) { y ->
                        this[size - y - 1][x]
                    }
                }
            }

            override fun createFromParcel(parcel: Parcel): Tetromino {
                return Tetromino(parcel)
            }

            override fun newArray(size: Int): Array<Tetromino?> {
                return arrayOfNulls(size)
            }

            val I = Tetromino(
                arrayOf(arrayOf(true, true, true))
            )
            val J = Tetromino(
                arrayOf(
                    arrayOf(true, false, false),
                    arrayOf(true, true, true)
                )
            )
            val L = Tetromino(
                arrayOf(
                    arrayOf(false, false, true),
                    arrayOf(true, true, true)
                )
            )
            val O = Tetromino(
                arrayOf(
                    arrayOf(true, true),
                    arrayOf(true, true)
                )
            )
            val S = Tetromino(
                arrayOf(
                    arrayOf(false, true, true),
                    arrayOf(true, true, false)
                )
            )
            val T = Tetromino(
                arrayOf(
                    arrayOf(false, true, false),
                    arrayOf(true, true, true)
                )
            )
            val Z = Tetromino(
                arrayOf(
                    arrayOf(true, true, false),
                    arrayOf(false, true, true)
                )
            )

            val all = arrayOf(I, J, L, O, S, T, Z)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {

        }

        override fun describeContents(): Int {
            return 0
        }
    }

    private var squareSides: Float = -1.0f

    private fun initSquareSides() {
        squareSides = totalWidth / 10
    }

    private fun square() = Rect().apply {
        width = squareSides
        height = squareSides
    }

    private fun initTetronimos() {

    }

}
