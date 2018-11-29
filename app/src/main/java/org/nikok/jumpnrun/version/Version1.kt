package org.nikok.jumpnrun.version

import org.nikok.jumpnrun.challenges.ETQReachLevelN
import org.nikok.jumpnrun.challenges.EscapeNSquares
import org.nikok.jumpnrun.challenges.KillNObstacles
import org.nikok.jumpnrun.challenges.TenPointsChallenge
import org.nikok.jumpnrun.core.JumpNRun
import org.nikok.jumpnrun.games.DummyGame
import org.nikok.jumpnrun.games.EscapeTheSquares
import org.nikok.jumpnrun.games.JetpackJoyride

object Version1 : Patch {
    override fun apply(jumpNRun: JumpNRun) {
        with(jumpNRun) {
            registerChallenges()
            registerGames()
        }
    }

    private fun JumpNRun.registerGames() {
        registerGame(EscapeTheSquares.FACTORY)
        registerGame(JetpackJoyride.FACTORY)
    }

    private fun JumpNRun.registerChallenges() {
        registerChallenge(DummyGame::class, TenPointsChallenge())
        registerChallenge(EscapeTheSquares::class, ETQReachLevelN(10, "Beginner", 100))
        registerChallenge(EscapeTheSquares::class, ETQReachLevelN(25, "Advanced", 1000))
        registerChallenge(EscapeTheSquares::class, EscapeNSquares(100, "Escaper", 100))
        registerChallenge(EscapeTheSquares::class, KillNObstacles(10, "Basic Square killer", 100))
        registerChallenge(EscapeTheSquares::class, KillNObstacles(25, "Advanced Square killer", 500))
        registerChallenge(EscapeTheSquares::class, KillNObstacles(100, "Expert Square killer", 1000))
    }
}