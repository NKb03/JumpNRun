package org.nikok.jumpnrun.core

abstract class Challenge<in R : GameResult>(val name: String, val description: String) : Reward() {
    abstract val progress: Int

    abstract val max: Int

    open val achieved: Boolean get() = progress >= max

    protected abstract fun gamePlayed(result: R)

    fun playedGame(result: R) {
        if (!achieved) {
            gamePlayed(result)
        }
    }
}
