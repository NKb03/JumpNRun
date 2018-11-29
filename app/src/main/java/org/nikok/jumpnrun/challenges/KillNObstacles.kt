package org.nikok.jumpnrun.challenges

import org.nikok.jumpnrun.core.Challenge
import org.nikok.jumpnrun.games.EscapeTheSquares

class KillNObstacles(
    private val n: Int,
    name: String,
    override val credits: Int
) : Challenge<EscapeTheSquares.Result>(name, "Kill $n obstacles") {
    private var killedObstacles = 0

    override val progress: Int
        get() = killedObstacles

    override val max: Int
        get() = n

    override fun gamePlayed(result: EscapeTheSquares.Result) {
        killedObstacles += result.killedObstacles
    }
}