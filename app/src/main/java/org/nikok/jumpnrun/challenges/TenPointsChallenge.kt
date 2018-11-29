package org.nikok.jumpnrun.challenges

import org.nikok.jumpnrun.core.Challenge
import org.nikok.jumpnrun.games.DummyGame

class TenPointsChallenge : Challenge<DummyGame.Result>(
    NAME,
    DESCRIPTION
) {
    override var progress: Int = 0
    override val max: Int
        get() = 10
    override val achieved: Boolean
        get() = progress >= max

    override fun gamePlayed(result: DummyGame.Result) {
        println(result)
        progress = result.points
    }

    override val credits: Int
        get() = 1000

    companion object {
        private const val NAME = "Ten points"
        private const val DESCRIPTION = "Reach ten points in a game"
    }
}