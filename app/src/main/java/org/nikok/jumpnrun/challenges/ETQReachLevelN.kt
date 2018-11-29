package org.nikok.jumpnrun.challenges

import org.nikok.jumpnrun.core.Challenge
import org.nikok.jumpnrun.games.EscapeTheSquares

class ETQReachLevelN(private val n: Int, name: String, override val credits: Int) :
    Challenge<EscapeTheSquares.Result>(name, "Reach level $n in escape the squares") {
    private var topLevel = 0

    override val progress: Int
        get() = topLevel

    override val max: Int get() = n

    override val achieved: Boolean
        get() = progress >= max

    override fun gamePlayed(result: EscapeTheSquares.Result) {
        if (result.level > topLevel) {
            topLevel = result.level
        }
    }
}