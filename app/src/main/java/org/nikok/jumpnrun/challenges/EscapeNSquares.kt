package org.nikok.jumpnrun.challenges

import org.nikok.jumpnrun.core.Challenge
import org.nikok.jumpnrun.games.EscapeTheSquares

class EscapeNSquares(private val n: Int, name: String, override val credits: Int) :
    Challenge<EscapeTheSquares.Result>(name, "Escape $n squares in one run") {
    private var escapedSquares = 0

    override val progress: Int
        get() = escapedSquares

    override val max: Int
        get() = n
    override val achieved: Boolean
        get() = progress >= max

    override fun gamePlayed(result: EscapeTheSquares.Result) {
        escapedSquares = result.escapedSquares
    }
}